package web_backups.lib.global.Backup;

import com.jcraft.jsch.*;
import web_backups.lib.global.TOMLParser.ConfigObject;
import web_backups.lib.global.exceptions.NoValidDataException;
import web_backups.lib.global.sftpConnection.Connection;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static web_backups.lib.global.Constants.GlobalConstants.*;
import static web_backups.lib.global.enums.ExceptionMessage.INVALID_CONFIG_FILE;

public class Backup {

    private static final Backup INSTANCE = new Backup();
//    private static final Logger LOGGER = LoggerFactory.getLogger(Backup.class);;
    private static final String ZIP_INSTALLED_PATH = "/usr/bin/7z"; // the path of installed 7z, by default it is here.

    public static Backup getInstance() {
        return INSTANCE;
    }

    public void backupFiles(ConfigObject config, String type) throws JSchException {

        String backupType = "-i".equals(type) ? INCREMENTAL_TYPE_NAME.getText() : FULL_TYPE_NAME.getText();
        String archiveName = getArchiveNameByType(backupType, config);

        // TODO: ADD LOGGER

        /* Archive creation */
        try {
            createArchive(archiveName, config);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /* Archive transfer */
        transferArchiveToRemoteServer(config, archiveName, "-f".equals(type) ? FOLDER_FULL.getText() :
                FOLDER_INCREMENTAL.getText());

        /* remove the archive with given name afterwards when the backup is done */
        if (config.getBackup().getKeepOnLocalServer() != null && !config.getBackup().getKeepOnLocalServer()) {
            deleteArchive(archiveName);
        }
    }

    private void transferArchiveToRemoteServer(ConfigObject config, String archiveName, String folderType) throws JSchException {

        // TODO: add logger and separate those lines into new method

        /* This part is gonna be retrieved from the config file! */
        String userName = "";
        String remoteServAddr = config.getStorage().getRemoteStorageAddress();
        int port = 22;
        String pwd = "";

        // TODO: add UserName to the config

        Connection connection = new Connection(userName, remoteServAddr, port, pwd);
        connection.connect();
        ChannelSftp sftpChannel = connection.getSftpChannel();

        // TODO: traverse the FS to find the file

        /* It is expected that the remote storage location does contain the path delimiter in the end! */
        String destinationFolder = config.getStorage().getRemoteStorageLocation()
                + config.getMain().getSiteId()
                + PATH_DELIMITER.getText()
                + MAIN_BACKUPS_FOLDER.getText()
                + (FOLDER_FULL.getText().equals(folderType) ? FOLDER_FULL.getText() : FOLDER_INCREMENTAL.getText());

        // TODO ADD LOGGER HERE
        try {
            sftpChannel.put(destinationFolder, archiveName);
        } catch (SftpException e) {
            e.printStackTrace();
        }
        connection.disconnect();
    }

    private void deleteArchive(String archiveName) {
        // TODO: add logger
        try {
            Process process = Runtime
                    .getRuntime()
                    .exec("rm - r " + archiveName);
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
    private void createArchive(String archiveName, ConfigObject config) throws IOException, InterruptedException, NoValidDataException {
        // processes the backup
        String executable = ZIP_INSTALLED_PATH + " a -t7z " + archiveName + " "
                + String.join(" ", config.getBackup().getIncludedPaths());

        // in default the main directory of the site with the * wildcard has to be set! Mandatory field!
        if (config.getBackup().getIncludedPaths().isEmpty()) {
            throw new NoValidDataException(INVALID_CONFIG_FILE.getErrorMsg());
        }
        if (!config.getBackup().getExcludedPaths().isEmpty()) {
            executable += " " + config.getBackup().getExcludedPaths()
                    .stream()
                    .map(s -> "-x" + s)
                    .collect(Collectors.joining(" "));
        }

        Process process = Runtime
                .getRuntime() // vytvara process na pc, kde bezi ta app
                .exec(executable); // excluded files

        process.waitFor();
        // TODO: add log here

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
