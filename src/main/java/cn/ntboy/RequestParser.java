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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class RequestParser extends LifecycleBase implements Runnable {

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

    protected void configSocketAndProcess(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        ByteBuffer buf = ByteBuffer.allocate(100);
        StringBuilder sb = new StringBuilder();
        try {
            int len;
            do {
                buf.clear();
                len = socketChannel.read(buf);
                if (len == -1) {
                    //发送端主动关闭了连接 我们也关闭连接就行了
                    socketChannel.close();
                    return;
                }
                buf.limit(len);
                buf.rewind();
                CharBuffer decode = defaultCharset.decode(buf);
                sb.append(decode, 0, decode.length());
            } while (len == buf.capacity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeLog(sb.toString());
        int reqline = sb.indexOf("\r\n");

        this.parseRequestLine(sb.substring(0, reqline));
        int iheaderEnd = sb.indexOf("\r\n\r\n");
        this.parseRequestHeaders(sb.substring(reqline + 2, iheaderEnd));
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
//            System.out.println(System.currentTimeMillis());
            socketChannel.close();
//            System.out.println(System.currentTimeMillis());
        } catch (IOException e) {
            //IGNORE
        }
    }

    private void writeToSocket() throws IOException {
//        System.out.println(response);
//        System.out.println(System.currentTimeMillis());
        this.socketChannel.write(ByteBuffer.wrap(this.response.getResponseHeader().getBytes()));
        this.socketChannel.write(ByteBuffer.wrap(nl));
        this.response.getHeader().forEach((k, v) -> {
            try {
                this.socketChannel.write(ByteBuffer.wrap(((String) k).getBytes()));
                this.socketChannel.write(ByteBuffer.wrap(": ".getBytes()));
                this.socketChannel.write(ByteBuffer.wrap(((String) v).getBytes()));
                this.socketChannel.write(ByteBuffer.wrap(nl));
            } catch (IOException e) {
                e.printStackTrace();
                //todo log or throw
            }

        });
        this.socketChannel.write(ByteBuffer.wrap(nl));
        this.socketChannel.write(ByteBuffer.wrap(this.response.getContent()));
//        System.out.println(System.currentTimeMillis());
    }

    private void parseRequestHeaders(String headerStr) {
        String[] headerArray = headerStr.split("\r\n");
        for (String header : headerArray) {
            int index = header.indexOf(':');
            String key = header.substring(0, index).trim();
            String value = header.substring(index + 1).trim();
            this.request.setHeader(key, value);
        }
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
        try {
            init();
            start();
            stop();
            destroy();
        } catch (LifecycleException e) {
            //todo:do some log
        }


    }

    @Override
    protected void initInternal() throws LifecycleException {

        if (request == null) {
            request = new HttpRequest();
        }
        if (response == null) {
            response = new HttpResponse();
        }
    }

    @Override
    protected void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
        configSocketAndProcess(socketChannel);
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
    }

    @Override
    protected void destroyInternal() throws LifecycleException {

    }

    @Setter
    @Getter
    TestEndpoint endpoint;

}
