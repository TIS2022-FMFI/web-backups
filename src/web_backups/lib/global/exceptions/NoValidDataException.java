package web_backups.lib.global.exceptions;

public class NoValidDataException extends ReqExcp {
    private static final long serialVersionUID = 1L;

    public NoValidDataException(String msg) {
        super(msg);
    }

    // to differentiate exception causes
    public NoValidDataException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
