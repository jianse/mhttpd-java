package cn.ntboy.mhttpd.util.net;

import cn.ntboy.mhttpd.LifecycleException;
import cn.ntboy.mhttpd.LifecycleState;
import cn.ntboy.mhttpd.util.LifecycleBase;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class Poller extends LifecycleBase implements Runnable {

    private Charset charset = StandardCharsets.ISO_8859_1;
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

    private void dispatch(SelectionKey key) throws IOException {
        if (key.isReadable()) {
            doRead(key);
            key.cancel();
        }
    }

    private void doRead(SelectionKey key) throws IOException {
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
