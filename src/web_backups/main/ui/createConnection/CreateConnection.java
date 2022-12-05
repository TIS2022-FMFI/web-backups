package web_backups.main.ui.createConnection;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


public class CreateConnection {
    public static void connect(String username, String host,int port, String password) {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(15000);
            session.connect();
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        String username = "webbackup";
        String host = "capek.ii.fmph.uniba.sk";
        int port = 22;
        String password = "Ondrej123";
        connect(username,host,port,password);

    }

}
