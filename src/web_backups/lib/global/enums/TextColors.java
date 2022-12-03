package web_backups.lib.global.enums;

public enum TextColors {

    RESET("\u001B[0m"),
    SUCCESS("\u001B[32m"),
    ERROR("\u001B[31m"),
    BLUE("\033[0;34m");


    private final String color;

    TextColors(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
