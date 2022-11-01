package web_backups.lib.global.exceptions;

public class RexExcp extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public RexExcp(Throwable cause) {
        super(cause);
    }

    public RexExcp(String msg) {
        super(msg);
    }

    public RexExcp(String msg, Throwable cause) {
        super(msg, cause);
    }
}
