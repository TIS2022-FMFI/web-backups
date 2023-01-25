package web_backups.lib.global.sftpConnection;

import com.jcraft.jsch.*;

public class Connection {
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
     *  ancestor before call getSftpChannel
     * */
    public void connect() {
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(userName, host, port);
            session.setPassword(password);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            System.out.println("You have been connected");
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    public Session getSession() {
        return session;
    }

    public void disconnect() {
        if (channel.isConnected()) {
            channel.disconnect();
        }
        if (session.isConnected()) {
            session.disconnect();
        }
    }

    /**
     *
     * */
    public ChannelSftp getSftpChannel() throws JSchException {
        channel = this.getSession().openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        return sftpChannel;
    }
}