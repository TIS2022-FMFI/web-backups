package web_backups.main;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import web_backups.lib.global.Backup.Backup;
import web_backups.lib.global.Backup.Restore;
import web_backups.lib.global.TOMLParser.TomlParser;
import web_backups.lib.global.sftpConnection.Connection;

import java.io.IOException;

import static web_backups.lib.global.Constants.GlobalConstants.PATH_DELIMITER;

/**
 * This method is used to run the CLI
 * DO NOT CHANGE THIS!
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, JSchException, SftpException {
//        AppRun application = new AppRun();
//        application.run();

        TomlParser tomlParser = null;
        try {
            tomlParser = new TomlParser("testConfig.toml");
        } catch (Exception e) {
            e.printStackTrace();
        }
//
//        ChannelSftp c = connection.getSftpChannel();
//
//        System.out.println("AWSGNJ");
//        Backup.getInstance().backupFiles(tomlParser.getConfigObject(), "-i");
//        Restore.getInstance().restore(tomlParser.getConfigObject(), "test-incrBackup2023-2-1.7z",
//                tomlParser.getConfigObject().getStorage().getLocalStorageLocation() + PATH_DELIMITER.getText());

        Restore.getInstance().restoreFiles(tomlParser.getConfigObject(),
                "test-incrBackup2023-2-1.7z",
                "/home/yarvelian/Desktop/web-backup/web-backups/rest.txt",
                tomlParser.getConfigObject().getStorage().getLocalStorageLocation() + PATH_DELIMITER.getText()
        );
    }
}
