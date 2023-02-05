package web_backups.main.ui.sites;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static web_backups.lib.global.Constants.GlobalConstants.PATH_DELIMITER;

public class ConfigFileUtils {

    private static final ConfigFileUtils INSTANCE = new ConfigFileUtils();
    private static final String ENABLED = "sites_enabled.txt";

    public static ConfigFileUtils getInstance() {
        return INSTANCE;
    }

    public void enable(String rootAddr, String siteName) {
        List<String> lines = readFile(rootAddr);
        if (!lines.contains(siteName)) {
            lines.add(siteName);
            writeFile(lines, rootAddr);
        }
    }

    public void disable(String rootAddr, String siteName) {
        List<String> lines = readFile(rootAddr);
        if (lines.contains(siteName)) {
            lines.remove(siteName);
            writeFile(lines, rootAddr);
        }
    }

    private List<String> readFile(String rootAddr) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(rootAddr + PATH_DELIMITER.getText() + ENABLED))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return lines;
    }

    private void writeFile(List<String> lines, String rootAddr) {
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(rootAddr + PATH_DELIMITER.getText() + ENABLED))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }

}
