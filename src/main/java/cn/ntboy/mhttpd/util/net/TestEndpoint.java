package cn.ntboy.mhttpd.util.net;

import cn.ntboy.mhttpd.*;
import cn.ntboy.mhttpd.core.StandardThreadExecutor;
import cn.ntboy.mhttpd.protocol.ProtocolHandler;
import cn.ntboy.mhttpd.protocol.http.HttpRequest;
import cn.ntboy.processor.Processor;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.*;

public class TestEndpoint extends AbstractEndpoint<SocketChannel,SocketChannel> {
    private static final Logger logger = LogManager.getLogger(TestEndpoint.class);

//    private volatile boolean running=false;

    private ServerSocketChannel serverSock=null;
    private boolean paused = true;

    @Setter
    @Getter
    private Executor executor = null;
    private InetAddress address;

    private Poller poller = null;

    @Override
    protected void bind() throws Exception {
        serverSock=ServerSocketChannel.open();
        InetSocketAddress addr = new InetSocketAddress(getAddress(), getPort());
        logger.debug("prepare to bind addr:{} port:{}",getAddress(),getPort());
        serverSock.socket().bind(addr,getAcceptCount());
    }

    @Override
    public SocketChannel serverSocketAccept() throws Exception {
        return serverSock.accept();
    }

    @Override
    public boolean setSocketOptions(SocketChannel socket) {
        try {
            socket.configureBlocking(false);
//            NioChannel channel = new NioChannel();
//            channel.setIOChannel(socket);
//            poller.register(channel);
        } catch (IOException e) {
            logger.error("configBlocking error",e);
            e.printStackTrace();
        }
        try {
            socket.register(poller.getSelector(), SelectionKey.OP_READ,poller);

        } catch (ClosedChannelException e) {
            logger.error("reg to selector error",e);
            e.printStackTrace();
        }
//todo:move this code to another place
//        RequestParser requestParser = new RequestParser();
//        requestParser.setSocketChannel(socket);
//        requestParser.setEndpoint(this);
//        executor.execute(requestParser);

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

        if(executor==null){
            executor=new StandardThreadExecutor();
        }

        poller =new Poller();
        poller.setEndpoint(this);
    }

    @Override
    protected void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
        if(!running) {
            running =true;
            paused =false;

            poller.start();

            startAcceptorThreads();
        }

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
        //executor = Executors.newFixedThreadPool(10);
    }

    private int getAcceptCount() {
        return 10000;
    }

    @Setter
    @Getter
    private int port = -1;


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


    public void process(SelectionKey key){

//        logger.debug("ex  Name:{}",executor.getName());
        executor.execute(new Processor(key,getProtocolHandler().getConnector().getService().getContexts()));
    }


}
