package cn.ntboy.mhttpd.core;

import cn.ntboy.mhttpd.Request;
import lombok.ToString;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@ToString
public class HttpRequest implements Request {

    private Charset defaultCharset= StandardCharsets.UTF_8;

    private Charset charset = null;

    private String method;

    private String path;
    
    private String protocol;

    private Map<String,String> header= new HashMap<>();

    private Map<String,String> parameter= new HashMap<>();

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String getParameter(String key) {
        return parameter.get(key);
    }

    @Override
    public void setParameter(String key, String value) {
        this.parameter.put(key,value);
    }

    @Override
    public void setPath(String path) {
        this.path=path;
    }

    @Override
    public String getPath() {
        return this.path;
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
    public void setProtocol(String protocol) {
        this.protocol = protocol;
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
}
