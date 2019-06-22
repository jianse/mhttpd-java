package cn.ntboy.mhttpd.util.net;

import lombok.Getter;
import lombok.Setter;

import java.nio.channels.SelectionKey;
import java.util.concurrent.CountDownLatch;

public class NioSocketWrapper {




    public void registerReadInterest() {
        getPoller().add(getSocket(), SelectionKey.OP_READ);
    }


    public void registerWriteInterest() {
        getPoller().add(getSocket(), SelectionKey.OP_WRITE);
    }

    @Getter
    @Setter
    Poller poller = null;
    @Setter
    @Getter
    private NioChannel socket;
    public volatile boolean closed =false;
    @Getter
    @Setter
    TestEndpoint endpoint;
    private int interestOps =0;
    private CountDownLatch readLatch=null;
    private CountDownLatch writeLatch =null;


    public OperationState<?> readOperation = null;
    public OperationState<?> writeOperation = null;

    class OperationState<T> implements Runnable{
        T attachment;

        @Override
        public void run() {

        }
    }

    public NioSocketWrapper(NioChannel socket, TestEndpoint endpoint) {
        this.socket=socket;
        this.endpoint =endpoint;
    }

    public int interestOps(){
        return interestOps;
    }
    public int interestOps(int ops) {
        this.interestOps =ops;
        return ops;
    }

    public CountDownLatch getReadLatch() { return readLatch; }
    public CountDownLatch getWriteLatch() { return writeLatch; }
    protected CountDownLatch resetLatch(CountDownLatch latch){
        if(latch==null||latch.getCount()==0){
            return null;
        }else {
            throw new IllegalStateException("latchMustBeZero");
        }


    }
    public void resetReadLatch() { readLatch = resetLatch(readLatch); }
    public void resetWriteLatch() { writeLatch = resetLatch(writeLatch); }


}
