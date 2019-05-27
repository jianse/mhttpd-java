package cn.ntboy.mhttpd.util.net;

import cn.ntboy.mhttpd.util.threads.LimitLatch;
import lombok.Getter;

import java.util.List;

/**
 * @param <S> The type used by the socket wrapper associated with this endpoint.
 *            May be the same as U.
 * @param <U> The type of the underlying socket used by this endpoint. May be
 *            the same as S.
 */
public abstract class AbstractEndpoint<S,U>{
    private volatile LimitLatch connectionLimitLatch =null;
    @Getter
    private int maxConnections=10000;
    private volatile boolean running =false;

    protected List<Acceptor<U>> acceptors;

    abstract void bind() throws Exception;

    public void countUpOrAwaitConnection() throws InterruptedException {
        //unlimited
        if(maxConnections==-1)return;
        LimitLatch latch = connectionLimitLatch;
        if(latch!=null) latch.countUpOrAwait();
    }

    public void setMaxConnections(int maxCon){
        this.maxConnections=maxCon;
        LimitLatch latch = this.connectionLimitLatch;
        if(latch!=null){
            if(maxCon==-1){
                releaseConnectionLatch();
            }else{
                latch.setLimit(maxCon);
            }
        }else if(maxCon>0){
            initializeConnectionLatch();
        }
    }

    private LimitLatch initializeConnectionLatch() {
        if(maxConnections==-1)return null;
        if(connectionLimitLatch==null){
            connectionLimitLatch =new LimitLatch(getMaxConnections());
        }
        return connectionLimitLatch;
    }

    private void releaseConnectionLatch() {
        LimitLatch latch =connectionLimitLatch;
        if(latch!=null){
            latch.releaseAll();
        }
        connectionLimitLatch=null;
    }

    public boolean isRunning() {
        return running;
    }

    public abstract U serverSocketAccept()throws Exception ;

    public long countDownConnection() {
        if(maxConnections==-1) return -1;
        LimitLatch latch=connectionLimitLatch;
        if(latch!=null){
            long result =latch.countDown();
            return result;
        }else return -1;
    }

    public abstract boolean setSocketOptions(U socket);

    public abstract void closeSocket(U socket);

    public void destroySocket(U socket){
        closeSocket(socket);
    }
}
