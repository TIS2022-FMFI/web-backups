package web_backups.main;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import web_backups.lib.global.Backup.BackupDaemon;
import web_backups.lib.global.TOMLParser.TomlParser;
import web_backups.main.ui.AppRun;

import java.io.IOException;

/**
 * This method is used to run the CLI
 * DO NOT CHANGE THIS!
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, JSchException, SftpException {
//        AppRun application = new AppRun();
//        application.run();

        BackupDaemon.getInstance().runBackup(new TomlParser("testConfig.toml").getConfigObject());
    }
}
