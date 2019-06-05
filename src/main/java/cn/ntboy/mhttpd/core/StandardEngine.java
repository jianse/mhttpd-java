package cn.ntboy.mhttpd.core;

import cn.ntboy.mhttpd.Engine;
import cn.ntboy.mhttpd.LifecycleException;
import cn.ntboy.mhttpd.LifecycleState;
import cn.ntboy.mhttpd.Service;
import cn.ntboy.mhttpd.util.LifecycleBase;
import cn.ntboy.mhttpd.util.res.StringManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//todo:impl this
public class StandardEngine extends LifecycleBase implements Engine {

    private static final Logger logger =LogManager.getLogger(StandardEngine.class);
    private static final StringManager sm=StringManager.getManager(StandardEngine.class);

    private String defaultHost;

    private Service service=null;
    @Override
    public String getDefaultHost() {
        return defaultHost;
    }

    @Override
    public void setDefaultHost(String defaultHost) {
        this.defaultHost =defaultHost;
    }

    @Override
    public Service getService() {
        return service;
    }

    @Override
    public void setService(Service service) {
        this.service = service;
    }

    @Override
    protected void initInternal() throws LifecycleException {
        logger.debug(sm.getString("standardEngine.init"));
    }

    @Override
    protected void startInternal() throws LifecycleException {
        logger.debug(sm.getString("standardEngine.start"));
        setState(LifecycleState.STARTING);
    }

    @Override
    protected void stopInternal() throws LifecycleException {

    }

    @Override
    protected void destroyInternal() throws LifecycleException {

    }
}
