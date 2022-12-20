package web_backups.lib.global.TOMLParser;
import org.jetbrains.annotations.NotNull;

public final class RestoreConfigPart {
    private final String preRestoreScriptPath;
    private final String postRestoreScriptPath;

    public RestoreConfigPart(@NotNull String preRestoreScriptPath, @NotNull String postRestoreScriptPath) {
        this.preRestoreScriptPath = preRestoreScriptPath;
        this.postRestoreScriptPath = postRestoreScriptPath;
    }

    public String getPreRestoreScriptPath() {
        return preRestoreScriptPath;
    }

    public String getPostRestoreScriptPath() {
        return postRestoreScriptPath;
    }
}
