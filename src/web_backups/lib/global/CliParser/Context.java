package web_backups.lib.global.CliParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Context is used to store the result of parsing.
 */
public final class Context {

    private final Parser parser;
    private final Command command;
    private final Map<Flag, String> flags;
    //private final String arg;
    private final Map<Integer, CommandArgument> args;
    public CommandArgument getArg(Integer position) {
        return args.get(position);
    }
    public Map<Integer, CommandArgument> getArgs() { return args; }

    public <T> T getFlagValue(Flag<T> flag) {
        String valStr = flags.get(flag);
        if (valStr != null) {
            return flag.convert(valStr);
        }
        return flag.getDefaultValue();
    }


    public Map<String, String> getFlagValues() {
        return flags.entrySet().stream()
                .map(entry -> new HashMap.SimpleEntry<>(entry.getKey().getShortName(), entry.getValue()))
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
    }

    boolean hasCommand() {
        return command != null;
    }

    private Context(Parser parser, Command command, Map<Integer, CommandArgument> args, Map<Flag, String> flags) {
        this.parser = parser;
        this.command = command;
        this.args = args;
        this.flags = flags;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Context{");
        sb.append("app=").append(parser);
        sb.append(", command=").append(command);
        sb.append(", flags=").append(flags);
        sb.append(", args='").append(args).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static ContextBuilder builder() {
        return new ContextBuilder();
    }

    public final static class ContextBuilder {

        private Parser parser;

        private Map<Flag, String> flags = new HashMap<>();
        private Map<Integer, CommandArgument> args = new HashMap<Integer, CommandArgument>();
        private Command command;
        private String arg;

        private ContextBuilder() {
        }

        public ContextBuilder setApp(Parser parser) {
            this.parser = parser;
            return this;
        }

        public ContextBuilder setCommand(Command command) {
            this.command = command;
            return this;
        }

        public ContextBuilder setArg(String value) {
            this.arg = value;
            return this;
        }

        public ContextBuilder addArg(Integer position, CommandArgument value) {
            this.args.put(position, value);
            return this;
        }

//        public ContextBuilder addValue(Flag flag, String value) {
//            values.put(flag, value);
//            return this;
//        }

        public ContextBuilder addFlag(Flag flag, String value) {
            flags.put(flag, value);
            return this;
        }

        public Context build() {
            return new Context(parser, command, args, flags);
        }


    }
}
