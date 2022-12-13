package web_backups.lib.global.enums;

/**
 *
 * */
public enum ExceptionMessage {
    INVALID_COMMAND("Not a Valid command"),
    INVALID_OPTION("This option is not valid in the scope."),
    FILE_NOT_FOUND("The requested file does not exist! Please, check the path"),
    INVALID_SITE_DATA("No backups for given site name were found."),
    SITES_FOLDER_NOT_FOUND("The sites parent directory and/or sites_enabled/disabled folder does not exist"),
    INVALID_FLAG("No Valid Flag provided");

    private final String errorMsg;

    ExceptionMessage(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
