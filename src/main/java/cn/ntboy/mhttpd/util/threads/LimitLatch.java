package cn.ntboy.mhttpd.util.threads;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class LimitLatch{

    private class Sync extends AbstractQueuedSynchronizer{

        @Override
        protected int tryAcquireShared(int arg) {
            long newCount=count.incrementAndGet();
            if(!released&&newCount>limit){
                count.decrementAndGet();
                return -1;
            }else {
                return 1;
            }
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            count.decrementAndGet();
            return true;
        }
    }

    private final Sync sync;
    private final AtomicLong count;
    @Getter
    @Setter
    private volatile long limit;
    private volatile boolean released=false;

    public LimitLatch(long limit) {
        this.limit = limit;
        this.count=new AtomicLong(0);
        this.sync=new Sync();
    }

    public void countUpOrAwait() throws InterruptedException{
        sync.acquireSharedInterruptibly(1);
    }

    public long countDown(){
        sync.releaseShared(0);
        long result=getCount();
        return result;
    }

    public boolean releaseAll(){
        released=true;
        return sync.releaseShared(0);
    }

    public void reset(){
        this.count.set(0);
        released=false;
    }

    public long getCount(){return count.get();}

    /**
     * Returns <code>true</code> if there is at least one thread waiting to
     * acquire the shared lock, otherwise returns <code>false</code>.
     * @return <code>true</code> if threads are waiting
     */
    public boolean hasQueuedThreads() {
        return sync.hasQueuedThreads();
    }

    /**
     * Provide access to the list of threads waiting to acquire this limited
     * shared latch.
     * @return a collection of threads
     */
    public Collection<Thread> getQueuedThreads() {
        return sync.getQueuedThreads();
    }
}
