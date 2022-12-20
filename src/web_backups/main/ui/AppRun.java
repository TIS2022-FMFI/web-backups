package web_backups.main.ui;

import web_backups.lib.global.CliParser.*;
import web_backups.lib.global.TOMLParser.TomlParser;
import web_backups.lib.global.exceptions.NoValidDataException;
import web_backups.main.ui.list.ListUtils;
import web_backups.main.ui.mailSender.MailSender;
import web_backups.main.ui.menuOptions.Help;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static web_backups.lib.global.enums.ExceptionMessage.INVALID_COMMAND;
import static web_backups.lib.global.enums.ExceptionMessage.INVALID_OPTION;
import static web_backups.lib.global.enums.TextColors.*;

public class AppRun {
    private boolean isRunning = false;
    private static final String HELP = "wb help";
    private static final String EXIT = "wb exit";
    private static final String ROOT = "C:\\Users\\Delta\\Desktop\\Programming\\AIN_Data\\TESTING"; // WILL BE REMOVED!

    /**
     * This method is used in app start or via user request by typing <b>wb help</b> and help options.
     *
     * @param command The input line to be used
     */
    private void showMenu(String command) {
        if (HELP.equals(command)) {
            Help.getInstance().getDefault();
            return;
        }

        Help.getInstance().matchCodeByEnum(command);
    }

    private Parser initializeParser() {
        List<Command> commandList = initializeCommands();
        Parser.ParserBuilder parserBuilder = Parser.builder()
                .setName("wb")
                .setUsage("Intelligent backup system for web servers");
        for (Command command : commandList) {
            parserBuilder.addCommand(command);
        }

        return parserBuilder.build();
    }

    private List<Command> initializeCommands() {
        List<Command> commandList = new ArrayList<>();
        Map<String, CommandArgument> args = initializeArguments();
        Map<String, BooleanFlag> flags = initializeFlags();
        commandList.add(Command.builder()
                .setName("list-backups")
                .setUsage("Output a list of backups. In default, it’ll list all backups from the local server. If the site name is set, only its backups will be listed.")
                .addArg(1, args.get("*[site_name]"))
                .addFlag(flags.get("-f"))
                .addFlag(flags.get("-i"))
                .addFlag(flags.get("-b"))
                .setExecutor(ctx -> {
                    try {
                        listBackups(ctx);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .build());
        commandList.add(Command.builder()
                .setName("list-sites")
                .setUsage("Output the list of all configured sites of all local servers that share storage server with this local server.")
                .addFlag(flags.get("-e"))
                .addFlag(flags.get("-d"))
                .setExecutor(ctx -> {
                    try {
                        listSites(ctx);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .build());
        commandList.add(Command.builder()
                .setName("backup")
                .setUsage("Manually runs the backup of a specific site")
                .addArg(1, args.get("[site_name]"))
                .addFlag(flags.get("-i"))
                .setExecutor(this::backup)
                .build());
        commandList.add(Command.builder()
                .setName("restore")
                .setUsage("Restores a specific site by writing its name and specifying the backup_id")
                .addArg(1, args.get("[site_name]"))
                .addArg(2, args.get("[backup_id]"))
                .setExecutor(this::restore)
                .build());
        commandList.add(Command.builder()
                .setName("restore-files")
                .setUsage("Restores specific file(s) (if exists) from a .txt file")
                .addArg(1, args.get("[file.txt]"))
                .addArg(2, args.get("[file_path]"))
                .setExecutor(this::restoreFiles)
                .build());
        commandList.add(Command.builder()
                .setName("enable")
                .setUsage("Enables site by adding or removing a link to the sites-enabled folder")
                .addArg(1, args.get("[site_name]"))
                .setExecutor(this::enable)
                .build());
        commandList.add(Command.builder()
                .setName("disable")
                .setUsage("Disables site by adding or removing a link to the sites-enabled folder ")
                .addArg(1, args.get("[site_name]"))
                .setExecutor(this::disable)
                .build());
        commandList.add(Command.builder()
                .setName("set-period")
                .setUsage("Sets the backup period (days)")
                .addArg(1, args.get("[period]"))
                .setExecutor(this::setPeriod)
                .build());
        commandList.add(Command.builder()
                .setName("set-switch")
                .setUsage("Whether to keep backups also on the local server. possible values 0/1 (Bool)")
                .addArg(1, args.get("[switch]"))
                .setExecutor(this::setSwitch)
                .build());
        commandList.add(Command.builder()
                .setName("auto")
                .setUsage("Checks all configs of locally enabled sites and performs backups if needed automatically as requested in site config files")
                .setExecutor(this::auto)
                .build());

        return commandList;
    }

    private Map<String, CommandArgument> initializeArguments() {
        Map<String, CommandArgument> args = new HashMap<>();
        args.put("[site_name]", (CommandArgument.builder()
                .setName("site_name")
                .setPosition(1)
                .setIsRequired(true)
                .build()));
        args.put("*[site_name]", (CommandArgument.builder()
                .setName("site_name")
                .setPosition(1)
                .setIsRequired(false)
                .build()));
        args.put("[backup_id]", (CommandArgument.builder()
                .setName("backup_id")
                .setPosition(2)
                .setIsRequired(true)
                .build()));
        args.put("[file.txt]", (CommandArgument.builder()
                .setName("file.txt")
                .setPosition(2)
                .setIsRequired(true)
                .build()));
        args.put("[file_path]", (CommandArgument.builder()
                .setName("file_path")
                .setPosition(2)
                .setIsRequired(true)
                .build()));
        args.put("[period]", (CommandArgument.builder()
                .setName("period")
                .setPosition(1)
                .setIsRequired(true)
                .build()));
        args.put("[switch]", (CommandArgument.builder()
                .setName("switch")
                .setPosition(1)
                .setIsRequired(true)
                .build()));

        return args;
    }

    private Map<String, BooleanFlag> initializeFlags() {
        Map<String, BooleanFlag> flags = new HashMap<>();
        //flags for "listBackups"
        // -i also for "backup"
        flags.put("-i", (BooleanFlag.builder()
                .setShortName("i")
                .setUsage("represents an incremental backup")
                .build()));
        flags.put("-f", (BooleanFlag.builder()
                .setShortName("f")
                .setUsage("represents a full backup")
                .build()));
        flags.put("-b", (BooleanFlag.builder()
                .setShortName("b")
                .setUsage("lists the backups that are kept on the server")
                //.setDefaultValue(true)
                .build()));
        //flags for "listSites"
        flags.put("-e", (BooleanFlag.builder()
                .setShortName("e")
                .setUsage("lists only enabled sites")
                .build()));
        flags.put("-d", (BooleanFlag.builder()
                .setShortName("d")
                .setUsage("lists only disabled sites")
                .build()));
        return flags;
    }


    private void backup(Context context) {
        StringBuffer buffer = new StringBuffer();
        Formatter formatter = new Formatter(buffer, Locale.US);
        String substr = "";
        Map<String, String> flags = context.getFlagValues();
        if (flags.get("i") != null) {
            substr = " incremental";
        }
        formatter.format("Make%s backup with site_name %s ", substr, context.getArg(1).getValue());

        System.out.println("" + formatter);
    }

    private void restore(Context context) {
        StringBuffer buffer = new StringBuffer();
        Formatter formatter = new Formatter(buffer, Locale.US);
        if (context.getArg(2) == null) {
            formatter.format("Make restore with site_name %s", context.getArg(1).getValue());
        } else {
            formatter.format("Make restore with site_name %s and backup_id %s", context.getArg(1).getValue(), context.getArg(2).getValue());
        }
        System.out.println("" + formatter);
    }

    private void listBackups(Context context) throws IOException {
        Map<String, String> enteredFlag = context.getFlagValues();
//        Map<Integer, CommandArgument> args = context.getArgs();

        // TODO: add validation for a single site
        if (enteredFlag.isEmpty()) {
            ListUtils.getInstance().listBackups(ROOT, "-b", "");
        }
        if (enteredFlag.containsKey("i")) {
            ListUtils.getInstance().listBackups(ROOT, "-i", "");
        }
        if (enteredFlag.containsKey("f")) {
            ListUtils.getInstance().listBackups(ROOT, "-f", "");
        }
        
    }

    private void listSites(Context context) throws IOException {
        Map<String, String> enteredFlag = context.getFlagValues();
        if (enteredFlag.isEmpty() || (enteredFlag.containsKey("e") && enteredFlag.get("e").equals("true"))) {
            ListUtils.getInstance().listSites(ROOT, "", "", "-e");
        }
        if (enteredFlag.containsKey("d") && enteredFlag.get("d").equals("true")) {
            ListUtils.getInstance().listSites(ROOT, "", "", "-d");
        }

    }

    private void restoreFiles(Context context) {
        System.out.println("execute!!!!");
    }

    private void enable(Context context) {
    }

    private void disable(Context context) {
    }

    private void setPeriod(Context context) {
    }

    private void setSwitch(Context context) {
    }

    private void auto(Context context) {
    }

    /**
     * This method is used to run proper command from the list. <br>
     * i.e. provide backup, restore, etc. <br>
     * default should represent a warning/error based on the input
     *
     * @param command The type of CLI command
     * @param line    user input
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
        //showMenu(HELP);
        Parser parser = initializeParser();

        TomlParser tomlParser;
        try {
            tomlParser = new TomlParser("testConfig.toml");
        } catch (Exception e) {
            System.out.println(ERROR.getColor() + e + RESET.getColor());
        }

        //Example of using ConfigObject:  tomlParser.getConfigObject().getBackup().getIncrementalBackupTime();

        while (isRunning) {
            String newLine = br.readLine();
            if (newLine == null) {
                return;
            }
            parser.execute(newLine.split(" "));
            //parseCommand(newLine); // TODO: Replace
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
