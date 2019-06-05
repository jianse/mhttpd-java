package cn.ntboy.mhttpd;

import cn.ntboy.mhttpd.connector.Connector;

public interface Service extends Lifecycle{
    public String getName();

    public void setName(String name);

    public Server getServer();

    public void setServer(Server server);

    public void addConnector(Connector connector);

    public Connector[] findConnectors();

    public void removeConnector(Connector connector);

    public void addExecutor(Executor ex);

    public Executor[] findExecutors();

    public Executor getExecutor(String name);

    public void removeExecutor(Executor ex);

    public void setContainer(Engine engine);

    public void setContexts(Contexts contexts);

    public Contexts getContexts();
}
