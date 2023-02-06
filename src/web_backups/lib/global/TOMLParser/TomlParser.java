package web_backups.lib.global.TOMLParser;

import com.electronwill.nightconfig.core.file.FileConfig;

import java.util.List;


public class TomlParser {

    private final ConfigObject configObject;

    public TomlParser(String configPath) {
        this.configObject = readConfig(configPath);
    }

    public ConfigObject getConfigObject() {
        return configObject;
    }

    private ConfigObject readConfig(String configPath) {
        FileConfig config = FileConfig.of(configPath);
        config.load();

        MainConfigPart main = readMainPart(config);
        BackupConfigPart backup = readBackupPart(config);
        RestoreConfigPart restore = readRestorePart(config);
        StorageConfigPart storage = readStoragePart(config);


        config.close();
        return new ConfigObject(main, backup, restore, storage);
    }

    private MainConfigPart readMainPart(FileConfig config) {
        String siteId = config.get("main.site_id");
        String name = config.get("main.admin_email_address");
        String localServerId = config.get("main.local_server_id");
        String username = config.get("main.username");
        String password = config.get("main.password");

        return new MainConfigPart(siteId, name, localServerId, username, password);
    }

    private BackupConfigPart readBackupPart(FileConfig config) {
        List<String> fullBackupPeriods = config.get("backup.full_backup_periods");
        String incrementalBackupPeriod = config.get("backup.incremental_backup_period");
        List<String> incrementalBackupTime = config.get("backup.incremental_backup_time");
        Boolean keepOnLocalServer = config.get("backup.keep_on_local_server");
        String preBackupScriptPath = config.get("backup.pre_backup_script");
        String postBackupScriptPath = config.get("backup.post_backup_script");
        List<String> includedPaths = config.get("backup.included_paths");
        List<String> excludedPaths = config.get("backup.excluded_paths");

        return new BackupConfigPart(fullBackupPeriods, incrementalBackupPeriod, incrementalBackupTime,
                keepOnLocalServer, preBackupScriptPath, postBackupScriptPath, includedPaths, excludedPaths);
    }

    private RestoreConfigPart readRestorePart(FileConfig config) {
        String preRestoreScriptPath = config.get("restore.pre_restore_script");
        String postRestoreScriptPath = config.get("restore.post_restore_script");

        return new RestoreConfigPart(preRestoreScriptPath, postRestoreScriptPath);
    }

    private StorageConfigPart readStoragePart(FileConfig config) {
        String remoteStorageAddress = config.get("storage.remote_storage_address");
        String localStorageLocation = config.get("storage.local_storage_location");
        String remoteStorageLocation = config.get("storage.remote_storage_location");
        String configFilesLocation = config.get("storage.config_files_location");

        return new StorageConfigPart(remoteStorageAddress, localStorageLocation, remoteStorageLocation, configFilesLocation);
    }
}
