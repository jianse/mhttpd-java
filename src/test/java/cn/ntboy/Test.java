package cn.ntboy;

import cn.ntboy.mhttpd.LifecycleException;
import cn.ntboy.mhttpd.LifecycleState;
import cn.ntboy.mhttpd.util.net.AbstractEndpoint;
import cn.ntboy.mhttpd.util.net.Acceptor;
import cn.ntboy.mhttpd.util.net.TestEndpoint;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketOption;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static java.lang.Runtime.getRuntime;

public class Test {

    @org.junit.jupiter.api.Test
    void test() throws Exception {
        TestEndpoint endpoint = new TestEndpoint();
        endpoint.start();
        ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress(10080));
        ss.accept();
//        Thread.currentThread().wait(100000);
    }

    @org.junit.jupiter.api.Test
    void envTest(){
        try {
            String[] envp={"env_test=1234"};
            Process process = getRuntime().exec("/home/lenne/mhttpd/showenv",envp, Paths.get("/home/lenne/mhttpd/www").toFile());
            process.waitFor();
            InputStream stream = process.getInputStream();
            byte[] bytes = stream.readAllBytes();
            System.out.println(new String(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            //itr
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    void socketOptionTest() throws IOException {
        ServerSocketChannel ss=ServerSocketChannel.open();
        ss.bind(new InetSocketAddress(10080));
        SocketChannel socketChannel = ss.accept();
        Set<SocketOption<?>> options = socketChannel.socket().supportedOptions();
        System.out.println(options);
    }
}
