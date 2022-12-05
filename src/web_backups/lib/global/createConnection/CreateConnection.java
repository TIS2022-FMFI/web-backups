package web_backups.lib.global.createConnection;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import web_backups.lib.global.exceptions.NoValidDataException;
import static web_backups.lib.global.enums.ExceptionMessage.INVALID_SESSION;


public class CreateConnection {
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final JSch jsch;

    public CreateConnection(String host, int port,String username,String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        jsch = new JSch();
    }

    public void connect() {
        try {
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
        } catch (JSchException e) {
            throw new NoValidDataException(INVALID_SESSION.getErrorMsg());
        }
    }
}