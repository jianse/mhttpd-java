package cn.ntboy.mhttpd.core;

import cn.ntboy.mhttpd.Context;
import cn.ntboy.mhttpd.Request;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@ToString
public class HttpRequest implements Request {

    private Charset defaultCharset= StandardCharsets.UTF_8;

    private Charset charset = null;

    @Getter
    @Setter
    private String method;

    @Getter
    @Setter
    private String path;

    @Getter
    @Setter
    private String protocol;

    @Getter
    @Setter
    private String queryString;

    private Map<String,String> header= new HashMap<>();

    private Map<String,String> parameter= new HashMap<>();

    @Override
    public String getParameter(String key) {
        return parameter.get(key);
    }

    @Override
    public void setParameter(String key, String value) {
        this.parameter.put(key,value);
    }


    @Override
    public void setDefaultCharset(Charset charset) {
        defaultCharset=charset;
    }

    @Override
    public void setCharset(Charset charset) {
        this.charset =charset;
    }

    @Override
    public Charset getCharset() {
        return charset!=null?charset:defaultCharset;
    }

    @Override
    public String getHeader(String key) {
        return header.get(key);
    }

    @Override
    public String setHeader(String key, String value) {
        return header.put(key,value);
    }

    @Override
    public String getUserAgent() {
        return header.get("User-Agent");
    }

    @Override
    public String getReferer() {
        return header.get("Referer");
    }

    @Override
    public String getContentType() {
        return header.get("Content-Type");
    }

    @Override
    public void setContentType(String contentType) {
        header.put("Content-Type",contentType);
    }

    @Override
    public String getContentLength() {
        return header.get("Content-Length");
    }



    @Getter
    @Setter
    private byte[] requestBody=null;

    @Getter
    @Setter
    Context context =null;

}
