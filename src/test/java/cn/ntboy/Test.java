package cn.ntboy;

import cn.ntboy.mhttpd.LifecycleException;
import cn.ntboy.mhttpd.LifecycleState;
import cn.ntboy.mhttpd.util.net.AbstractEndpoint;
import cn.ntboy.mhttpd.util.net.Acceptor;
import cn.ntboy.mhttpd.util.net.TestEndpoint;
import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.SQLOutput;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
        System.
        Map<String, String> getenv = System.getenv();
        getenv.forEach((k,v)->{
            System.out.println(k+":"+v);
        });
    }


}
