package cn.ntboy.mhttpd.util.net;

import cn.ntboy.mhttpd.protocol.ProtocolHandler;
import cn.ntboy.mhttpd.LifecycleException;
import cn.ntboy.mhttpd.LifecycleState;
import cn.ntboy.mhttpd.Service;
import cn.ntboy.mhttpd.util.LifecycleBase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Connector extends LifecycleBase{
    protected int connectionTimeout = 20000;

    /**
     * The redirect port for non-SSL to SSL redirects.
     */
    protected int redirectPort = 443;

    /**
     * The secure connection flag that will be set on all requests received
     * through this connector.
     */
    protected boolean secure = false;

    private Charset uriCharset = StandardCharsets.UTF_8;

    protected String protocolHandlerClassName = null;
    protected ProtocolHandler protocolHandler = null;
    protected boolean running = false;
    private int port = 18080;
    private ServerSocket serverSocket = null;
    private Acceptor acceptor = null;
    private Service service = null;
    private Thread accepterTread = null;
    public Connector(String protocol) throws IOException {

        if ("HTTP/1.1".equals(protocol)) {
            protocolHandlerClassName = "cn.ntboy.mhttpd.protocol.http.Http11Protocol";
        } else {
            protocolHandlerClassName = protocol;
        }

        ProtocolHandler p = null;

        try {
            Class<?> clazz = Class.forName(protocolHandlerClassName);
            p = (ProtocolHandler) clazz.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.protocolHandler = p;
        }

//        IConfigManager configManager = ConfigManager.getInstance();
//        String portStr = configManager.getConfigItem("port");
//        int port = Integer.parseInt(portStr);
//        ServerSocket httpServerSocket = new ServerSocket(port);
//
//        System.out.println(httpServerSocket.getInetAddress().getLocalHost().getHostAddress() + ":" + httpServerSocket.getLocalPort());
//
//        String maxUser = configManager.getConfigItem("maxUser");
//        int iMaxUser = Integer.parseInt(maxUser);
//        ExecutorService threadPool = Executors.newFixedThreadPool(iMaxUser);
//        while (true) {
//            Socket acceptSocket = httpServerSocket.accept();
//            SocketWorker worker = new SocketWorker(acceptSocket);
//            threadPool.submit(worker);
//        }

    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getRedirectPort() {
        return redirectPort;
    }

    public void setRedirectPort(int redirectPort) {
        this.redirectPort = redirectPort;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isRunning() {
        return running;
    }

    public void handleSocket(Socket socket) throws IOException {
        //todo: invoke the method in protocolHandler to parse request and return response
        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        threadPool.submit(new Runnable(){
            @Override
            public void run() {

                try {
                    InputStream stream = socket.getInputStream();
                    byte[] bytes = new byte[1024];
                    int len = stream.read(bytes);
                    System.out.println(new String(bytes, 0, len));

                    OutputStream outputStream = socket.getOutputStream();
                    InputStream resource = getClass().getClassLoader().getResourceAsStream("httphead.dat");
                    while ((len = resource.read(bytes)) > 0) {
                        outputStream.write(bytes, 0, len);
                    }
                    socket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void initInternal() throws LifecycleException {
        try {
            ServerSocket serverSocket = new ServerSocket();
            SocketAddress addr = new InetSocketAddress(InetAddress.getByName("localhost"), port);
            serverSocket.bind(addr);
            this.serverSocket = serverSocket;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (acceptor == null) {
            acceptor = new Acceptor();
            acceptor.setConnector(this);
        } else {
            acceptor.setConnector(this);
        }
    }

    @Override
    protected void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
        this.running = true;
        accepterTread = new Thread(acceptor);
        accepterTread.start();
    }

    @Override
    protected void stopInternal() throws LifecycleException {

    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    @Override
    protected void destroyInternal() throws LifecycleException {

    }

    public void pause() {
        try {
            if (protocolHandler != null) {
                protocolHandler.pause();
            }
        } catch (Exception e) {
            //Ignore
        }
    }

    public ProtocolHandler getProtocolHandler() {
        return protocolHandler;
    }
}
