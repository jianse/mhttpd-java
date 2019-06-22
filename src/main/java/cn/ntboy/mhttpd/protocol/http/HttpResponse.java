package cn.ntboy.mhttpd.protocol.http;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.protocol.http.HTTPStatusCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@ToString(exclude = "request")
public class HttpResponse implements cn.ntboy.mhttpd.Response {
    private static Logger logger = LogManager.getLogger(HttpResponse.class);

    private ByteArrayOutputStream outputStream= new ByteArrayOutputStream();

    HTTPStatusCode statusCode = HTTPStatusCode.OK;
    @Getter
    private boolean error=false;

    public HttpResponse(Request request) {
        this.request = request;
        request.setResponse(this);
        setupValues(request);
    }

    private void setupValues(Request request) {
        if(request.isKeepAlive()){
            this.setKeepAlive(true);
        }
    }

    @Override
    public void sendError(int ec) {
        this.error=true;
        try {
            this.statusCode=HTTPStatusCode.get(ec);
        } catch (Exception e) {
            sendError(500);
        }
    }

    @Override
    public void sendError(HTTPStatusCode code) {
        this.error=true;
        this.statusCode =code;
    }

    @Override
    public OutputStream getOutputStream(){
        return outputStream;
    }


    @Getter
    private Map<String,String> header=new HashMap<>();

    public byte[] getContent(){
        return outputStream.toByteArray();
    }

    @Override
    public void sendRedirect(String path) {
        this.sendError(302);
        this.header.put("Location",path);
    }

    @Override
    public void setContentLength() {
        this.header.put("Content-Length",String.valueOf(outputStream.size()));
    }

    @Override
    public void setContentType(String type) {
        this.header.put("Content-Type",type);
    }

    @Override
    public int getErrorCode() {
        if(error){
            return statusCode.getCode();
        }
        return 200;
    }

    private Charset charset=null;
    private Charset defaultCharset= StandardCharsets.UTF_8;

    @Override
    public Charset getCharset() {
        return charset==null?defaultCharset:charset;
    }

    @Setter
    @Getter
    Request request =null;

    byte[] nl={'\r','\n'};


    @Override
    public void doResponse() {
//        logger.debug("doResponse:{}",this);
        SelectionKey key = request.getSelectionKey();
        SocketChannel channel = (SocketChannel)key.channel();
        try {
            channel.write(getCharset().encode(getResponseLine()));
            channel.write(ByteBuffer.wrap(nl));
//            Thread.sleep(1000);
            header.forEach((k,v)->{
                try {
                    channel.write(getCharset().encode(k));
                    channel.write(ByteBuffer.wrap(": ".getBytes()));
                    channel.write(getCharset().encode(v));
                    channel.write(ByteBuffer.wrap(nl));
                }catch (Exception e){
                    logger.error("write response headers error");
                }
            });
            channel.write(ByteBuffer.wrap(nl));
            channel.write(ByteBuffer.wrap(getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        request.getSelectionKey().interestOps(SelectionKey.OP_READ);

    }

    @Getter
    boolean keepAliveSet;


    @Override
    public void setKeepAlive(boolean keepAlive) {
        if(keepAlive){
            header.put("Connection","keep-alive");
        }else {
            header.put("Connection","close");
        }
        keepAliveSet=true;
    }

    @Override
    public boolean isKeepAlive() {
        return "keep-alive".equals(header.get("Connection"));
    }

    @Override
    public String getContentType() {
        return header.get("Content-Type");
    }

    @Override
    public void setHeader(String key, String value) {
        header.put(key,value);
    }

    public void setKeepAliveTimeOut(int timeout) {
        //fixme
    }

    private String getResponseLine(){
        return "HTTP/1.1 " + statusCode.getCode() + " " + statusCode.getName();
    }


}
