package web_backups.lib.global.Constants;

public enum GlobalConstants {

    INCREMENTAL_TYPE_NAME("incrBackup"),
    FOLDER_INCREMENTAL("incremental"),
    FOLDER_FULL("full"),
    FULL_TYPE_NAME("full"),
    MAIN_BACKUPS_FOLDER("backups"),
    PATH_DELIMITER("\\")
    ;

    private final String text;

    GlobalConstants(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
