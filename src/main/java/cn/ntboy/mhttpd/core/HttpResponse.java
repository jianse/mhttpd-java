package cn.ntboy.mhttpd.core;

import cn.ntboy.mhttpd.protocol.http.HTTPStatusCode;
import lombok.Getter;
import lombok.ToString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@ToString
public class HttpResponse implements cn.ntboy.mhttpd.Response {
    private ByteArrayOutputStream outputStream= new ByteArrayOutputStream();

    HTTPStatusCode statusCode = HTTPStatusCode.OK;
    private boolean error=false;

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

    public String getResponseHeader(){
        return "HTTP/1.1 "+statusCode.getCode()+" "+statusCode.getName();
    }

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
}
