package cn.ntboy.mhttpd.core;

import cn.ntboy.mhttpd.LifecycleException;
import cn.ntboy.mhttpd.LifecycleState;
import cn.ntboy.mhttpd.Server;
import cn.ntboy.mhttpd.Service;
import cn.ntboy.mhttpd.util.LifecycleBase;
import cn.ntboy.mhttpd.util.net.Connector;

public class StandardService extends LifecycleBase implements Service{

    private final Object connectorsLock = new Object();
    private String name = null;
    private Server server = null;
    private Connector[] connectors = new Connector[0];

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
    protected void initInternal() throws LifecycleException {
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
