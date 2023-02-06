package web_backups.main;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.sun.security.auth.login.ConfigFile;
import web_backups.lib.global.Backup.BackupDaemon;
import web_backups.lib.global.TOMLParser.ConfigObject;
import web_backups.lib.global.TOMLParser.TomlParser;
import web_backups.lib.global.exceptions.NoValidDataException;
import web_backups.main.ui.AppRun;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static web_backups.lib.global.Constants.GlobalConstants.PATH_DELIMITER;
import static web_backups.lib.global.enums.ExceptionMessage.FILE_NOT_FOUND;

/**
 * This method is used to run the CLI
 * DO NOT CHANGE THIS!
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, JSchException, SftpException {
        AppRun application = new AppRun();
        application.run();
    }
}
