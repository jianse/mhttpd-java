package cn.ntboy.mhttpd.util.net;

import cn.ntboy.mhttpd.LifecycleException;
import cn.ntboy.mhttpd.LifecycleState;
import cn.ntboy.mhttpd.util.LifecycleBase;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Poller extends LifecycleBase implements Runnable {

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
        while (running){
            try {
                int nReady = selector.select(500);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()){
                SelectionKey key = it.next();

                if(key.isReadable()) {
                    SocketChannel channel = (SocketChannel)key.channel();
                    endpoint.process(channel);
                }
                it.remove();
            }
        }

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
        Thread t = new Thread(this);
        t.start();

    }

    @Override
    protected void stopInternal() throws LifecycleException {
        this.running=false;
    }

    @Override
    protected void destroyInternal() throws LifecycleException {

    }
}
