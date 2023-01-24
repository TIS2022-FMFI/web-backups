package web_backups.lib.global.Backup;

import web_backups.lib.global.TOMLParser.ConfigObject;

public class Restore {

    private static final Restore INSTANCE = new Restore();


    public static Restore getInstance() {
        return INSTANCE;
    }

    public void restore(ConfigObject config) {

    }
}
