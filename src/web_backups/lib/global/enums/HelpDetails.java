package web_backups.lib.global.enums;

public enum HelpDetails {
    LIST_DETAILS("list","NOT IMPLEMENTED YET",""),
    BACKUP_DETAILS("backup", "NOT IMPLEMENTED YET", ""),
    RESTORE_DETAILS("restore", "NOT IMPLEMENTED YET", ""),
    RESTORE_FILE_DETAILS("file restore", "NOT IMPLEMENTED YET", ""),
    ENABLE_DETAILS("enable", "NOT IMPLEMENTED YET", ""),
    DISABLE_DETAILS("disable", "NOT IMPLEMENTED YET", ""),
    SET_DETAILS("set", "NOT IMPLEMENTED YET", "");

    private final String type;
    private final String details;
    private final String specifiedDetail;

    HelpDetails(String type, String details, String specifiedDetail) {
        this.type = type;
        this.details = details;
        this.specifiedDetail = specifiedDetail;
    }

    public String getDetails() {
        return details;
    }

    public String getType() {
        return type;
    }

    public String getSpecifiedDetail() {
        return specifiedDetail;
    }
}
