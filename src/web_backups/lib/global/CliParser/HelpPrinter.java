package web_backups.lib.global.CliParser;

import java.io.PrintStream;
import java.util.Formatter;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Interface to implement to provide help printers.
 */
@FunctionalInterface
public interface HelpPrinter<T> {

    void print(T target, PrintStream out);

    HelpPrinter<Parser> HELP_PRINTER_APP = (parser, out) -> {
        Formatter formatter = new Formatter(out);
        formatter.format("%s\n", parser.getUsage());
        formatter.format("\n");
        formatter.format("Usage:\n");
        formatter.format("\t%s [commands]\n", parser.getName());
        formatter.format("\n");
        formatter.format("Global app flags:\n");
        parser.getFlags().forEach(f -> {
            formatter.format("\t%-20s%s\n", concat(prefix("-", f.getShortName()), prefix("--", f.getName())), f.getUsage());
        });
        formatter.format("\n");
        formatter.format("Commands:\n");
        parser.getCommands().forEach(c -> {
            formatter.format("\t%-20s%s\n", concat(c.getShortName(), c.getName()), c.getUsage());
        });
    };

    HelpPrinter<Command> HELP_PRINTER_CMD = (cmd, out) -> {
        Formatter formatter = new Formatter(out);
        formatter.format("Name:\n");
        formatter.format("\t%s - %s\n", cmd.getName(), cmd.getUsage());
        formatter.format("Usage:\n");
        if (cmd.getFlags().isEmpty()) {
            formatter.format("\t%s [parameters...]\n", cmd.getName());
        } else {
            formatter.format("\t%s [parameters...] [flags]\n", cmd.getName());
            formatter.format("Flags:\n");
            cmd.getFlags().forEach(f -> {
                formatter.format("\t%-30s%s\n", concat(prefix("-", f.getShortName()), prefix("--", f.getName())), f.getUsage());
            });
        }
        if(!cmd.getArgs().isEmpty()) {
            formatter.format("Parameters:\n");
            cmd.getArgs().values().forEach(f -> {
                formatter.format("\t%s%-30s\n", f.getIsRequired() ? "": "(Optional) ", f.getName());
            });
        }
    };

    static String concat(String... str) {
        return Stream.of(str).filter(Objects::nonNull).collect(Collectors.joining(","));
    }

    static String prefix(String prefix, String s) {
        if (s != null) {
            return prefix + s;
        }
        return null;
    }
}
