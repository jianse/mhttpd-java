package cn.ntboy.mhttpd;

import java.util.concurrent.TimeUnit;

public interface Executor extends java.util.concurrent.Executor,Lifecycle {
    void setName(String name);
    String getName();

    void execute(Runnable command, long timeout, TimeUnit unit);

}
