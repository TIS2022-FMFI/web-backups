package web_backups.lib.global.enums;

/**
 *
 * */
public enum ExceptionMessage {
    INVALID_COMMAND("Not a Valid command"),
    INVALID_OPTION("This option is not valid in the scope.");

    private final String errorMsg;

    ExceptionMessage(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
