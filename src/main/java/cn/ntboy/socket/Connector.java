package cn.ntboy.socket;

import cn.ntboy.config.IConfigManager;
import cn.ntboy.config.impl.ConfigManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Connector{

    public Connector() throws IOException {

        IConfigManager configManager = ConfigManager.getInstance();
        String portStr = configManager.getConfigItem("port");
        int port = Integer.parseInt(portStr);
        ServerSocket httpServerSocket = new ServerSocket(port);

        System.out.println(httpServerSocket.getInetAddress().getLocalHost().getHostAddress()+":"+httpServerSocket.getLocalPort());

        String maxUser = configManager.getConfigItem("maxUser");
        int iMaxUser=Integer.parseInt(maxUser);
        ExecutorService threadPool = Executors.newFixedThreadPool(iMaxUser);
        while (true){
            Socket acceptSocket = httpServerSocket.accept();
            SocketWorker worker = new SocketWorker(acceptSocket);
            threadPool.submit(worker);
        }


    }
}
