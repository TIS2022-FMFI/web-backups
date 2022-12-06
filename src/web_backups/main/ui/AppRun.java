package web_backups.main.ui;

import web_backups.lib.global.CliParser.Command;
import web_backups.lib.global.CliParser.Parser;
import web_backups.lib.global.exceptions.NoValidDataException;
import web_backups.main.ui.mailSender.MailSender;
import web_backups.main.ui.menuOptions.Help;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static web_backups.lib.global.enums.ExceptionMessage.INVALID_COMMAND;
import static web_backups.lib.global.enums.ExceptionMessage.INVALID_OPTION;
import static web_backups.lib.global.enums.TextColors.*;

public class AppRun {
    private boolean isRunning = false;
    private static final String HELP = "wb help";
    private static final String EXIT = "wb exit";

    /**
     * This method is used in app start or via user request by typing <b>wb help</b> and help options.
     * @param command The input line to be used
     */
    private void showMenu(String command) {
        if (HELP.equals(command)) {
            Help.getInstance().getDefault();
            return;
        }

        Help.getInstance().matchCodeByEnum(command);
    }

    private Parser initializeParser(){
        List<Command> commandList = initializeCommands();
        Parser.ParserBuilder parserBuilder = Parser.builder()
                .setName("wb")
                .setUsage("Intelligent backup system for web servers");
        for (Command command : commandList) {
            parserBuilder.addCommand(command);
        }

        return parserBuilder.build();
    }

    private List<Command> initializeCommands(){
        List<Command> commandList = new ArrayList<>();
        commandList.add(Command.builder()
                .setName("list")
                .setUsage("NOT IMPLEMENTED YET")
                .build());
        commandList.add(Command.builder()
                .setName("backup")
                .setUsage("NOT IMPLEMENTED YET")
                .build());
        commandList.add(Command.builder()
                .setName("restore")
                .setUsage("NOT IMPLEMENTED YET")
                .build());
        commandList.add(Command.builder()
                .setName("fileRestore")
                .setUsage("NOT IMPLEMENTED YET")
                .build());
        commandList.add(Command.builder()
                .setName("enable")
                .setUsage("NOT IMPLEMENTED YET")
                .build());
        commandList.add(Command.builder()
                .setName("disable")
                .setUsage("NOT IMPLEMENTED YET")
                .build());
        commandList.add(Command.builder()
                .setName("set")
                .setUsage("NOT IMPLEMENTED YET")
                .build());
        commandList.add(Command.builder()
                .setName("help")
                .setUsage("NOT IMPLEMENTED YET")
                .setExecutor(ctx -> first(ctx.toString()))
                .build());

        return commandList;
    }

    private void first(String msg) {
        System.out.println(msg);
        showMenu(HELP);
    }

    /**
     * This method is used to run proper command from the list. <br>
     * i.e. provide backup, restore, etc. <br>
     * default should represent a warning/error based on the input
     * @param command The type of CLI command
     * @param line user input
     */
    public void getOption(String command, String line) {
        try {
            switch (command) { // TODO: add commands here
                case HELP:
                    showMenu(line);
                    break;
                case "other":
                    break;
                case EXIT:
                    System.out.println(SUCCESS.getColor() + "EXITING APPLICATION! " + RESET.getColor());
                    isRunning = false;
                    break;
                default:
                    System.out.println(ERROR.getColor() + INVALID_COMMAND + RESET.getColor());
                    break;
            }
        } catch (Exception e) {
            throw new NoValidDataException(INVALID_OPTION.getErrorMsg());
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
        //Parser parser = initializeParser();
        while (isRunning) {
            String newLine = br.readLine();
            if (newLine == null) {
                return;
            }
            //parser.execute(newLine.split(" "));
            parseCommand(newLine); // TODO: Replace
        }
    }

    /**
     * TODO line parsing. The functionality should be provided by a parser via separate module.
     */
    public void parseCommand(String line) throws InterruptedException {
        if (line.isEmpty()) {
            return;
        }
        String option = line; // TODO: CREATE PARSER FOR THIS! Should either return the parsed value or call the option.
        getOption(option, line);
        TimeUnit.MILLISECONDS.sleep(500); // testing purpose, will be removed.
    }

    private void sendMail(String from, String to, String host, String subject, String msg) {
        MailSender ms = new MailSender(from, to, host, subject, msg);
        ms.sendMail();
    }

}
