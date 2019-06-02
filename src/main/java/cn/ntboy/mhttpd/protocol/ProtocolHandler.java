package cn.ntboy.mhttpd.protocol;

import cn.ntboy.mhttpd.Lifecycle;
import cn.ntboy.mhttpd.util.net.SSLHostConfig;

import java.util.concurrent.Executor;

public interface ProtocolHandler extends Lifecycle {

    public void pause();

    public void resume();

    public void closeServerSocketGraceful();

    public Executor getExecutor();

    public void setExecutor(Executor executor);

    public void addSslHostConfig(SSLHostConfig sslHostConfig);

    public SSLHostConfig[] findSslHostConfigs();

    public void addUpgradeProtocol(UpgradeProtocol upgradeProtocol);
}
