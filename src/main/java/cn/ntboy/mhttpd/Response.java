package cn.ntboy.mhttpd;

import cn.ntboy.mhttpd.protocol.http.HTTPStatusCode;
import cn.ntboy.processor.filter.FilterState;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Map;

public interface Response{

    void sendError(int ec);
    void sendError(HTTPStatusCode code);

    OutputStream getOutputStream();

    byte[] getContent();

    void sendRedirect(String path);

    Map getHeader();

    void setContentLength();

    void setContentType(String type);

    String toString();

    int getErrorCode();

    Charset getCharset();

    boolean isError();

    void setRequest(Request request);

    /**
     * write the response to the client
     */
    void doResponse();

    boolean isKeepAliveSet();

    void setKeepAlive(boolean b);

    boolean isKeepAlive();

    String getContentType();

    void setHeader(String key, String value);
}
