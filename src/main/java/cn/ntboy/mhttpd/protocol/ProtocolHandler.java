package cn.ntboy.mhttpd.protocol;

import cn.ntboy.mhttpd.Lifecycle;
import cn.ntboy.mhttpd.connector.Connector;
import cn.ntboy.mhttpd.util.net.SSLHostConfig;

import java.util.concurrent.Executor;

public interface ProtocolHandler extends Lifecycle {

    void pause();

    void resume();

    void closeServerSocketGraceful();

    Executor getExecutor();

    void setExecutor(Executor executor);

    void addSslHostConfig(SSLHostConfig sslHostConfig);

    SSLHostConfig[] findSslHostConfigs();

    void addUpgradeProtocol(UpgradeProtocol upgradeProtocol);

    void setConnector(Connector connector);

    Connector getConnector();
}
