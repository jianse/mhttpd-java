package cn.ntboy.mhttpd.util.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Acceptor implements Runnable{

    Connector connector = null;

    public Connector getConnector() {
        return connector;
    }

    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    @Override
    public void run() {
        if (connector != null) {
            while (connector.isRunning()) {
                ServerSocket serverSocket = connector.getServerSocket();
                try {
                    Socket socket = serverSocket.accept();
                    connector.handleSocket(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
