package web_backups.lib.global.enums;

public enum HelpDetails {
    LIST_DETAILS("NOT IMPLEMENTED YET"),
    BACKUP_DETAILS("NOT IMPLEMENTED YET"),
    RESTORE_DETAILS("NOT IMPLEMENTED YET"),
    RESTORE_FILE_DETAILS("NOT IMPLEMENTED YET"),
    ENABLE_DETAILS("NOT IMPLEMENTED YET"),
    DISABLE_DETAILS("NOT IMPLEMENTED YET"),
    SET_DETAILS("NOT IMPLEMENTED YET");


    private final String details;

    HelpDetails(String details) {
        this.details = details;
    }

    public String getDetails() {
        return details;
    }
}
