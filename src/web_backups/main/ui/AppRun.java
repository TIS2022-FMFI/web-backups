package web_backups.main.ui;

import web_backups.lib.global.exceptions.NoValidDataException;
import web_backups.main.ui.MenuOptions.Help;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import static web_backups.lib.global.enums.ExceptionMessage.INVALID_COMMAND;
import static web_backups.lib.global.enums.TextColors.*;

public class AppRun {
    private boolean isRunning = false;
    private static final String HELP = "wb help";

    /**
     * This method is used in app start or via user request by typing <b>wb help</b>
     */
    //TODO: the parser should be used here as well to differentiate whether to show only commands or just a command and it's flags, params, etc.
    private void showMenu(String command) {
        Help.getInstance().getDefault();
        // TODO: make other calls for each type!
        // i.e:
//        if (HELP.equals(command)) {
//            Help.getInstance().getDefault();
//            return;
//        }
    }

    /**
     * This method is used to run proper command from the list. <br>
     * i.e. provide backup, restore, etc. <br>
     * default should represent a warning/error based on the input
     */
    public void getOption(String command, String line) {
        try {
            switch (command) { // TODO: add commands here
                case HELP:
                    showMenu(line);
                    break;
                case "other":
                    break;
                case "wb exit":
                    System.out.println(SUCCESS.getColor() + "EXITING APPLICATION! " + RESET.getColor());
                    isRunning = false;
                    break;
                default:
                    System.out.println(ERROR.getColor() + INVALID_COMMAND + RESET.getColor());
                    break;
            }
        } catch (Exception e) {
            throw new NoValidDataException(INVALID_COMMAND.getErrorMsg()); // TODO CREATE EXCEPTION
        }
    }


    /**
     * This method handles the application run when started manually to be used via CLI.
     * Each line
     */
    public void run() throws IOException, InterruptedException {
        isRunning = true;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        showMenu(HELP);
        while (isRunning) {
            String newLine = br.readLine();
            if (newLine == null) {
                return;
            }
            parseCommand(newLine);
        }
    }

    /**
     * TODO line parsing. The functionality should be provided by a parser via separate module.
     * replace the getOption(line) by getOption(command, option) call with entering the command and option to be used.
     * i.e.
     */
    public void parseCommand(String line) throws InterruptedException {
        if (line.isEmpty()) {
            return;
        }
        String option = line; // TODO: CREATE PARSER FOR THIS! Should either return the parsed value or call the option.
        getOption(option, line);
        TimeUnit.MILLISECONDS.sleep(500); // testing purpose, will be removed.
    }

}
