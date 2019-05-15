package cn.ntboy.mhttpd.protocol;

import cn.ntboy.mhttpd.util.net.SSLHostConfig;

import java.util.concurrent.Executor;

public interface ProtocolHandler{

    public void init()throws Exception;

    public void start();

    public void pause();

    public void resume();

    public void stop();

    public void destroy();

    public void closeServerSocketGraceful();

    public Executor getExecutor();

    public void setExecutor(Executor executor);

    public void addSslHostConfig(SSLHostConfig sslHostConfig);

    public SSLHostConfig[] findSslHostConfigs();

    public void addUpgradeProtocol(UpgradeProtocol upgradeProtocol);
}
