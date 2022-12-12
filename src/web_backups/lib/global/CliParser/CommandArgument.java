package web_backups.lib.global.CliParser;

import java.util.HashMap;
import java.util.Map;

public final class CommandArgument {
    private final String value;
    private final String name;
    private final Integer position;

    private final Boolean isRequired;

    private final String relatesToParam;


    public CommandArgument(String value, String name, Integer position, Boolean isRequired, String relatesToParam) {
        this.value = value;
        this.name = name;
        this.position = position;
        this.isRequired = isRequired;
        this.relatesToParam = relatesToParam;
    }


    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public Integer getPosition() {
        return position;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public String getRelatesToParam() { return relatesToParam; }

    public static CommandArgumentBuilder builder() { return new CommandArgumentBuilder(); }

    public static class CommandArgumentBuilder {
        private String value = "NULL";
        private String name;
        private Integer position;
        private Boolean isRequired;

        private String relatesToParam = "";
        public CommandArgumentBuilder() {
        }

        public CommandArgumentBuilder setValue(String value) {
            this.value = value;
            return this;
        }

        public CommandArgumentBuilder setRelatesToParam(String paramName) {
            this.relatesToParam = relatesToParam;
            return this;
        }

        public CommandArgumentBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public CommandArgumentBuilder setIsRequired(Boolean isRequired) {
            this.isRequired = isRequired;
            return this;
        }


        public CommandArgumentBuilder setPosition(Integer position) {
            this.position = position;
            return this;
        }

        public CommandArgument build() { return new CommandArgument(value, name, position, isRequired, relatesToParam); }
    }
}


