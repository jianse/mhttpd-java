package cn.ntboy.mhttpd.util.net;

import cn.ntboy.mhttpd.LifecycleException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;

public class NioEndpoint extends AbstractEndpoint<NioChannel, SocketChannel>{

    private volatile ServerSocketChannel serverSocket = null;
    private int acceptorThreadCount=1;
    private int pollerThreadCount = Math.min(2,Runtime.getRuntime().availableProcessors());
    private SocketProperties socketProperties = new SocketProperties();

    private volatile CountDownLatch stopLatch = null;

    protected void setStopLatch(CountDownLatch stopLatch) {
        this.stopLatch = stopLatch;
    }

    private int acceptCount = 100;
    public void setAcceptCount(int acceptCount) { if (acceptCount > 0) this.acceptCount = acceptCount; }
    public int getAcceptCount() { return acceptCount; }

    @Override
    protected void bind() throws Exception{
        initServerSocket();

        if(acceptorThreadCount==0){
            acceptorThreadCount = 1;
        }
        if(pollerThreadCount<=0){
            pollerThreadCount = 1;
        }
        setStopLatch(new CountDownLatch(pollerThreadCount));
    }

    private void initServerSocket() throws IOException {
        serverSocket = ServerSocketChannel.open();
        socketProperties.setProperties(serverSocket.socket());
        InetSocketAddress addr = new InetSocketAddress(getAddress(),getPort());
        serverSocket.socket().bind(addr,getAcceptCount());
    }

    private int getPort() {
        return 0;
    }

    private String getAddress() {
        return null;
    }

    @Override
    public SocketChannel serverSocketAccept() throws Exception {
        return serverSocket.accept();
    }

    @Override
    public boolean setSocketOptions(SocketChannel socket) {
        return false;
    }

    @Override
    public void closeSocket(SocketChannel socket) {

    }

    @Override
    protected void initInternal() throws LifecycleException {

    }

    @Override
    protected void startInternal() throws LifecycleException {

    }

    @Override
    protected void stopInternal() throws LifecycleException {

    }

    @Override
    protected void destroyInternal() throws LifecycleException {

    }
}
