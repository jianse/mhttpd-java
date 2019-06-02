package cn.ntboy.mhttpd.core;

import cn.ntboy.mhttpd.*;
import cn.ntboy.mhttpd.util.LifecycleBase;
import cn.ntboy.mhttpd.util.net.Connector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class StandardService extends LifecycleBase implements Service{

    Logger log = LogManager.getLogger(StandardService.class);
    private final Object connectorsLock = new Object();
    private String name = null;
    private Server server = null;
    private Connector[] connectors = new Connector[0];

    /**
     * The list of executors held by the service.
     */
    protected final ArrayList<Executor> executors = new ArrayList<>();
    private Engine engine =null;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Server getServer() {
        return this.server;
    }

    @Override
    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public void addConnector(Connector connector) {

        synchronized (connectorsLock) {
            connector.setService(this);
            Connector[] results = new Connector[connectors.length + 1];
            System.arraycopy(connectors, 0, results, 0, connectors.length);
            results[connectors.length] = connector;
            connectors = results;
        }

    }

    @Override
    public Connector[] findConnectors() {
        return connectors;
    }

    @Override
    public void removeConnector(Connector connector) {
        //todo: fix this
    }

    @Override
    public void addExecutor(Executor ex) {
        synchronized (executors) {
            if(!executors.contains(ex)) {
                executors.add(ex);
                if(getState().isAvailable()){
                    try{
                        ex.start();
                    }catch (LifecycleException x){
                        log.error("executor start error",x);
                    }
                }
            }
        }
    }

    @Override
    public Executor[] findExecutors() {
        synchronized (executors) {
            Executor[] arr = new Executor[executors.size()];
            executors.toArray(arr);
            return arr;
        }
    }

    @Override
    public Executor getExecutor(String name) {
        synchronized (executors) {
            for (Executor executor: executors) {
                if (name.equals(executor.getName()))
                    return executor;
            }
        }
        return null;
    }

    @Override
    public void removeExecutor(Executor ex) {
        synchronized (executors) {
            if ( executors.remove(ex) && getState().isAvailable() ) {
                try {
                    ex.stop();
                } catch (LifecycleException e) {
                    log.error("standardService.executor.stop", e);
                }
            }
        }
    }

    @Override
    protected void initInternal() throws LifecycleException {
        if (engine!=null) {
            engine.init();
        }

        for(Executor executor:findExecutors()){
            executor.init();
        }

        synchronized (connectorsLock) {
            for (Connector connector : connectors) {
                connector.init();
            }
        }
    }

    @Override
    protected void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
        synchronized (connectorsLock) {
            for (Connector connector : connectors) {
                connector.start();
            }
        }
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        synchronized (connectorsLock) {
            for (Connector connector : connectors) {
                connector.pause();
                connector.getProtocolHandler().closeServerSocketGraceful();
            }
        }
    }

    @Override
    protected void destroyInternal() throws LifecycleException {
        synchronized (connectorsLock) {
            for (Connector connector : connectors) {
                connector.destroy();
            }
        }
    }
}
