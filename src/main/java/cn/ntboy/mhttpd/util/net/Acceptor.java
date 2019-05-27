package cn.ntboy.mhttpd.util.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Acceptor<U> implements Runnable{


    private final AbstractEndpoint<?,U> endpoint;

    protected volatile AcceptorState state = AcceptorState.NEW;

    public Acceptor(AbstractEndpoint<?, U> endpoint) {
        this.endpoint = endpoint;
    }


    @Override
    public void run() {
        while (endpoint.isRunning()) {
            state = AcceptorState.RUNNING;
            try {
                endpoint.countUpOrAwaitConnection();

                U socket=null;
                try{
                    socket=endpoint.serverSocketAccept();
                }catch (Exception ioe){
                    endpoint.countDownConnection();
                    throw ioe;
                }
                if(endpoint.isRunning()){
                    if(!endpoint.setSocketOptions(socket)){
                        endpoint.closeSocket(socket);
                    }
                }else {
                    endpoint.destroySocket(socket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        state = AcceptorState.ENDED;
    }

    public enum AcceptorState {
        NEW, RUNNING, PAUSED, ENDED
    }
}
