package web_backups.main.ui.menuOptions;

import web_backups.lib.global.enums.HelpDetails;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static web_backups.lib.global.enums.HelpDetails.*;

/**
 * This class is used to print exact help call. <br>
 * i.e wb help will show commands and their meanings. <br>
 * wb help backup will show the meaning, flags, params, etc. for the backup command. <br>
 * */
public class Help {
    private static final int LINE_SIZE = 40;
    private static final Map<String, String> commandsMap;
    private static final String introMessage = "Help Options: \nCOMMANDS\n\n";
    private static final String EOL = "\n";
    static {
        LinkedHashMap<String, String> map = new LinkedHashMap();
        map.put("List", LIST_DETAILS.getDetails());
        map.put("Backup", BACKUP_DETAILS.getDetails());
        map.put("Restore", RESTORE_DETAILS.getDetails());
        map.put("Restore File", RESTORE_FILE_DETAILS.getDetails());
        map.put("Enable", ENABLE_DETAILS.getDetails());
        map.put("Disable", DISABLE_DETAILS.getDetails());
        map.put("Set", SET_DETAILS.getDetails());
        commandsMap = Collections.unmodifiableMap(map);
    }

    private static final Help INSTANCE = new Help();

    public Help() {

    }

    public static Help getInstance() {
        return INSTANCE;
    }

    private String calculateSpaceBlock(String command) {
        if (command.length() < LINE_SIZE) {
            return Stream.generate(() -> " ").limit(LINE_SIZE - command.length()).collect(Collectors.joining());
        }
        return "";
    }

    /**
     * This method is used as default HELP
     * */
    public void getDefault() {
        StringBuilder sb = new StringBuilder();
        sb.append(introMessage);
        for (Map.Entry<String, String> entry : commandsMap.entrySet()) {
            String command = entry.getKey();
            sb.append(command)
                    .append(calculateSpaceBlock(command))
                    .append(entry.getValue())
                    .append(EOL);
        }
        System.out.println(sb);
    }

    public void matchCodeByEnum(String code) {
        if (code == null) {
            return;
        }
        for (HelpDetails detail: Arrays.asList(LIST_DETAILS, BACKUP_DETAILS, RESTORE_DETAILS,
                RESTORE_FILE_DETAILS, ENABLE_DETAILS, DISABLE_DETAILS, SET_DETAILS)) {
            if (detail.getType().equals(code)) {
                System.out.println(detail.getSpecifiedDetail());
            }
        }
    }
}
