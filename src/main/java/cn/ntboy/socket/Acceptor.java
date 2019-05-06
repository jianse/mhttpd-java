package cn.ntboy.socket;

import cn.ntboy.server.Server;

import java.net.ServerSocket;
import java.net.Socket;

public class Acceptor implements Runnable{
    ServerSocket serverSocket;

    public Acceptor(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {

    }
}
