package cn.ntboy.mhttpd.core;

import cn.ntboy.mhttpd.protocol.http.HTTPStatusCode;
import lombok.Getter;
import lombok.ToString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

@ToString
public class HttpResponse implements cn.ntboy.mhttpd.Response {
    private ByteArrayOutputStream outputStream= new ByteArrayOutputStream();

    HTTPStatusCode statusCode = HTTPStatusCode.OK;

    @Override
    public void sendError(int ec) {
        try {
            this.statusCode=HTTPStatusCode.get(ec);
        } catch (Exception e) {
            sendError(500);
        }
    }

    @Override
    public void sendError(HTTPStatusCode code) {
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
}
