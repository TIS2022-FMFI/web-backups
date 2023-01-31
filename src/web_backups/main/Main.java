package web_backups.main;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import web_backups.main.ui.AppRun;

import java.io.IOException;

/**
 * This method is used to run the CLI
 * DO NOT CHANGE THIS!
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, JSchException, SftpException {
        AppRun application = new AppRun();
        application.run();
//        String userName = "webbackup";
//        String hostUrl = "capek.ii.fmph.uniba.sk";
//        int port = 22;
//        String pwd = "Ondrej123";
//
//        Connection connection = new Connection(userName, hostUrl, port, pwd);
//        connection.connect();
//        TomlParser tomlParser = null;
//        try {
//            tomlParser = new TomlParser("testConfig.toml");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        ChannelSftp c = connection.getSftpChannel();
//
//        System.out.println("AWSGNJ");
//        Backup.getInstance().performManualBackup(tomlParser.getConfigObject(), connection.getSession(), "/home/", "-i");

    }
}
