package web_backups.main;

import web_backups.lib.global.exceptions.NoValidDataException;

import static web_backups.lib.global.exceptions.ExceptionMessage.TEST_FORMAT;

public class Main {
    public static void main(String[] args) {
//        throw new NoValidDataException(ExceptionMessage.TEST.getErrorMsg());
        throw new NoValidDataException(String.format(TEST_FORMAT.getErrorMsg(), "added String to format"));
    }
}
