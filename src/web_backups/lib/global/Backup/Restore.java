package web_backups.lib.global.Backup;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_backups.lib.global.TOMLParser.ConfigObject;
import web_backups.lib.global.exceptions.NoValidDataException;
import web_backups.lib.global.sftpConnection.Connection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static web_backups.lib.global.Constants.GlobalConstants.*;
import static web_backups.lib.global.enums.ExceptionMessage.FILE_NOT_FOUND;

public class Restore {

    private final Logger logger = LoggerFactory.getLogger(Backup.class);

    private static final Restore INSTANCE = new Restore();

    public static Restore getInstance() {
        return INSTANCE;
    }


    /**
     * @param config
     * @param backupId The backup zip name from the root site folder. i.e. full/fullBackup-xyz.7z on remote server
     */
    public void restore(ConfigObject config, String backupId, String destinationFolder) throws JSchException, SftpException {
        // the zip file with given backup Id is stored on the localserver site location
        logger.info("STARTING SITE RESTORE");

        retrieveZipFileFromRemoteServer(config, backupId);

        // restore process on local machine
        try {
            String executable = "7z x "
                    + config.getStorage().getLocalStorageLocation()
                    + PATH_DELIMITER.getText()
                    + backupId
                    + " -o"
                    + destinationFolder;
            Process process = Runtime.getRuntime().exec(executable);
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        removeTransferedZipFileLocally(config, backupId);
    }

    private void removeTransferedZipFileLocally(ConfigObject config, String archiveName) {
        String dst = config.getStorage().getLocalStorageLocation() + PATH_DELIMITER.getText() + archiveName;
        logger.info("DELETING ARCHIVE THAT HAS BEEN TRANSFERED TO LOCAL SERVER: " + dst);
        try {
            Process process = Runtime
                    .getRuntime()
                    .exec("rm - r " + dst);
            process.waitFor();
            logger.info("Successfully deleted.");
        } catch (IOException e) {
            logger.error("IO EXCEPTION! ", e);
        } catch (InterruptedException e) {
            logger.error("INTERRUPT EXCEPTION! ", e);
        }
    }

    public void restoreFiles(ConfigObject config, String backupId, String filePath, String destinationFolder) throws JSchException, SftpException, IOException, InterruptedException {
        logger.info("Restoring files for site name: " + config.getMain().getSiteId());

        retrieveZipFileFromRemoteServer(config, backupId);

        String inputZip = config.getStorage().getLocalStorageLocation()
                + PATH_DELIMITER.getText()
                + backupId;

        /* The zip is being retrieved from remote server and then extracted locally */
        File file = new File(filePath);
        if (!file.exists()) {
            logger.error(FILE_NOT_FOUND.getErrorMsg());
            throw new NoValidDataException(FILE_NOT_FOUND.getErrorMsg());
        }
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        ArrayList files = new ArrayList();

        String line;
        while ((line = br.readLine()) != null) {
            files.add(line);
        }

        br.close();
        fr.close();

        logger.info("Retrieving files from directory.");
        files.forEach(fileName -> {
            Process process = null;
            try {
                String executable = "7z x "
                        + config.getStorage().getLocalStorageLocation()
                        + PATH_DELIMITER.getText()
                        + backupId
                        + " "
                        + fileName
                        + " -o"
                        + destinationFolder;
                process = Runtime.getRuntime().exec(executable);
            } catch (IOException e) {
                logger.error("IO Exception", e);
                throw new RuntimeException(e);
            }
            try {
                process.waitFor();
                logger.info("File " + fileName + " retrieved");
            } catch (InterruptedException e) {
                logger.error("Interrupt exception", e);
                throw new RuntimeException(e);
            }
        });
        logger.info("Restore finished.");
    }

    private String getZipFilePath(ConfigObject config, String backupId) {
        return config.getStorage().getRemoteStorageLocation()
                + PATH_DELIMITER.getText()
                + MAIN_BACKUPS_FOLDER.getText()
                + PATH_DELIMITER.getText()
                + config.getMain().getSiteId()
                + PATH_DELIMITER.getText()
                + (backupId.contains(FOLDER_FULL.getText()) ? FOLDER_FULL.getText() : FOLDER_INCREMENTAL.getText())
                + PATH_DELIMITER.getText()
                + backupId
                ;
    }

    /**
     * Method retrieves the zip file of given site from config and backupId to the local server on local storage specified in the config file
     */
    private void retrieveZipFileFromRemoteServer(ConfigObject config, String backupId) throws JSchException, SftpException {

        String userName = config.getMain().getUsername();
        String remoteServAddr = config.getStorage().getRemoteStorageAddress();
        int port = 22;
        String pwd = config.getMain().getPassword();

        // connection
        logger.info("Creating connection");
        Connection connection = new Connection(userName, remoteServAddr, port, pwd);
        connection.connect();
        ChannelSftp sftpChannel = connection.getSftpChannel();

        String zipFilePath = getZipFilePath(config, backupId);

        logger.info("Retrieving file: " + zipFilePath);
        sftpChannel.get(zipFilePath, config.getStorage().getLocalStorageLocation() + PATH_DELIMITER.getText());

        logger.info("DISCONNECTING!");
        connection.disconnect();
    }

}
