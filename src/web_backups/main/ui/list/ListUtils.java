package web_backups.main.ui.list;

import web_backups.lib.global.enums.TextColors;
import web_backups.lib.global.exceptions.NoValidDataException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static web_backups.lib.global.enums.ExceptionMessage.*;

/**
 * Utility class for the list command <br>
 * <b>NOTE: DO NOT CHANGE THE FILE HIERARCHY! ABSOLUTE PATHS ARE BEING USED. </b>
 *
 * @author Filip Lukáč
 * @since 11.12.2022
 */
public class ListUtils {

    private static final String PATH_DELIMITER = "\\";
    private static final String INCREMENTAL = "\\incremental";
    private static final String FULL = "\\full";
    private static final String BACKUPS = "\\backups";
    private static final String SITES_ENABLED = "\\sites_enabled.txt";
    private static final String SITES_DISABLED = "\\sites_disabled.txt";
    private static final String ROOT_FOLDER_NAME = "web_backups";
    private static final String EXIT_MSG = "*** ALL FILES HAVE BEEN LISTED ***";

    private static final ListUtils INSTANCE = new ListUtils();

    public static ListUtils getInstance() {
        return INSTANCE;
    }

    /**
     * This method is used to print all backup data for each site stored in the site folder.
     *
     * @see #listBackupFromRootDir(String, String)
     */
    public void listBackups(String root, String flag, String siteName) throws IOException {
        // TODO CHECK SPECS
        // flag -e -> List enabled sites
        // flag -d -> List disabled sites

        if (!new File(root).exists()) {
            throw new NoValidDataException(FILE_NOT_FOUND.getErrorMsg());
        }

        if (!flag.isEmpty() && (siteName == null || siteName.isEmpty())) {
            if (!flag.equals("-i") && !flag.equals("-f") && !flag.equals("-b")) {
                throw new NoValidDataException(INVALID_FLAG.getErrorMsg());
            }
            List<Path> directories = Files.walk(Paths.get(root + BACKUPS), 1)
                    .filter(p -> Files.isDirectory(p) && !p.equals(Paths.get(root + BACKUPS)))
                    .collect(Collectors.toList());


            for (Path path : directories) {
                System.out.println(TextColors.ERROR.getColor() + "PERFORMING PRINTING OF SITE: "
                        + new File(path.toString()).getName() + TextColors.RESET.getColor());
                listBackupFromRootDir(path.toString(), flag.equals("-b") ? "" : flag);
            }
        } else { // requires the absolute path of the site.
            if (siteName == null || siteName.isEmpty()) {
                throw new NoValidDataException(SITES_FOLDER_NOT_FOUND.getErrorMsg());
            }
            listBackupFromRootDir(root + BACKUPS + PATH_DELIMITER + siteName, flag);
        }
    }

    /**
     * Method that lists all the files inside a given folder using BFS traversal
     *
     * @param dir  represents the absolute path to the listed site
     * @param flag represents the type of files to be listed (i.e. -i incremental backup, -f full backup, no flag = both)
     */
    private void listBackupFromRootDir(String dir, String flag) throws IOException { // To be replaced with Flag
        Queue<File> directories = new LinkedList<>();
        String myDir = dir;
        if (flag.equals("-i")) {
            myDir += INCREMENTAL;
        }
        if (flag.equals("-f")) {
            myDir += FULL;
        }

        File root = new File(myDir);
        if (!root.exists()) {
            throw new NoValidDataException(FILE_NOT_FOUND.getErrorMsg());
        }
        directories.add(root);

        System.out.println("LISTING SITE: " + root.getName() + " CREATION TIME: "
                + new Date(Files.readAttributes(Paths.get(dir), BasicFileAttributes.class).creationTime().to(TimeUnit.MILLISECONDS))
                + " TYPE: " + getType(flag)
        );

        while (!directories.isEmpty()) {
            File[] files = directories.poll().listFiles();
            if (files == null) {
                System.out.println(EXIT_MSG);
                return;
            }
            for (File f : files) {
                if (f.isDirectory()) {
                    directories.add(f);
                } else if (f.isFile()) {
                    performPrinting(f, root);
                }
            }
        }
        System.out.println(EXIT_MSG); // in case the root dir is empty
    }

    private String getType(String flag) {
        switch (flag) {
            case "-i":
                return "incremental";
            case "-f":
                return "full";
            default:
                return "incremental + full";
        }
    }

    private String getParent(File child, File root) {
        StringBuilder sb = new StringBuilder();
        File current = new File(child.getPath());
        while (!current.getName().equals(root.getName())) {
            sb.insert(0, "::");
            sb.insert(0, new File(current.getParent()).getName());
            current = new File(current.getParent());
        }
        return sb.toString();
    }

    /**
     * Method that lists the details of each site in the sites_enabled folder.
     *
     * @param root                Tbe web_backups folder path
     * @param localServerName     Local server name that is retrieved from the config
     * @param remoteServerAddress Address that the site is connected to, retrieved from config.
     * @see #listSite(String, String, String, String)
     */
    public void listSites(String root, String localServerName, String remoteServerAddress, String flag) throws IOException {

        File rootFile = new File(root);
        if (!rootFile.exists()) {
            throw new NoValidDataException(SITES_FOLDER_NOT_FOUND.getErrorMsg());
        }

        List<String> sites;
        try {
            sites = Files.readAllLines(Paths.get(rootFile.getPath() + getFlag(flag)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new NoValidDataException(FILE_NOT_FOUND.getErrorMsg());
        }

        for (String site : sites) {
            listSite(root, localServerName, remoteServerAddress, site);
        }

    }

    private String getFlag(String flag) {
        switch (flag) {
            case "-e":
                return SITES_ENABLED;
            case "-d":
                return SITES_DISABLED;
            default:
                if (!flag.isEmpty()) {
                    throw new NoValidDataException(INVALID_FLAG.getErrorMsg());
                }
                return "";
        }
    }

    /**
     * Method that lists the details given site. <br>
     * format of each output: site_name , local_server_name,  storage_server(IP or hostname),  date/time of last backup
     *
     * @param root                Tbe web_backups folder path
     * @param localServerName     Local server name that is retrieved from the config
     * @param remoteServerAddress Address that the site is connected to, retrieved from config.
     * @param siteName            The site name To be listed
     */
    private void listSite(String root, String localServerName, String remoteServerAddress, String siteName) throws IOException {
        File rootFile = new File(root);
        if (!rootFile.exists()) {
            throw new NoValidDataException(SITES_FOLDER_NOT_FOUND.getErrorMsg());
        }

        String lastBackupTime = getLastBackupTimeBySiteName(siteName, rootFile);

        System.out.printf(
                "Site Name: %s, Local Server Name: %s, Storage Server: %s, Last Backup: %s%n",
                siteName, localServerName, remoteServerAddress, lastBackupTime);
    }

    /**
     * Method that gets the last modified time of incremental backup (they appear every time)
     */
    private String getLastBackupTimeBySiteName(String siteName, File root) throws IOException {
        File[] files = new File(root.getPath() + BACKUPS + PATH_DELIMITER + siteName + INCREMENTAL + PATH_DELIMITER).listFiles();

        if (files == null) {
            throw new NoValidDataException(FILE_NOT_FOUND.getErrorMsg());
        }

        Date lastBackupDateTime = null;
        for (File file : files) {
            Date fileDate = new Date(
                    Files.readAttributes(Paths.get(file.getPath()), BasicFileAttributes.class)
                            .creationTime()
                            .to(TimeUnit.MILLISECONDS)
            );

            if (lastBackupDateTime == null || lastBackupDateTime.compareTo(fileDate) < 1) {
                lastBackupDateTime = fileDate;
            }
        }
        if (lastBackupDateTime == null) {
            throw new NoValidDataException(INVALID_SITE_DATA.getErrorMsg());
        }
        return lastBackupDateTime.toString();
    }

    private void performPrinting(File f, File root) {
        System.out.println(TextColors.SUCCESS.getColor() +
                getParent(f, root) +
                TextColors.BLUE.getColor() + f.getName() + TextColors.RESET.getColor()
        );
    }

}
