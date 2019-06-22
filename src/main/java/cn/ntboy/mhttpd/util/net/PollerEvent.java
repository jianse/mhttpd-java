package cn.ntboy.mhttpd.util.net;

import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;

public class PollerEvent implements Runnable{

    public static final int OP_REGISTER = 0x100;

    private NioChannel socket;
    private int interestOps;
    private NioSocketWrapper socketWrapper;

    public PollerEvent(NioChannel socket, NioSocketWrapper socketWrapper, int interestOps) {
        reset(socket, socketWrapper, interestOps);
    }

    public void reset(NioChannel socket, NioSocketWrapper socketWrapper, int interestOps) {
        this.socket = socket;
        this.interestOps = interestOps;
        this.socketWrapper = socketWrapper;
    }

    public void reset(){
        reset(null, null, 0);
    }

    @Override
    public void run() {
        if (interestOps ==OP_REGISTER){
            try{
                socket.getIOChannel().register(
                        socket.getPoller().getSelector(), SelectionKey.OP_READ,socketWrapper);
            }catch (Exception x){
                //todo:do some log
            }
        }else{
            final SelectionKey key =socket.getIOChannel().keyFor(socket.getPoller().getSelector());
            try{
                if(key==null){
                    //The key was cancelled
                    socket.socketWrapper.getEndpoint().countDownConnection();
                    socket.socketWrapper.closed=true;
                }else {
                    final NioSocketWrapper socketWrapper = (NioSocketWrapper) key.attachment();
                    if(socketWrapper!=null){
                        int ops=key.interestOps()|interestOps;
                        socketWrapper.interestOps(ops);
                        key.interestOps(ops);
                    }else {
                        socket.getPoller().cancelledKey(key);
                    }
                }
            }catch (CancelledKeyException ckx){
                try{
                    socket.getPoller().cancelledKey(key);
                }catch (Exception ignore){}
            }
        }
    }
}
