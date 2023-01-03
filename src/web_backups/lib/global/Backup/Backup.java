package web_backups.lib.global.Backup;

import com.jcraft.jsch.*;
import web_backups.lib.global.TOMLParser.ConfigObject;
import web_backups.lib.global.exceptions.NoValidDataException;

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

    private static final String ZIP_INSTALLED_PATH = "/usr/bin/7z"; // the path of installed 7z, by default it is here.

    public static Backup getInstance() {
        return INSTANCE;
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

        if (config.getBackup().getIncludedPaths().isEmpty()) { // in default the main directory of the site with the * wildcard has to be set!
            throw new NoValidDataException(INVALID_CONFIG_FILE.getErrorMsg());
        }
        if (!config.getBackup().getExcludedPaths().isEmpty()) {
            executable += " " + config.getBackup().getExcludedPaths().stream().map(s -> "-x" + s).collect(Collectors.joining(" "));
        }

        Process process = Runtime
                .getRuntime()
                .exec(executable); // excluded files

        process.waitFor();
    }

    /**
     * This method transfers the created backup file from the remote server to the local server
     *
     * @param archiveName The name of the archive that is created
     * @param rootDir     The root directory of Local Server where the data are stored
     * @param sftpChannel The sftp connection
     * @param type        The type of folder to store the backup in.
     */
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
     * @param type   Defines the substring type in the name
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
     * @param type The type(flag) of backup
     * */
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

    // this method removes the backup if it exceeds the config date(s). Still TODO
    //
    private void cleanFullBackupsFolder(String localServerStorage, int amountToStore) {
        // if there is more files than amtToStore, remove the oldest one
        // TODO IMPL.
    }

}
