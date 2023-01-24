package web_backups.lib.global.Backup;

import com.jcraft.jsch.*;
import web_backups.lib.global.TOMLParser.ConfigObject;
import web_backups.lib.global.exceptions.NoValidDataException;
import web_backups.lib.global.sftpConnection.Connection;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static web_backups.lib.global.enums.ExceptionMessage.INVALID_CONFIG_FILE;

public class Backup {

    private static final Backup INSTANCE = new Backup();
    private static final String INCREMENTAL_TYPE_NAME = "incrBackup";
    private static final String FULL_TYPE_NAME = "full";
    private static final String FOLDER_INCREMENTAL = "incremental";
    private static final String FOLDER_FULL = "full";
    private static final String PATH_DELIMITER = "\\";

    private static final String ZIP_INSTALLED_PATH = "/usr/bin/7z"; // the path of installed 7z, by default it is here.

    public static Backup getInstance() {
        return INSTANCE;
    }


    /**
     * @param rootDir Location of the root folder on remote server the data are kept on
     */
    public void backupFiles(ConfigObject config, String rootDir, String type) throws JSchException {

        String backupType = "-i".equals(type) ? INCREMENTAL_TYPE_NAME : FULL_TYPE_NAME;
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
        transferArchiveToRemoteServer(config, archiveName, "-f".equals(type) ? FOLDER_FULL : FOLDER_INCREMENTAL);

        /* remove the archive with given name afterwards when the backup is done */
        deleteArchive(archiveName);
    }

    private void transferArchiveToRemoteServer(ConfigObject config, String archiveName, String folderType) throws JSchException {

        // TODO: add logger and separate those lines into new method

        String userName = "";
        String remoteServAddr = config.getStorage().getRemoteStorageAddress();
        int port = 22;
        String pwd = "";

        // TODO: add UserName to the config
        //
        Connection connection = new Connection(userName, remoteServAddr, port, pwd);
        connection.connect();

        Channel channel = connection.getSession().openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;

        // TODO: traverse the FS to find the file

        String destinationFolder = config.getStorage().getRemoteStorageLocation() + PATH_DELIMITER + "backups"
                + config.getMain().getSiteId() + (FOLDER_FULL.equals(folderType) ? FOLDER_FULL : FOLDER_INCREMENTAL);

        // TODO ADD LOGGER HERE
        try {
            sftpChannel.put(destinationFolder, archiveName);
        } catch (SftpException e) {
            e.printStackTrace();
        }
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
     * This method proceeds the backup (both incremental and full (if needed)) of a site that is specified in the <br>
     * config file. Method is supposed to be used and run automatically by cron. <br>
     * Connection to the server before is needed!
     *
     * @param config  The configuration file of given site
     * @param session The session that is created by server connection. It is needed for sftp connection
     * @param rootDir The root directory of Local Server where the data are stored
     */
    @Deprecated
    public void performBackupFromConfig(ConfigObject config, Session session, String rootDir) throws JSchException, SftpException, IOException, InterruptedException {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;

        /* An incremental backup is being made since this method is run automatically from CRON. */
        String archiveName = getArchiveNameByType(INCREMENTAL_TYPE_NAME, config);

        createArchive(archiveName, config);

        // transfers the archive to the local server
        transferArchiveToLocalServer(archiveName, sftpChannel, rootDir, FOLDER_INCREMENTAL);

        /* full backup */
        // rename archive
        String fullBackupArchive = archiveName.replace(INCREMENTAL_TYPE_NAME, FULL_TYPE_NAME);
        Process process = Runtime.getRuntime().exec(
                archiveName + " mv " + fullBackupArchive);
        process.waitFor();

        // TODO: clean backups folder based on periods.
        // cleanFullBackupsFolder();
        transferArchiveToLocalServer(fullBackupArchive, sftpChannel, rootDir, FOLDER_FULL);

        sftpChannel.exit();
        session.disconnect();
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
     * This method transfers the created backup file from the remote server to the local server
     *
     * @param archiveName The name of the archive that is created
     * @param rootDir     The root directory of Local Server where the data are stored
     * @param sftpChannel The sftp connection
     * @param type        The type of folder to store the backup in.
     */
    @Deprecated
    private void transferArchiveToLocalServer(String archiveName, ChannelSftp sftpChannel,
                                              String rootDir, String type) throws SftpException {
        // local directory to be stored. i.e. /home/.../backups/siteName/type
        String localServerStorage = rootDir + type;
        // transfer
        sftpChannel.put(localServerStorage, archiveName);
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

    /**
     * This method is used when performing manual backups. Since there are two approaches of backing up <br>
     * data - One manual and one automatic via Cron, the manual does not make both backup types automatically.
     * Connection to the server before is needed!
     *
     * @param config  The configuration file of given site
     * @param session The session that is created by server connection. It is needed for sftp connection
     * @param rootDir The root directory of Local Server where the data are stored
     * @param type    The type(flag) of backup
     */
    public void performManualBackup(ConfigObject config, Session session, String rootDir, String type) throws JSchException, IOException, InterruptedException, SftpException {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;

        /* An incremental backup is being made since this method is run automatically from CRON. */
        String archiveName = getArchiveNameByType("-i".equals(type) ? INCREMENTAL_TYPE_NAME : FULL_TYPE_NAME, config);

        createArchive(archiveName, config);

        // transfers the archive to the local server
        transferArchiveToLocalServer(archiveName, sftpChannel, rootDir, "-i".equals(type) ? FOLDER_INCREMENTAL : FOLDER_FULL);

        sftpChannel.exit();
        session.disconnect();
    }

}
