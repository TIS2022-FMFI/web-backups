package web_backups.lib.global.exceptions;

public class ReqExcp extends RexExcp {
    private static final long serialVersionUID = 1L;


    public ReqExcp(Throwable cause) {
        super(cause);
    }

    public ReqExcp(String msg) {
        super(msg);
    }

    public ReqExcp(String msg, Throwable cause) {
        super(msg, cause);
    }
}
