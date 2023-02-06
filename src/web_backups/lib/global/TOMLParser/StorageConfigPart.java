package web_backups.lib.global.TOMLParser;
import org.jetbrains.annotations.NotNull;

public final class StorageConfigPart {
    private final String remoteStorageAddress;
    private final String localStorageLocation;
    private final String remoteStorageLocation;
    private final String configFilesLocation;

    public StorageConfigPart(@NotNull String remoteStorageAddress,
                             @NotNull String localStorageLocation,
                             @NotNull String remoteStorageLocation,
                             @NotNull String configFilesLocation) {
        this.remoteStorageAddress = remoteStorageAddress;
        this.localStorageLocation = localStorageLocation;
        this.remoteStorageLocation = remoteStorageLocation;
        this.configFilesLocation = configFilesLocation;
    }

    public String getRemoteStorageAddress() {
        return remoteStorageAddress;
    }

    public String getLocalStorageLocation() {
        return localStorageLocation;
    }

    public String getRemoteStorageLocation() {
        return remoteStorageLocation;
    }

    public String getConfigFilesLocation() {
        return configFilesLocation;
    }
}
