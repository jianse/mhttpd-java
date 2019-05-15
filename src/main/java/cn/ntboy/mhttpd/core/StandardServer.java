package cn.ntboy.mhttpd.core;

import cn.ntboy.mhttpd.LifecycleException;
import cn.ntboy.mhttpd.LifecycleState;
import cn.ntboy.mhttpd.Server;
import cn.ntboy.mhttpd.Service;
import cn.ntboy.mhttpd.util.LifecycleBase;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.AccessControlException;

public class StandardServer extends LifecycleBase implements Server{

    private final Object servicesLock = new Object();
    private int port = 8005;
    private String shutdown = "SHUTDOWN";
    private ServerSocket awaitSocket = null;
    private Thread awaitThread = null;
    private volatile boolean stopAwait = false;
    private String address = "localhost";
    private Service[] services = new Service[0];

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String getShutdown() {
        return shutdown;
    }

    @Override
    public void setShutdown(String command) {
        this.shutdown = command;
    }

    @Override
    public void await() {
        try {
            awaitSocket = new ServerSocket(port, 1, InetAddress.getByName(address));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            awaitThread = Thread.currentThread();

            //waiting for shutdown command
            while (!stopAwait) {
                ServerSocket serverSocket = awaitSocket;
                if (awaitSocket == null) {
                    return;
                }

                Socket socket = null;
                StringBuilder command = new StringBuilder();
                try {
                    InputStream stream;
                    try {
                        socket = serverSocket.accept();
                        socket.setSoTimeout(10 * 1000); //Ten sec
                        stream = socket.getInputStream();
                    } catch (SocketTimeoutException ste) {
                        continue;
                    } catch (AccessControlException ace) {
                        continue;
                    } catch (IOException e) {
                        if (stopAwait) {
                            //socket is closed
                            break;
                        }
                        break;
                    }

                    int expected = 1024;
                    while (expected > 0) {
                        int ch = -1;
                        try {
                            ch = stream.read();
                        } catch (IOException e) {
                            ch = -1;
                        }

                        if (ch < 32 || ch == 127) {
                            break;
                        }
                        command.append((char) ch);
                        expected--;
                    }
                } finally {
                    try {
                        if (socket != null) {
                            socket.close();
                        }
                    } catch (IOException e) {
                        //Ignore
                    }
                }

                boolean match = command.toString().equals(shutdown);
                if (match) {
                    break;
                } else {
                    //todo:record this
                }
            }
        } finally {
            ServerSocket serverSocket = awaitSocket;
            awaitThread = null;
            awaitSocket = null;

            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    //Ignore
                }
            }
        }

    }

    @Override
    public void addService(Service service) {
        service.setServer(this);

        synchronized (servicesLock) {
            Service[] results = new Service[services.length + 1];
            System.arraycopy(services, 0, results, 0, services.length);
            results[services.length] = service;
            services = results;
        }
    }

    @Override
    public Service findService(String name) {
        if (name == null) {
            return null;
        }
        synchronized (servicesLock) {
            for (Service service : services) {
                if (name.equals(service.getName())) {
                    return service;
                }
            }
        }
        return null;
    }

    @Override
    public Service[] findServices() {
        return services;
    }

    @Override
    public void removeService(Service service) {
        //todo:
    }

    @Override
    protected void initInternal() throws LifecycleException {

        // Initialize our defined Services
        for (Service service : services) {
            service.init();
        }
    }

    @Override
    protected void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
        synchronized (servicesLock) {
            for (Service service : services) {
                service.start();
            }
        }
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
        for (Service service : services) {
            service.stop();
        }
        stopAwait();
    }

    public void stopAwait() {
        stopAwait = true;
        Thread t = awaitThread;
        if (t != null) {
            ServerSocket s = awaitSocket;
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    //Ignored
                }
            }
            t.interrupt();
            try {
                t.join(1000);
            } catch (InterruptedException e) {
                //Ignored
            }
        }
    }

    @Override
    protected void destroyInternal() throws LifecycleException {
        for (Service service : services) {
            service.destroy();
        }
    }
}
