package web_backups.lib.global.exceptions;

/**
 *
 * */
public enum ExceptionMessage {
    TEST("Test error message"), // test
    TEST_FORMAT("Use string.format for string %s"); // test

    private final String errorMsg;

    ExceptionMessage(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
