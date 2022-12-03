package web_backups.lib.global.enums;

/**
 *
 * */
public enum ExceptionMessage {
    INVALID_COMMAND("Not a Valid command");

    private final String errorMsg;

    ExceptionMessage(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
