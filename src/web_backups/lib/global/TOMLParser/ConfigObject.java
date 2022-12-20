package web_backups.lib.global.TOMLParser;

public final class ConfigObject {
    private final MainConfigPart main;
    private final BackupConfigPart backup;
    private final RestoreConfigPart restore;
    private final StorageConfigPart storage;

    public ConfigObject(MainConfigPart main, BackupConfigPart backup, RestoreConfigPart restore, StorageConfigPart storage) {
        this.main = main;
        this.backup = backup;
        this.restore = restore;
        this.storage = storage;
    }

    public MainConfigPart getMain() {
        return main;
    }

    public BackupConfigPart getBackup() {
        return backup;
    }

    public RestoreConfigPart getRestore() {
        return restore;
    }

    public StorageConfigPart getStorage() {
        return storage;
    }
}
