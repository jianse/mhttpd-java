package cn.ntboy;

import cn.ntboy.config.IConfigManager;
import cn.ntboy.config.impl.ConfigManager;
import cn.ntboy.socket.Connector;
import cn.ntboy.socket.SocketWorker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main{
    public static void main(String[] args) throws Exception {
        //初始化配置管理器
        IConfigManager configManager = ConfigManager.getInstance();
        configManager.loadConfig(args);

        Connector httpConnector = new Connector();


    }
}
