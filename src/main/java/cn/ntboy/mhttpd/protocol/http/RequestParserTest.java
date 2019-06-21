package cn.ntboy.mhttpd.protocol.http;

import cn.ntboy.StringUtils;
import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import cn.ntboy.mhttpd.util.net.TestEndpoint;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class RequestParserTest {

    private static final Logger logger = LogManager.getLogger(RequestParserTest.class);

    @Getter
    @Setter
    private Charset defaultCharset = StandardCharsets.ISO_8859_1;

    @Getter
    @Setter
    private Request request = null;

    private String[] protocols = {"HTTP/1.0", "HTTP/1.1", "HTTP/2.0"};
    private String[] methods = {"GET", "POST", "PUT", "DELETE", "CONNECT", "HEAD", "TRACE", "OPTIONS"};

    public RequestParserTest() {
    }

    public void parse(Request request){
        this.request =request;
        parse(this.request.getRequestString());
    }

    public void parse(StringBuilder sb){
        parse(sb.toString());
    }

    public void parse(String str){
//        System.out.println("rp:"+str);
        BufferedReader reader =new  BufferedReader(new CharArrayReader(str.toCharArray()));
        try {
            String line = reader.readLine();
            parseRequestLine(line);
            while(!(line=reader.readLine()).isEmpty()){
                setHeader(line);
            }
            //含有请求体
            if(request.getMethod().equals("POST")){
                //解析请求体
                //todo:限制请求体的最大长度 预防DDoS攻击
                if(request.getContentLength()!=null){
                    Integer length = Integer.valueOf(request.getContentLength());
                    System.out.println(length);
                    char[] body=new char[length];
                    int len=0;
                    while (len<length) {
                        len += reader.read(body, len, length);
                    }
                    request.setRequestBody(StringUtils.Chars2Bytes(body));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseRequestLine(String requestLine) {
        String[] s = requestLine.split(" ");
        if (s.length == 3) {
            parseMethod(s[0]);
            parseURL(s[1]);
            parseProtocol(s[2]);
        }
    }

    private void parseProtocol(String protocol) {
        if (contains(protocol, protocols)) {
            this.request.setProtocol(protocol);
        }
    }

    private void parseURL(String url) {
        if (url.contains("?")) {
            // has param
            String[] split = url.split("\\?");

            url = split[0];
            parseQueryString(split[1]);

        }

        parseURLPath(url);
    }

    private void parseURLPath(String url) {
        url = URLDecoder.decode(url, defaultCharset).trim();
        this.request.setPath(url);
    }

    private void parseQueryString(String param) {
        if(param!=null){
            request.setQueryString(param);
            String[] split = param.split("&");
            for (String item : split) {
                String[] kv = item.split("=");
                if(kv.length!=2){
                    continue;
                }
                String key = URLDecoder.decode(kv[0], defaultCharset).trim();
                String value = URLDecoder.decode(kv[1], defaultCharset).trim();
                this.request.setParameter(key, value);
            }
        }

    }

    private void parseMethod(String method) {
        if (contains(method, methods)) {
            this.request.setMethod(method);
        } else {
            throw new RuntimeException("405" + "method");
        }
    }

    private boolean contains(String item, String[] list) {
        for (String method : list) {
            if (method.equals(item)) {
                return true;
            }
        }
        return false;
    }

    private void setHeader(String header) {
        int index = header.indexOf(':');
        String key = header.substring(0, index).trim();
        String value = header.substring(index + 1).trim();
        this.request.setHeader(key, value);
    }

    @Setter
    @Getter
    TestEndpoint endpoint;

}
