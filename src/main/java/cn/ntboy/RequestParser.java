package cn.ntboy;

import cn.ntboy.mhttpd.*;
import cn.ntboy.mhttpd.core.HttpRequest;
import cn.ntboy.mhttpd.core.HttpResponse;
import cn.ntboy.mhttpd.util.LifecycleBase;
import cn.ntboy.mhttpd.util.net.TestEndpoint;
import cn.ntboy.processor.Processor;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class RequestParser implements Runnable {

    private static final Logger logger = LogManager.getLogger(RequestParser.class);
    private byte[] nl = {0x0d, 0x0a};
    @Getter
    @Setter
    private SocketChannel socketChannel;
    @Getter
    @Setter
    private Charset defaultCharset = StandardCharsets.UTF_8;
    @Getter
    @Setter
    private Request request = null;
    @Getter
    @Setter
    private Response response = null;
    @Getter
    @Setter
    private String defaultIndex = "index.html";
    @Getter
    @Setter
    private int maxHttpHeaderSize = 1024 * 8;

    private String[] protocols = {"HTTP/1.0", "HTTP/1.1", "HTTP/2.0"};
    private String[] methods = {"GET", "POST", "PUT", "DELETE", "CONNECT", "HEAD", "TRACE", "OPTIONS"};

    public RequestParser(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void parseRequestLine(String requestLine) {
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
            if (split.length != 2) {
                //todo:a wrong url
                throw new RuntimeException();
            }
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
        request.setQueryString(param);
        String[] split = param.split("&");
        for (String item : split) {
            String[] kv = item.split("=");
            if (kv.length != 2) {
                //todo:url参数有误
                throw new RuntimeException();
            }
            String key = URLDecoder.decode(kv[0], defaultCharset).trim();
            String value = URLDecoder.decode(kv[1], defaultCharset).trim();
            this.request.setParameter(key, value);
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

    protected void configSocketAndProcess() {
        Socket socket = socketChannel.socket();
        InputStream stream=null;
        try {
            stream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(stream!=null){
            InputStreamReader iToR = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(iToR);
            try {
                String line =reader.readLine();
                if(line==null){
                    //请求方主动关闭我们也关闭
                    return;
                }
                parseRequestLine(line);
                while (!(line=reader.readLine()).isBlank()){
                    setHeader(line);
                }
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
        logger.debug(request.getMethod()+" "+request.getPath());
        request.setContext(endpoint.getProtocolHandler().getConnector().getService().getContexts().getContext(request.getPath()));
        Processor processor = new Processor();
        try {
            processor.process(request, response);
        } catch (Exception e) {
            OutputStream os = response.getOutputStream();
            try {
                os.write(e.toString().getBytes());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            response.sendError(500);
            e.printStackTrace();
        }

        try {
            writeToSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socketChannel.shutdownOutput();
            socketChannel.close();
        } catch (IOException e) {
            //IGNORE
        }
    }

    private void writeToSocket() throws IOException {
        Socket socket = socketChannel.socket();
        OutputStream os = socket.getOutputStream();
        os.write(this.response.getResponseHeader().getBytes());
        os.write(nl);

        this.response.getHeader().forEach((k, v) -> {
            try {
                os.write(((String)k).getBytes());
                System.out.println(k);
                System.out.println(v);
                os.write(": ".getBytes());
                os.write(((String)v).getBytes());
                os.write(nl);
            } catch (IOException e) {
                e.printStackTrace();
                //todo log or throw
            }

        });
        os.write(nl);
        os.write(this.response.getContent());
    }

    private void parseRequestHeaders(String headerStr) {
        String[] headerArray = headerStr.split("\r\n");
        for (String header : headerArray) {
            setHeader(header);
        }
    }

    private void setHeader(String header) {
        int index = header.indexOf(':');
        String key = header.substring(0, index).trim();
        String value = header.substring(index + 1).trim();
        this.request.setHeader(key, value);
    }

    private void writeLog(String str) {

        try {
            FileOutputStream fos = new FileOutputStream(new File("log/" + System.currentTimeMillis() + ".log"));
            FileChannel logFileChannel = fos.getChannel();
            logFileChannel.write(ByteBuffer.wrap(str.getBytes(defaultCharset)));
            logFileChannel.close();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    public void run() {
        configSocketAndProcess();
    }

    @Setter
    @Getter
    TestEndpoint endpoint;

}
