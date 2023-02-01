package web_backups.lib.global.TOMLParser;


import org.jetbrains.annotations.NotNull;

public final class MainConfigPart {
    private final String siteId;
    private final String adminEmailAddress;
    private final String localServerId;
    private final String username;
    private final String password;

    public MainConfigPart(@NotNull String siteId, @NotNull String adminEmailAddress,
                          @NotNull String localServerId, @NotNull String username, @NotNull String password) {
        this.siteId = siteId;
        this.adminEmailAddress = adminEmailAddress;
        this.localServerId = localServerId;
        this.password = password;
        this.username = username;
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

    public String getUsername() { return username; }

    public String getPassword() {return password; }
}
