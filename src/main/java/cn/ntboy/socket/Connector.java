package cn.ntboy.socket;

import cn.ntboy.config.IConfigManager;
import cn.ntboy.config.impl.ConfigManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Connector{

    public Connector() throws IOException {

        IConfigManager configManager = ConfigManager.getInstance();
        String portStr = configManager.getConfigItem("port");
        int port = Integer.parseInt(portStr);
        ServerSocket httpServerSocket = new ServerSocket(port);

        ExecutorService threadPool = Executors.newFixedThreadPool(100);

        Socket acceptSocket = httpServerSocket.accept();
        SocketWorker worker = new SocketWorker(acceptSocket);
        threadPool.submit(worker);

    }
}
