package cn.ntboy;

import cn.ntboy.config.IConfigManager;
import cn.ntboy.config.impl.ConfigManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main{
    public static void main(String[] args) throws Exception {

        IConfigManager configManager = ConfigManager.getInstance();
        configManager.loadConfig(args);

        String portStr = configManager.getConfigItem("port");

        int port = Integer.parseInt(portStr);
        ServerSocket serverSocket;

        serverSocket = new ServerSocket(port);

        ExecutorService threadPool = Executors.newFixedThreadPool(100);

        while (true) {
            Socket acceptSocket = serverSocket.accept();
            Runnable runnable = () -> {
                try {
                    byte[] buf = new byte[1024];
                    InputStream inputStream = acceptSocket.getInputStream();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream));
                    String line;
                    int readed;
                    readed = inputStream.read(buf);
//                    new String(buf,0,readed);
                    System.out.println(new String(buf, 0, readed));

                    System.out.println(">>done<<");

                    File file = new File("httphead.dat");
                    System.out.println(file.getAbsolutePath());
                    FileInputStream fin = new FileInputStream(file);

                    OutputStream outputStream = acceptSocket.getOutputStream();

                    while ((readed = fin.read(buf)) != -1) {
                        outputStream.write(buf, 0, readed);
                    }
                    fin.close();
                    outputStream.close();
                    inputStream.close();
                    acceptSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            threadPool.submit(runnable);

        }

    }
}
