package cn.ntboy.processor;

import cn.ntboy.mhttpd.Contexts;
import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import cn.ntboy.mhttpd.protocol.http.HttpRequest;
import cn.ntboy.mhttpd.protocol.http.HttpResponse;
import cn.ntboy.mhttpd.protocol.http.RequestParserTest;
import cn.ntboy.processor.filter.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Stack;

public class Processor implements Runnable {

    private Request request=null;
    private Response response=null;
    private static final Logger logger = LogManager.getLogger(Processor.class);

    private FilterChain chain = new ArrayListFilterChain();
    private SelectionKey key =null;
    private Contexts contexts = null;

    private Charset charset = StandardCharsets.ISO_8859_1;

    public Processor(SelectionKey key, Contexts contexts) {
        this.key=key;
        this.contexts =contexts;
    }

    private void process(Request request, Response response) {
        try {
            chain.doFilter(request,response);
        } catch (Exception e) {
            //所有异常在这里已经处理完毕没有必要捕获异常了
        }
    }

    @Override
    public void run() {
        if(createRequest()){
            logger.debug("pre process");
            process(request,response);
        }
    }

    private boolean createRequest() {
        try{
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            StringBuilder builder = new StringBuilder();
//            boolean cancel=false;
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
                    //对端链路关闭
//                    cancel=true;
                    key.cancel();
                    channel.close();
                }
            }while(len>0);
            if(key.isValid()){
//                logger.debug("requestString:{}",builder.toString());
                request =new HttpRequest(builder);
                request.setContext(contexts.getContext(request.getPath()));
                request.setSelectionKey(key);
                response=request.getResponse();
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
