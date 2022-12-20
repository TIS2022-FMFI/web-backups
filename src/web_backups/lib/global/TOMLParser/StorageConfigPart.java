package web_backups.lib.global.TOMLParser;
import org.jetbrains.annotations.NotNull;

public final class StorageConfigPart {
    private final String remoteStorageAddress;
    private final String localStorageLocation;
    private final String remoteStorageLocation;

    public StorageConfigPart(@NotNull String remoteStorageAddress,
                             @NotNull String localStorageLocation,
                             @NotNull String remoteStorageLocation) {
        this.remoteStorageAddress = remoteStorageAddress;
        this.localStorageLocation = localStorageLocation;
        this.remoteStorageLocation = remoteStorageLocation;
    }
}
