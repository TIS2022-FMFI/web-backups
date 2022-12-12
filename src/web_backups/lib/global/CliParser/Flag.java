package web_backups.lib.global.CliParser;

/**
 * Flag can be type of Boolean or String.
 *
 * @see BooleanFlag
 * @see StringFlag
 */
public interface Flag<T> {

    String HYPHEN = "-";
    String DOUBLE_HYPHEN = "--";


    String getName();

    String getShortName();

    String getUsage();

    T getDefaultValue();

    T convert(String val);

    default boolean matches(String name) {
        return ("--" + getName()).equals(name) ||
                ("-" + getShortName()).equals(name);
    }

    static boolean isFlag(String name) {
        if (name != null) {
            return name.startsWith(HYPHEN) || name.startsWith(DOUBLE_HYPHEN);
        }
        return false;
    }


}
