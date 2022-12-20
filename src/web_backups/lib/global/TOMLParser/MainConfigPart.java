package web_backups.lib.global.TOMLParser;
import org.jetbrains.annotations.NotNull;

public final class MainConfigPart {
    private final String siteId;
    private final String adminEmailAddress;
    private final String localServerId;

    public MainConfigPart(@NotNull String siteId, @NotNull String adminEmailAddress, @NotNull String localServerId) {
        this.siteId = siteId;
        this.adminEmailAddress = adminEmailAddress;
        this.localServerId = localServerId;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getAdminEmailAddress() {
        return adminEmailAddress;
    }

    public String getLocalServerId() {
        return localServerId;
    }
}
