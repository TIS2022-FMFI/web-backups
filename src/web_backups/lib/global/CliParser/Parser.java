package web_backups.lib.global.CliParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public final class Parser {

//    static final BooleanFlag FLAG_HELP = BooleanFlag.builder()
//            .setName("help")
//            .setShortName("h")
//            .setUsage("These are common commands used in various situations:")
//            .build();
//

    private final String name;
    private final String usage;
    private final List<Flag> flags;
    private final List<Command> commands;
    private final HelpPrinter<Parser> helpPrinter;

    private Parser(String name, String usage, List<Flag> flags, List<Command> commands, HelpPrinter<Parser> helpPrinter) {
        this.name = name;
        this.usage = usage;
        this.flags = flags;
        this.commands = commands;
        this.helpPrinter = helpPrinter;
    }

    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }

    public List<Flag> getFlags() {
        return flags;
    }

    public List<Command> getCommands() {
        return commands;
    }

    /**
     * Main function to parse program arguments.
     *
     * @param args Program arguments
     * @throws NullPointerException if args is null
     */
    public void execute(String args[]) {
        Objects.requireNonNull(args);
        Arguments arguments = new Arguments(args);
        if (arguments.isEmpty()) {
            helpPrinter.print(this, System.out);
            return;
        }
        Context.ContextBuilder contextBuilder = Context.builder().setApp(this);
        while (arguments.hasNext()) {
            String arg = arguments.next();
            if (!parseFlag(arg, arguments, contextBuilder, flags) &&
                    !parseCommand(arg, arguments, contextBuilder, flags)) {
                throw new IllegalArgumentException("Illegal argument " + arg);
            }
        }
        Context ctx = contextBuilder.build();
    }

    private boolean parseCommand(String arg,
                                 Arguments arguments,
                                 Context.ContextBuilder contextBuilder,
                                 List<Flag> flags) {
        Optional<Command> cmdOpt = commands.stream().filter(c -> c.matches(arg)).findFirst();
        if (cmdOpt.isPresent()) {
            Command cmd = cmdOpt.get();
            contextBuilder.setCommand(cmd);
            List<Flag> allFlags = concat(flags, cmd.getFlags());
            while (arguments.hasNext()) {
                String cmdArg = arguments.next();
                if (!parseFlag(cmdArg, arguments, contextBuilder, allFlags)) {
                    if (cmd.getArg() != null) {
                        contextBuilder.setArg(cmdArg);
                    } else {
                        throw new IllegalArgumentException("Unexpected argument " + cmdArg);
                    }
                }
            }
            cmd.execute(contextBuilder.build());
            return true;
        }
        return false;
    }

    private boolean parseFlag(String arg,
                              Arguments arguments,
                              Context.ContextBuilder contextBuilder,
                              List<Flag> flags) {
        if (Flag.isFlag(arg)) {
            Optional<Flag> flag = flags.stream().filter(f -> f.matches(arg)).findFirst();
            if (!flag.isPresent()) {
                throw new IllegalArgumentException("Unknown flag " + arg);
            }
            if (flag.get() instanceof BooleanFlag) {
                // boolean flag can be without the value
                contextBuilder.addValue(flag.get(), "true");
                return true;
            } else if (arguments.hasNext() && !Flag.isFlag(arguments.peek())) {
                // next is available and it is not flag
                contextBuilder.addValue(flag.get(), arguments.next());
                return true;
            }
            throw new IllegalArgumentException("Missing value for flag " + flag.get().getName());
        }
        return false;
    }

    private static <T> List<T> concat(List<T>... list) {
        return Stream.of(list).flatMap(Collection::stream).collect(Collectors.toList());
    }

    /**
     * Creates new instance of {@link ParserBuilder}.
     *
     * @return new AppBuilder instance.
     */
    public static ParserBuilder builder() {
        return new ParserBuilder();
    }

    public final static class ParserBuilder {
        private String name;
        private String usage;
        private List<Flag> flags = new ArrayList<>();
        private List<Command> commands = new ArrayList<>();
        private HelpPrinter<Parser> helpPrinter = HelpPrinter.HELP_PRINTER_APP;

        private ParserBuilder() {

        }

        public ParserBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public ParserBuilder setUsage(String usage) {
            this.usage = usage;
            return this;
        }

        public ParserBuilder addFlag(Flag flags) {
            this.flags.add(flags);
            return this;
        }

        public ParserBuilder addCommand(Command command) {
            this.commands.add(command);
            return this;
        }

        public ParserBuilder setHelpPrinter(HelpPrinter<Parser> helpPrinter) {
            this.helpPrinter = helpPrinter;
            return this;
        }

        public Parser build() {
            return new Parser(name, usage, flags, commands, helpPrinter);
        }
    }

}
