package web_backups.lib.global.Backup;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import web_backups.lib.global.TOMLParser.ConfigObject;
import web_backups.lib.global.exceptions.NoValidDataException;
import web_backups.lib.global.sftpConnection.Connection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static web_backups.lib.global.Constants.GlobalConstants.*;
import static web_backups.lib.global.enums.ExceptionMessage.FILE_NOT_FOUND;

public class Restore {

    private static final Restore INSTANCE = new Restore();

    public static Restore getInstance() {
        return INSTANCE;
    }


    /**
     * @param config
     * @param backupId The backup zip name from the root site folder. i.e. full/fullBackup-xyz.7z on remote server
     * @param siteName
     */
    public void restore(ConfigObject config, String siteName, String backupId) throws JSchException, SftpException {
        // the zip file with given backup Id is stored on the localserver site location
        retrieveZipFileFromRemoteServer(config, siteName, backupId);

        // restore process on local machine
        try {
            Process process = Runtime.getRuntime().exec("7z x " + config.getStorage().getLocalStorageLocation());
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void restoreFiles(ConfigObject config, String siteName, String backupId, String filePath) throws JSchException, SftpException, IOException, InterruptedException {
        retrieveZipFileFromRemoteServer(config, siteName, backupId);
        String outputPath = config.getStorage().getLocalStorageLocation();
        String inputZip = backupId;

        /* Clarify whether the restore is being made from a remote server or not! If yes, adapt the config file with localAddress! */
        File file = new File(filePath);
        if (!file.exists()) {
            throw new NoValidDataException(FILE_NOT_FOUND.getErrorMsg());
        }
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        StringBuffer sb = new StringBuffer();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();
        fr.close();

        Process process = Runtime.getRuntime().exec("7z x -o" + outputPath + " " + inputZip + sb.toString());
        process.waitFor();

    }

    private String getZipFilePath(ConfigObject config, String storage, String siteName, String backupId, String folder) {
        return storage
                + PATH_DELIMITER.getText()
                + siteName
                + PATH_DELIMITER.getText()
                + MAIN_BACKUPS_FOLDER.getText()
                + folder
                + backupId;
    }

    /**
     * Method retrieves the zip file of given siteName and backupId to the local server on local storage specified in the config file
     * */
    private void retrieveZipFileFromRemoteServer(ConfigObject config, String siteName, String backupId) throws JSchException, SftpException {
        /* This part is gonna be retrieved from the config file! */
        String userName = "";
        String remoteServAddr = config.getStorage().getRemoteStorageAddress();
        int port = 22;
        String pwd = "";

        // TODO: add UserName to the config

        Connection connection = new Connection(userName, remoteServAddr, port, pwd);
        connection.connect();
        ChannelSftp sftpChannel = connection.getSftpChannel();

        String folder = backupId.contains("full") ? FOLDER_FULL.getText() : FOLDER_INCREMENTAL.getText();
        String zipFilePath = getZipFilePath(config, config.getStorage().getRemoteStorageLocation(), siteName, backupId, folder);

        sftpChannel.get(zipFilePath, config.getStorage().getLocalStorageLocation());
        // logger.log
        connection.disconnect();
    }

}
