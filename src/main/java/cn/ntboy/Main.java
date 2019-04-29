package cn.ntboy;

import cn.ntboy.config.IConfigManager;
import cn.ntboy.config.impl.ConfigManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

public class Main {
    public static void main(String[] args) {

        IConfigManager configManager = ConfigManager.getInstance();
        configManager.loadConfig(args);
        Set<String> keySet = configManager.getKeySet();

        String portStr = configManager.getConfigItem("port");

        int port = Integer.parseInt(portStr);
        ServerSocket serverSocket =null;
        try {
            serverSocket= new ServerSocket(port);
            Socket acceptSocket = serverSocket.accept();
            InputStream inputStream = acceptSocket.getInputStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream));
            String line;
            while(!(line = reader.readLine()).equals("")){
                System.out.println(line);
            }
            System.out.println(">>done<<");

            File file = new File("httphead.dat");
            System.out.println(file.getAbsolutePath());
            FileInputStream fin=new FileInputStream(file);

            OutputStream outputStream = acceptSocket.getOutputStream();
            byte[] buf = new byte[1024];
            int readed;
            while ((readed = fin.read(buf))!=-1){
                outputStream.write(buf,0,readed);
            }



//            String str="<font color=\"red\">hello</font>";

        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
