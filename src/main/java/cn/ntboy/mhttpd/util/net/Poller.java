package cn.ntboy.mhttpd.util.net;

import cn.ntboy.mhttpd.LifecycleException;
import cn.ntboy.mhttpd.LifecycleState;
import cn.ntboy.mhttpd.util.LifecycleBase;
import cn.ntboy.mhttpd.util.collections.SynchronizedQueue;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

public class Poller extends LifecycleBase implements Runnable {

    private SynchronizedQueue<PollerEvent> events = new SynchronizedQueue<>();

    private Logger logger = LogManager.getLogger(Poller.class);
    @Getter
    @Setter
    private TestEndpoint endpoint = null;

    @Setter
    @Getter
    private Selector selector = null;
    private boolean running =false;

    @Override
    public void run() {
        try {
            while (running) {
                int nSelect = selector.select(500);
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {


                    dispatch(it.next());
                    it.remove();

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private volatile int keyCount = 0;

    private long selectorTimeout = 1000;

    //todo:rewrite run
    public void run2() {
        // Loop until destroy() is called
        while (true) {

            boolean hasEvents = false;

            try {
                if (running) {
                    hasEvents = events();
                    if (wakeupCounter.getAndSet(-1) > 0) {
                        // If we are here, means we have other stuff to do
                        // Do a non blocking select
                        keyCount = selector.selectNow();
                    } else {
                        keyCount = selector.select(selectorTimeout);
                    }
                    wakeupCounter.set(0);
                }
                if (!running) {
                    events();
//                    timeout(0, false);
                    try {
                        selector.close();
                    } catch (IOException ioe) {
//                        log.error(sm.getString("endpoint.nio.selectorCloseFail"), ioe);
                    }
                    break;
                }
            } catch (Throwable x) {
//                ExceptionUtils.handleThrowable(x);
//                log.error(sm.getString("endpoint.nio.selectorLoopError"), x);
                continue;
            }
            // Either we timed out or we woke up, process events first
            if ( keyCount == 0 ) hasEvents = (hasEvents | events());

            Iterator<SelectionKey> iterator =
                    keyCount > 0 ? selector.selectedKeys().iterator() : null;
            // Walk through the collection of ready keys and dispatch
            // any active event.
            while (iterator != null && iterator.hasNext()) {
                SelectionKey sk = iterator.next();
                NioSocketWrapper attachment = (NioSocketWrapper)sk.attachment();
                // Attachment may be null if another thread has called
                // cancelledKey()
                if (attachment == null) {
                    iterator.remove();
                } else {
                    iterator.remove();
                    processKey(sk, attachment);
                }
            }

            // Process timeouts
//            timeout(keyCount,hasEvents);
        }

//        getStopLatch().countDown();
    }

    protected void unreg(SelectionKey sk, NioSocketWrapper socketWrapper, int readyOps) {
        // This is a must, so that we don't have multiple threads messing with the socket
        reg(sk, socketWrapper, sk.interestOps() & (~readyOps));
    }

    protected void reg(SelectionKey sk, NioSocketWrapper socketWrapper, int intops) {
        sk.interestOps(intops);
        socketWrapper.interestOps(intops);
    }

    protected void processKey(SelectionKey sk, NioSocketWrapper socketWrapper) {
        try {
            if (!running) {
                cancelledKey(sk);
            } else if (sk.isValid() && socketWrapper != null) {
                if (sk.isReadable() || sk.isWritable()) {
                    unreg(sk, socketWrapper, sk.readyOps());
                    boolean closeSocket = false;
                    // Read goes before write
                    if (sk.isReadable()) {
                        if (socketWrapper.readOperation != null) {
                            getExecutor().execute(socketWrapper.readOperation);
                        } else {
                            closeSocket = true;
                        }
                    }
                    if (!closeSocket && sk.isWritable()) {
                        if (socketWrapper.writeOperation != null) {
                            getExecutor().execute(socketWrapper.writeOperation);
                        } else {
                            closeSocket = true;
                        }
                    }
                    if (closeSocket) {
                        cancelledKey(sk);
                    }
                }
            } else {
                // Invalid key
                cancelledKey(sk);
            }
        } catch (CancelledKeyException ckx) {
            cancelledKey(sk);
        } catch (Throwable t) {
//            ExceptionUtils.handleThrowable(t);
//            log.error(sm.getString("endpoint.nio.keyProcessingError"), t);
        }
    }

    private Executor getExecutor() {
        return endpoint.getExecutor();
    }

    /**
     * process Events in the EventQueue
     * @return false if queue was empty
     */
    private boolean events() {
        boolean res = false;

        PollerEvent pe =null;
        for (int i=0,size = events.size();i<size&&(pe=events.poll())!=null;i++){
            res =true;
            try {
                pe.run();
                pe.reset();
            }catch (Throwable t){
                //todo:error log
            }
        }
        return res;
    }

    private void dispatch(SelectionKey key) throws IOException {
        if (key.isValid()&&key.isReadable()) {
            doRead(key);
        }
    }

    private void doRead(SelectionKey key) throws IOException {
        key.interestOps(SelectionKey.OP_WRITE);
        endpoint.process(key);
        /*fixme
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        StringBuilder builder = new StringBuilder();
        int len=0;
        do{
            buffer.clear();
            len = channel.read(buffer);
            if(len>0){
                buffer.flip();
//                                System.out.println("len:"+len);
                CharBuffer decode = charset.decode(buffer);
                builder.append(decode);
//                                System.out.println("line:\n'"+decode.toString()+"'");
            }else if(len<0){
//                                System.out.println(len);
                //对端链路关闭
                key.cancel();
                channel.close();
            }
        }while(len>0);
        if(key.isValid()){
            endpoint.createRequest(builder,key);
        }*/
    }

    @Override
    protected void initInternal() throws LifecycleException {
        if(selector==null){
            try {
                selector=Selector.open();
            } catch (IOException e) {
                logger.error("start selector error",e);
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
        this.running=true;
        Thread t = new Thread(this,"-Poller");
        t.start();

    }

    @Override
    protected void stopInternal() throws LifecycleException {
        this.running=false;
    }

    @Override
    protected void destroyInternal() throws LifecycleException {
        this.running=false;
        selector.wakeup();
    }

    public  NioSocketWrapper cancelledKey(SelectionKey key){
        NioSocketWrapper ka =null;
        try{
            if(key ==null) return null;
            ka =(NioSocketWrapper)key.attach(null);
            if(ka!=null){
                //当前key可能正在其他线程内处理
            }
            if(key.isValid()){
                key.cancel();
            }
            if(ka!=null){
                try {
                    ka.getSocket().close(true);
                }catch (Exception e){
                    //todo:debug log
                }
            }

            if(key.channel().isOpen()){
                try{
                    key.channel().close();
                }catch (Exception e){
                    //todo:endpoint channelCloseFail
                }
            }
        }catch (Throwable t){
            //todo:
        }
        return ka;
    }

    public void register(final NioChannel socket) {
        socket.setPoller(this);
        NioSocketWrapper socketWrapper = new NioSocketWrapper(socket,endpoint);
        socket.setSocketWrapper(socketWrapper);
        socketWrapper.setPoller(this);
        PollerEvent r = new PollerEvent(socket,socketWrapper,PollerEvent.OP_REGISTER);
        addEvent(r);
    }

    private AtomicLong wakeupCounter = new AtomicLong(0);

    private void addEvent(PollerEvent event) {
        events.offer(event);
        if(wakeupCounter.incrementAndGet()==0){
            selector.wakeup();
        }
    }

    public void add(final NioChannel socket,final int interestOps) {
        PollerEvent r = new PollerEvent(socket,null,interestOps);
        addEvent(r);
    }
}
