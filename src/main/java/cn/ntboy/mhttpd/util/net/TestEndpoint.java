package cn.ntboy.mhttpd.util.net;

import cn.ntboy.mhttpd.LifecycleException;
import cn.ntboy.mhttpd.LifecycleState;
import cn.ntboy.ProtocolHandler;
import lombok.Setter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TestEndpoint extends AbstractEndpoint<SocketChannel,SocketChannel> {

//    private volatile boolean running=false;

    private ServerSocketChannel serverSock=null;
    private boolean paused = true;
    @Setter
    private Executor executor = null;
    private InetAddress address;

    @Override
    protected void bind() throws Exception {
        serverSock=ServerSocketChannel.open();
        InetSocketAddress addr = new InetSocketAddress(getAddress(), getPort());
        serverSock.socket().bind(addr,getAcceptCount());
//        startInternal();

    }

    @Override
    public SocketChannel serverSocketAccept() throws Exception {
        return serverSock.accept();
    }

    @Override
    public boolean setSocketOptions(SocketChannel socket) {
        //process the connection
        ProtocolHandler protocolHandler = new ProtocolHandler();
        protocolHandler.setSocketChannel(socket);
        executor.execute(protocolHandler);
        return true;
    }

    @Override
    public void closeSocket(SocketChannel socket) {
        countDownConnection();
        try{
            socket.socket().close();
        }catch (IOException ioe) {
            //todo:do some log
        }

        try{
            socket.close();
        }catch (IOException ioe){
            //todo:do some log
        }
    }

    @Override
    protected void initInternal() throws LifecycleException {
        try {
            bind();
        } catch (Exception e) {
            e.printStackTrace();
            throw  new LifecycleException(e);
//            e.printStackTrace();
        }
    }

    @Override
    protected void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
        if(!running) {
            running =true;
            paused =false;

            if(getExecutor()==null){
                createExecutor();
            }

            startAcceptorThreads();
        }

//        while (running){
//            SocketChannel socketChannel = serverSock.accept();
//
//            ProtocolHandler protocolHandler = new ProtocolHandler();
//            protocolHandler.setSocketChannel(socketChannel);
//            executor.execute(protocolHandler);
////            protocolHandler.configSocketAndProcess(socketChannel);
//        }
    }

    @Override
    protected void stopInternal() throws LifecycleException {

    }

    @Override
    protected void destroyInternal() throws LifecycleException {

    }

    private void startAcceptorThreads() {
        int count = getAcceptorThreadCount();
        for(int i=0;i<count;i++){
            Acceptor<SocketChannel> acceptor = new Acceptor<>(this);
            String name = "Acceptor"+i;

            Thread t = new Thread(acceptor,name);
            t.start();
        }

    }

    private int getAcceptorThreadCount() {
        return 1;
    }

    private void createExecutor() {
        //todo: init executor
        executor = Executors.newFixedThreadPool(10);
//        executor =
    }


    private Executor getExecutor() {
        return executor;
    }

    private int getAcceptCount() {
        return 10000;
    }

    private int getPort() {
        return 8080;
    }

    private InetAddress getAddress() {
        return address;
    }

    public void closeServerSocketGraceful() {
        try {
            doCloseServerSocket();
        } catch (IOException e) {
            //todo:do some log
        }
    }

    private void doCloseServerSocket() throws IOException {
        if(serverSock!=null){
            // Close server socket
            serverSock.socket().close();
            serverSock.close();
        }
        serverSock = null;
    }
}
