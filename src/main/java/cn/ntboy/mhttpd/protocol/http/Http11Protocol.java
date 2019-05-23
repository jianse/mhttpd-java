package cn.ntboy.mhttpd.protocol.http;

import cn.ntboy.mhttpd.protocol.ProtocolHandler;
import cn.ntboy.mhttpd.protocol.UpgradeProtocol;
import cn.ntboy.mhttpd.util.net.SSLHostConfig;

import java.util.concurrent.Executor;

public class Http11Protocol implements ProtocolHandler{
    @Override
    public void init() throws Exception {

    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void closeServerSocketGraceful() {

    }

    @Override
    public Executor getExecutor() {
        return null;
    }

    @Override
    public void setExecutor(Executor executor) {

    }

    @Override
    public void addSslHostConfig(SSLHostConfig sslHostConfig) {

    }

    @Override
    public SSLHostConfig[] findSslHostConfigs() {
        return new SSLHostConfig[0];
    }

    @Override
    public void addUpgradeProtocol(UpgradeProtocol upgradeProtocol) {

    }
}
