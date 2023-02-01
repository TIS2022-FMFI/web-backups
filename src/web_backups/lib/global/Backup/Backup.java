package web_backups.lib.global.Backup;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_backups.lib.global.TOMLParser.ConfigObject;
import web_backups.lib.global.TOMLParser.StorageConfigPart;
import web_backups.lib.global.exceptions.NoValidDataException;
import web_backups.lib.global.sftpConnection.Connection;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static web_backups.lib.global.Constants.GlobalConstants.*;
import static web_backups.lib.global.enums.ExceptionMessage.INVALID_CONFIG_FILE;

public class Backup {

    private final Logger logger = LoggerFactory.getLogger(Backup.class);

    private static final Backup INSTANCE = new Backup();
    private static final String ZIP_INSTALLED_PATH = "/usr/bin/7z"; // the path of installed 7z, by default it is here.

    public static Backup getInstance() {
        return INSTANCE;
    }

    public void backupFiles(ConfigObject config, String type) throws JSchException {
        String backupType = "-i".equals(type) ? INCREMENTAL_TYPE_NAME.getText() : FULL_TYPE_NAME.getText();
        logger.info("BACKING UP FILES WITH TYPE: " + backupType);
        String archiveName = getArchiveNameByType(backupType, config);

        /* Archive creation */
        String storageLocation = config.getStorage().getLocalStorageLocation()
                + PATH_DELIMITER.getText()
                + backupType
                + PATH_DELIMITER.getText();
        try {
            logger.info("Storage location is: " + storageLocation);
            createArchive(archiveName, config, storageLocation);
        } catch (IOException e) {
            logger.error("IO Exception!", e);
        } catch (InterruptedException e) {
            logger.error("Interrupted Exception! ", e);
        }

        /* Archive transfer */
        logger.info("Transfering archive to remote server");
        transferArchiveToRemoteServer(config, storageLocation, "-f".equals(type) ? FOLDER_FULL.getText() :
                FOLDER_INCREMENTAL.getText(), archiveName);

        /* remove the archive with given name afterwards when the backup is done */
        if (config.getBackup().getKeepOnLocalServer() != null && !config.getBackup().getKeepOnLocalServer()) {
            deleteArchive(storageLocation + archiveName);
        }
    }

    private void transferArchiveToRemoteServer(ConfigObject config, String archivePath, String folderType, String archiveName) throws JSchException {

        // TODO: add logger and separate those lines into new method

        /* This part is gonna be retrieved from the config file! */
        String userName = config.getMain().getUsername();
        String remoteServAddr = config.getStorage().getRemoteStorageAddress();
        int port = 22;
        String pwd = config.getMain().getPassword();

        logger.info("Creating connection");
        Connection connection = new Connection(userName, remoteServAddr, port, pwd);
        connection.connect();
        ChannelSftp sftpChannel = connection.getSftpChannel();

        // TODO: traverse the FS to find the file

        /* It is expected that the remote storage location does contain the path delimiter in the end! */
        logger.info("Destination folder: ");
        String destinationFolder = config.getStorage().getRemoteStorageLocation()
                + PATH_DELIMITER.getText()
                + config.getMain().getSiteId()
                + PATH_DELIMITER.getText()
                + MAIN_BACKUPS_FOLDER.getText()
                + PATH_DELIMITER.getText()
                + (FOLDER_FULL.getText().equals(folderType) ? FOLDER_FULL.getText() : FOLDER_INCREMENTAL.getText())
                + PATH_DELIMITER.getText()
                + archiveName;
        logger.info(destinationFolder);
        try {
            sftpChannel.put(archivePath + archiveName, destinationFolder);
            logger.info("Transfer successfully done.");
        } catch (SftpException e) {
            logger.error("Transfer aborted ", e);
            e.printStackTrace();
        }
        connection.disconnect();
    }

    private void deleteArchive(String archiveLocation) {
        logger.info("DELETING ARCHIVE: " + archiveLocation);
        try {
            Process process = Runtime
                    .getRuntime()
                    .exec("rm - r " + archiveLocation);
            process.waitFor();
            logger.info("Successfully deleted.");
        } catch (IOException e) {
            logger.error("IO EXCEPTION! ", e);
        } catch (InterruptedException e) {
            logger.error("INTERRUPT EXCEPTION! ", e);
        }
    }

    /**
     * This method is used to create an archive with provided archive name. It uses the configuration file to retrieve <br>
     * included and excluded paths even if the backup is run manually. By default, if the user wants to back up all files <br>
     * from the remote server, the source directory has to be in included lists containing /* wildcard
     *
     * @param archiveName The name of the archive that is created
     * @param config      The configuration file of given site
     */
    private void createArchive(String archiveName, ConfigObject config, String storageLocation) throws IOException, InterruptedException, NoValidDataException {
        // processes the backup
        logger.info("Creating archive: ");
        String executable = ZIP_INSTALLED_PATH + " a -t7z " + archiveName + " "
                + String.join(" ", config.getBackup().getIncludedPaths()
        );

        // in default the main directory of the site with the * wildcard has to be set! Mandatory field!
        if (config.getBackup().getIncludedPaths().isEmpty()) {
            logger.error(INVALID_CONFIG_FILE.getErrorMsg());
            throw new NoValidDataException(INVALID_CONFIG_FILE.getErrorMsg());
        }
        if (!config.getBackup().getExcludedPaths().isEmpty()) {
            executable += " " + config.getBackup().getExcludedPaths()
                    .stream()
                    .map(s -> "-x" + s)
                    .collect(Collectors.joining(" "));
        }

        logger.info("Starting process");
        Process process = Runtime
                .getRuntime() // vytvara process na pc, kde bezi ta app
                .exec(executable); // excluded files
        logger.info("Backup file successfully created");
        process.waitFor();

        // move zip to desired directory
        String move = "mv " + archiveName + " " + storageLocation;
        logger.info("Moving zip file to destination folder");
        logger.info(storageLocation);
        Process proc = Runtime.getRuntime().exec(move);
        proc.waitFor();
        logger.info("Moving completed");

    }

    /**
     * This method creates the archive name based on the specification.
     *
     * @param type   Defines the substring type in the final zip name that is stored on remote server
     * @param config The configuration file of given site
     */
    private String getArchiveNameByType(String type, ConfigObject config) {
        LocalDateTime currentTime = LocalDateTime.now();
        int day = currentTime.getDayOfMonth();
        int month = currentTime.getMonthValue();
        int year = currentTime.getYear();

        /* SiteName-Type-Date.7z */
        return config.getMain().getSiteId() + "-" + type + year + "-" + month + "-" + day + ".7z";
    }

}
