package cn.ntboy.socket;

import cn.ntboy.http.HttpHandler;

import java.io.*;
import java.net.Socket;

public class SocketWorker implements Runnable{

    Socket client;

    public SocketWorker(Socket acceptSocket) {
        client = acceptSocket;
    }

    @Override
    public void run() {
        try {
            new HttpHandler(client);
            byte[] buf = new byte[1024];
            InputStream inputStream = client.getInputStream();
            int readed;
            readed = inputStream.read(buf);
            System.out.println(new String(buf, 0, readed));
            System.out.println(">>done<<");

            File file = new File("httphead.dat");
            System.out.println(file.getAbsolutePath());
            FileInputStream fin = new FileInputStream(file);

            OutputStream outputStream = client.getOutputStream();

            while ((readed = fin.read(buf)) != -1) {
                outputStream.write(buf, 0, readed);
            }
            fin.close();
            outputStream.close();
            inputStream.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
