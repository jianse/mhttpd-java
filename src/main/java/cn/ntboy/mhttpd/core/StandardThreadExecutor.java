package cn.ntboy.mhttpd.core;

import cn.ntboy.mhttpd.Executor;
import cn.ntboy.mhttpd.LifecycleException;
import cn.ntboy.mhttpd.util.LifecycleBase;

import java.util.concurrent.TimeUnit;

public class StandardThreadExecutor extends LifecycleBase implements Executor {

    String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute(Runnable command, long timeout, TimeUnit unit) {

    }

    @Override
    protected void initInternal() throws LifecycleException {

    }

    @Override
    protected void startInternal() throws LifecycleException {

    }

    @Override
    protected void stopInternal() throws LifecycleException {

    }

    @Override
    protected void destroyInternal() throws LifecycleException {

    }

    @Override
    public void execute(Runnable command) {

    }
}
