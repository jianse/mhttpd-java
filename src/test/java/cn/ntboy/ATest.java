package cn.ntboy;

import cn.ntboy.mhttpd.util.net.Poller;
import cn.ntboy.mhttpd.util.net.TestEndpoint;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

import static java.lang.Runtime.getRuntime;

public class ATest {

    @Test
    public void test() throws Exception {
        TestEndpoint endpoint = new TestEndpoint();
        endpoint.start();
        ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress(10080));
        ss.accept();
//        Thread.currentThread().wait(100000);
    }

    @Test
    public void envTest(){
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

    @Test
    public void socketOptionTest() throws IOException {
        ServerSocketChannel ss=ServerSocketChannel.open();
        ss.bind(new InetSocketAddress(10080));
        SocketChannel socketChannel = ss.accept();
        Set<SocketOption<?>> options = socketChannel.socket().supportedOptions();
        System.out.println(options);
    }

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ss=ServerSocketChannel.open();
        ss.bind(new InetSocketAddress(18080));
        SocketChannel socketChannel = ss.accept();
        SocketAddress localAddress =  socketChannel.getLocalAddress();

        System.out.println(localAddress);
        SocketAddress remoteAddress = socketChannel.getRemoteAddress();
//        System.out.println(remoteAddress.getHostString());
        System.out.println(remoteAddress);
    }




}
