package web_backups.main.ui.sites;

public class SiteUtils {

    private static final SiteUtils INSTANCE = new SiteUtils();
    private static final String ENABLED = "sites_enabled.txt";
    private static final String DISABLED = "sites_disabled.txt";

    public static SiteUtils getInstance() {
        return INSTANCE;
    }

    private void enable(String rootAddr, String siteName) {

//        openFile();

    }

    private void disable(String rootAddr, String siteName) {

    }

}
