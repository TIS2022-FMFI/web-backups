package web_backups.main;

import web_backups.main.ui.AppRun;

import java.io.IOException;

/**
 * This method is used to run the CLI
 * DO NOT CHANGE THIS!
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        AppRun application = new AppRun();
        application.run();
    }
}
