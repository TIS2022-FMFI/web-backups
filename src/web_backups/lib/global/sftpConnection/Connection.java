package web_backups.lib.global.sftpConnection;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connection {

    private final Logger logger = LoggerFactory.getLogger(Connection.class);

    private String userName;
    private String host;
    private int port;
    private String password;
    private Session session;
    private Channel channel;

    public Connection(String userName, String host, int port, String password) {
        this.userName = userName;
        this.host = host;
        this.port = port;
        this.password = password;
    }

    /**
     * ancestor before call getSftpChannel
     */
    public void connect() {
        try {
//            logger.debug("CREATING CONNECTION ");
            JSch jsch = new JSch();
            session = jsch.getSession(userName, host, port);
//            logger.debug("SETTING PASSWORD");
            session.setPassword(password);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
//            logger.debug("CONNECTED");

            System.out.println("You have been connected");
        } catch (JSchException e) {
//            logger.error("Connection failed", e);
        }
    }

    public Session getSession() {
        return session;
    }

    public void disconnect() {
        if (channel.isConnected()) {
//            logger.debug("disconnecting channel");
            channel.disconnect();
        }
        if (session.isConnected()) {
//            logger.debug("disconnecting session");
            session.disconnect();
        }
    }

    /**
     *
     */
    public ChannelSftp getSftpChannel() throws JSchException {
//        logger.debug("Creating SFTP Connection");
        channel = this.getSession().openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        final String retrieved = "SFTP Channel retrieved.";
        System.out.println(retrieved);
//        logger.debug(retrieved);
        return sftpChannel;
    }
}