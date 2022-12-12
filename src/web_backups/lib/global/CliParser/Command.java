package web_backups.lib.global.CliParser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Command can have flags and arguments.
 */
public final class Command {

    private final String name;
    private final String shortName;
    private final String usage;

    private final Boolean hasMutuallyExclusiveParameters;
    private final List<Flag> flags;
    //private Flag arg;
    private final Map<Integer, CommandArgument> args;
    private final Consumer<Context> executor;

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getUsage() {
        return usage;
    }

    public List<Flag> getFlags() {
        return flags;
    }

    public Boolean getHasMutuallyExclusiveParameters() {
        return hasMutuallyExclusiveParameters;
    }

    public Integer numberOfRequiredArgs() {
        Integer result = 0;
        for (CommandArgument cmdArg: args.values()) {
            if (cmdArg.getIsRequired() == true){
                result++;
            }
        }
        return result;
    }

    public CommandArgument getArg(Integer position) {
        // TODO: NULL?
        return args.get(position);
    }

    public Map<Integer, CommandArgument> getArgs() {
        return args;
    }

    public void execute(Context context) {
        executor.accept(context);
    }

    private Command(String name,
                    String shortName,
                    String usage,
                    Map<Integer, CommandArgument> args,
                    List<Flag> flags,
                    Consumer<Context> executor,
                    HelpPrinter<Command> helpPrinter,
                    Boolean hasMutuallyExclusiveParameters) {
        this.name = name;
        this.shortName = shortName;
        this.usage = usage;
        this.flags = flags;
        this.args = args;
        this.hasMutuallyExclusiveParameters = hasMutuallyExclusiveParameters;
        this.executor = new HelpPrinterExecutor(helpPrinter, this, executor);
    }

    boolean matches(String name) {
        return name.equals(getName()) ||
                name.equals(getShortName());
    }

    public static CommandBuilder builder() {
        return new CommandBuilder();
    }

    public final static class CommandBuilder {
        private String name;
        private String shortName;
        private String usage;

        private Boolean hasMutuallyExclusiveParameters;
        private List<Flag> flags = new LinkedList<>();
        private Map<Integer, CommandArgument> args = new HashMap<>();
        private Consumer<Context> executor;
        //private Flag arg;
        private HelpPrinter<Command> helpPrinter = HelpPrinter.HELP_PRINTER_CMD;

        public CommandBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public CommandBuilder setShortName(String shortName) {
            this.shortName = shortName;
            return this;
        }

        public CommandBuilder setUsage(String usage) {
            this.usage = usage;
            return this;
        }

        public CommandBuilder addArg(Integer position, CommandArgument arg) {
            this.args.put(position, arg);
            return this;
        }

        public CommandBuilder addFlag(Flag flag) {
            this.flags.add(flag);
            return this;
        }

        public CommandBuilder setExecutor(Consumer<Context> executor) {
            this.executor = executor;
            return this;
        }

        public CommandBuilder setHasMutuallyExclusiveParameters(Boolean hasMutuallyExclusiveParameters) {
            this.hasMutuallyExclusiveParameters = hasMutuallyExclusiveParameters;
            return this;
        }

        public CommandBuilder setHelpPrinter(HelpPrinter<Command> helpPrinter) {
            this.helpPrinter = helpPrinter;
            return this;
        }

        public Command build() {
            return new Command(name, shortName, usage, args, flags, executor, helpPrinter, hasMutuallyExclusiveParameters);
        }
    }

    private final static class HelpPrinterExecutor implements Consumer<Context> {

        private final HelpPrinter<Command> helpPrinter;
        private final Consumer<Context> delegate;
        private final Command command;

        public HelpPrinterExecutor(HelpPrinter<Command> helpPrinter, Command command, Consumer<Context> delegate) {
            this.helpPrinter = helpPrinter;
            this.delegate = delegate;
            this.command = command;
        }

        public void printHelp(){
            helpPrinter.print(command, System.out);
        }

        @Override
        public void accept(Context context) {
            if (context.getFlagValue(Parser.FLAG_HELP)) {
                printHelp();
                return;
            }
            delegate.accept(context);
        }
    }
}
