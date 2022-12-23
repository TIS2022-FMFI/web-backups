package web_backups.lib.global.TOMLParser;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class BackupConfigPart {
    private final List<String> fullBackupPeriods;
    private final String incrementalBackupPeriod;
    private final List<String> incrementalBackupTime;
    private final Boolean keepOnLocalServer;
    private final String preBackupScriptPath;
    private final String postBackupScriptPath;
    private final List<String> includedPaths;
    private final List<String> excludedPaths;

    public BackupConfigPart(@NotNull List<String> fullBackupPeriods,
                            @NotNull String incrementalBackupPeriod,
                            @NotNull List<String> incrementalBackupTime,
                            @NotNull Boolean keepOnLocalServer,
                            @NotNull String preBackupScriptPath,
                            @NotNull String postBackupScriptPath,
                            @NotNull List<String> includedPaths,
                            @NotNull List<String> excludedPaths) {
        this.fullBackupPeriods = fullBackupPeriods;
        this.incrementalBackupPeriod = incrementalBackupPeriod;
        this.incrementalBackupTime = incrementalBackupTime;
        this.keepOnLocalServer = keepOnLocalServer;
        this.preBackupScriptPath = preBackupScriptPath;
        this.postBackupScriptPath = postBackupScriptPath;
        this.includedPaths = includedPaths;
        this.excludedPaths = excludedPaths;
    }

    public List<String> getFullBackupPeriods() {
        return fullBackupPeriods;
    }

    public String getIncrementalBackupPeriod() {
        return incrementalBackupPeriod;
    }

    public List<String> getIncrementalBackupTime() {
        return incrementalBackupTime;
    }

    public Boolean getKeepOnLocalServer() {
        return keepOnLocalServer;
    }

    public String getPreBackupScriptPath() {
        return preBackupScriptPath;
    }

    public String getPostBackupScriptPath() {
        return postBackupScriptPath;
    }

    public List<String> getIncludedPaths() {
        return includedPaths;
    }

    public List<String> getExcludedPaths() {
        return excludedPaths;
    }
}
