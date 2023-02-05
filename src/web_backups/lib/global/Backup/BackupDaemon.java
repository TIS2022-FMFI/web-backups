package web_backups.lib.global.Backup;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import web_backups.lib.global.TOMLParser.ConfigObject;
import web_backups.lib.global.sftpConnection.Connection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static web_backups.lib.global.Constants.GlobalConstants.*;
import static web_backups.lib.global.Constants.GlobalConstants.PATH_DELIMITER;

public class BackupDaemon {
    private static final BackupDaemon INSTANCE = new BackupDaemon();

    public static BackupDaemon getInstance() {
        return INSTANCE;
    }
    public void runBackup(ConfigObject config) throws JSchException, SftpException {

        List<String> stringPeriods = config.getBackup().getFullBackupPeriods();
        List<Integer> backupPeriods = stringPeriods.stream().mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
        Map<Integer, List<String>> intervalArchives = new HashMap<>();
        //List<Integer> backupPeriods = new ArrayList<>(){{add(7);}};
        for (Integer period : backupPeriods) {
            String destinationFolder = config.getStorage().getRemoteStorageLocation()
                    + PATH_DELIMITER.getText()
                    + config.getMain().getSiteId()
                    + PATH_DELIMITER.getText()
                    + MAIN_BACKUPS_FOLDER.getText()
                    + PATH_DELIMITER.getText()
                    + (FOLDER_FULL.getText())
                    + period
                    + PATH_DELIMITER.getText();

//            Backup.getInstance().backupFiles(config, "-f"); // TODO
            List<String> archiveNames = retrieveArchiveNamesFromServer(config, destinationFolder);
//            List<String> archiveNames = new ArrayList<>(){{
//                add("testSite-fullBackup2023-1-29.7z");
//                add("siteNam1-fullBackup2023-1-22.7z");
//            }};
            intervalArchives.put(period, archiveNames);
        }

        for (Map.Entry<Integer, List<String>> entry : intervalArchives.entrySet()) {
            List<String> archiveNames = entry.getValue();
            Integer period = entry.getKey();
            if (archiveNames.isEmpty()) {
                // create new backup for period
                continue;
            }

            if (!getArchivesToDelete(archiveNames, period).isEmpty()) {
                //System.out.println("Create new backup");
                String path = buildPathToBackupsDir(config, period);
                deleteAllBackupsInFolder(config, path);
                //create new archive
            }
        }
    }

    private List<String> retrieveArchiveNamesFromServer(ConfigObject config, String destinationFolder) throws JSchException, SftpException {
        List<String> archiveNames = new ArrayList<>();

        String userName = config.getMain().getUsername();
        String remoteServAddr = config.getStorage().getRemoteStorageAddress();
        int port = 22;
        String pwd = config.getMain().getPassword();

        Connection connection = new Connection(userName, remoteServAddr, port, pwd);
        connection.connect();
        ChannelSftp sftpChannel = connection.getSftpChannel();

        try {
            sftpChannel.cd(destinationFolder);
            // Directory exists.
        } catch (SftpException e) {
            //Directory does not exist.
            sftpChannel.mkdir(destinationFolder);
            connection.disconnect();
            return archiveNames;
        }

        for (Object file : sftpChannel.ls(".")) {
            ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) file;
            if (!entry.getAttrs().isDir() && entry.getFilename().endsWith(".7z")) {
                archiveNames.add(entry.getFilename());
            }
        }

        connection.disconnect();
        return archiveNames;
    }

    private List<String> getArchivesToDelete(List<String> archiveNames, int interval) {
        List<String> archives = new ArrayList<>();
        Calendar now = Calendar.getInstance();

        Pattern archiveNamePattern = Pattern.compile("[a-zA-Z0-9]+-fullBackup(\\d{4})-(\\d{1,2})-(\\d{1,2}).7z");

        for (String archiveName : archiveNames) {
            Matcher matcher = archiveNamePattern.matcher(archiveName);
            if (matcher.matches()) {
                int year = Integer.parseInt(matcher.group(1));
                int month = Integer.parseInt(matcher.group(2));
                int day = Integer.parseInt(matcher.group(3));

                Calendar archiveDate = Calendar.getInstance();
                archiveDate.set(year, month - 1, day);

                long diff = now.getTimeInMillis() - archiveDate.getTimeInMillis();
                long days = TimeUnit.MILLISECONDS.toDays(diff);
                System.out.println("Diff in days " + (days + 1));
                if (days + 1 >= interval) {
                    archives.add(archiveName);
                    System.out.println(archiveName + " was deleted");
                }
            }
        }

        return archives;
    }

    private Boolean deleteAllBackupsInFolder(ConfigObject config, String path) throws JSchException, SftpException {

        String userName = config.getMain().getUsername();
        String remoteServAddr = config.getStorage().getRemoteStorageAddress();
        int port = 22;
        String pwd = config.getMain().getPassword();

        Connection connection = new Connection(userName, remoteServAddr, port, pwd);
        connection.connect();
        ChannelSftp sftpChannel = connection.getSftpChannel();

        Vector<ChannelSftp.LsEntry> fileList = sftpChannel.ls(path);
        for (ChannelSftp.LsEntry file : fileList) {
            if (!file.getAttrs().isDir()) {
                sftpChannel.rm(path + "/" + file.getFilename());
            }
        }

        return sftpChannel.ls(path).isEmpty();
    }

    private String buildPathToBackupsDir(ConfigObject config, Integer period) {
        return config.getStorage().getRemoteStorageLocation()
                + PATH_DELIMITER.getText()
                + config.getMain().getSiteId()
                + PATH_DELIMITER.getText()
                + MAIN_BACKUPS_FOLDER.getText()
                + PATH_DELIMITER.getText()
                + (FOLDER_FULL.getText())
                + period
                + PATH_DELIMITER.getText();
    }
}