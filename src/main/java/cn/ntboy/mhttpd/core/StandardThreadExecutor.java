package cn.ntboy.mhttpd.core;

import cn.ntboy.mhttpd.Executor;
import cn.ntboy.mhttpd.LifecycleException;
import cn.ntboy.mhttpd.LifecycleState;
import cn.ntboy.mhttpd.util.LifecycleBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@ToString
public class StandardThreadExecutor extends LifecycleBase implements Executor{

    Logger logger = LogManager.getLogger(StandardThreadExecutor.class);

    @Getter
    @Setter
    String name="Internal";

    @Getter
    @Setter
    private int maxThreads=150;

    @Getter
    @Setter
    private long keepAliveTime=10;

    @Setter
    @Getter
    private int minSpareThreads=4;

    @Getter
    @Setter
    private String namePrefix="thread-worker-";

    class TaskQueue{

    }

    private ArrayBlockingQueue<Runnable> queue=new ArrayBlockingQueue<>(100);
    private ThreadFactory threadFactory = new NameThreadFactory();

    class NameThreadFactory implements ThreadFactory{

        private final AtomicInteger mThreadNum = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,namePrefix+name+"-"+mThreadNum.getAndIncrement());
        }
    }

    private ThreadPoolExecutor executor;

    public StandardThreadExecutor() {
    }

    @Override
    public void execute(Runnable command, long timeout, TimeUnit unit) {
//        executor.setKeepAliveTime(timeout,unit);
        executor.execute(command);
    }

    @Override
    protected void initInternal() throws LifecycleException {
        executor = new ThreadPoolExecutor(minSpareThreads,maxThreads,keepAliveTime,TimeUnit.SECONDS,queue,threadFactory);
    }

    @Override
    protected void startInternal() throws LifecycleException {

        executor.prestartAllCoreThreads();
        logger.info("starting StandardThreadExecutor:{}" ,this.name);
        setState(LifecycleState.STARTING);
    }

    @Override
    protected void stopInternal() throws LifecycleException {

    }

    @Override
    protected void destroyInternal() throws LifecycleException {

    }

    @Override
    public void execute(Runnable command) {
        logger.debug(command);
        executor.execute(command);
    }
}
