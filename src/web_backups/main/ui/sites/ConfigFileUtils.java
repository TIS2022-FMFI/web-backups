package web_backups.main.ui.sites;

import web_backups.lib.global.TOMLParser.ConfigObject;

public class ConfigFileUtils {

    private static final ConfigFileUtils INSTANCE = new ConfigFileUtils();
    private static final String ENABLED = "sites_enabled.txt";
    private static final String DISABLED = "sites_disabled.txt";

    public static ConfigFileUtils getInstance() {
        return INSTANCE;
    }

    private void enable(ConfigObject config, String rootAddr, String siteName) {

    }

    private void disable(ConfigObject config, String rootAddr, String siteName) {

    }

    public void setPeriod(ConfigObject config, String rootAddr, String period) {

    }

    public void setSwitch(ConfigObject config, String rootAddr, String period) {

    }

}
