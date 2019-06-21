package cn.ntboy.mhttpd.connector;

import cn.ntboy.mhttpd.Executor;
import cn.ntboy.mhttpd.LifecycleException;
import cn.ntboy.mhttpd.LifecycleState;
import cn.ntboy.mhttpd.Service;
import cn.ntboy.mhttpd.core.StandardThreadExecutor;
import cn.ntboy.mhttpd.protocol.ProtocolHandler;
import cn.ntboy.mhttpd.util.LifecycleBase;
import cn.ntboy.mhttpd.util.net.Acceptor;
import cn.ntboy.mhttpd.util.res.StringManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Connector extends LifecycleBase {

    private static Logger logger = LogManager.getLogger(Connector.class);
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
    protected String protocolHandlerClassName = null;
    protected ProtocolHandler protocolHandler = null;
    protected boolean running = false;
    private StringManager sm = StringManager.getManager(Connector.class);
    private Charset uriCharset = StandardCharsets.UTF_8;
    private int port = 18080;
    private ServerSocket serverSocket = null;
    private Acceptor acceptor = null;
    private Service service = null;
    private Thread accepterTread = null;

    public static final String INTERNAL_EXECUTOR_NAME = "Internal";

    public String getExecutorName(){
        Object obj = protocolHandler.getExecutor();
        if(obj instanceof Executor){
            return ((Executor) obj).getName();
        }
        return INTERNAL_EXECUTOR_NAME;
    }

    public Connector(String protocol) throws IOException {

        if ("HTTP/1.1".equals(protocol)) {
            protocolHandlerClassName = "cn.ntboy.HTTP11Protocol";
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
        this.protocolHandler.setConnector(this);
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



    public void setPort(int port) {
        Class clazz=protocolHandler.getClass();
        Method setPort = null;
        try {
            setPort = clazz.getMethod("setPort",int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            setPort.invoke(protocolHandler,port);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void handleSocket(Socket socket) throws IOException {
        //todo: invoke the method in protocolHandler to parse request and return response
        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        threadPool.submit(new Runnable() {
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
        logger.debug("initializing connector...");
        try{
            protocolHandler.init();
        } catch (Exception e) {
            throw new LifecycleException(sm.getString("connector.protocolHandlerInitializationFailed"), e);
        }

    }

    @Override
    protected void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
        try {
            protocolHandler.start();
            logger.debug("started protocol handler:"+protocolHandler.getClass().getName());
        } catch (Exception e) {
            throw new LifecycleException(sm.getString("connector.protocolHandlerStartFailed"), e);
        }
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
        try {
            protocolHandler.stop();
        } catch (Exception e) {
            throw new LifecycleException(sm.getString("connector.protocolHandlerStopFailed"), e);
        }

    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    @Override
    protected void destroyInternal() throws LifecycleException {
        try {
            protocolHandler.destroy();
        }catch (Exception e){
            throw new LifecycleException(sm.getString("connector.protocolHandlerDestroyFailed"),e);
        }

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
