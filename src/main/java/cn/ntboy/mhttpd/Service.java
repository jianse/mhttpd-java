package cn.ntboy.mhttpd;

import cn.ntboy.mhttpd.connector.Connector;

public interface Service extends Lifecycle{
    String getName();

    void setName(String name);

    Server getServer();

    void setServer(Server server);

    void addConnector(Connector connector);

    Connector[] findConnectors();

    void removeConnector(Connector connector);

    void addExecutor(Executor ex);

    Executor[] findExecutors();

    Executor getExecutor(String name);

    void removeExecutor(Executor ex);

    void setContainer(Engine engine);

    void setContexts(Contexts contexts);

    Contexts getContexts();
}
