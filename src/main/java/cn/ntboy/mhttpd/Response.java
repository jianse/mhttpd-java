package cn.ntboy.mhttpd;

import cn.ntboy.mhttpd.protocol.http.HTTPStatusCode;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Map;

public interface Response{

    void sendError(int ec);
    void sendError(HTTPStatusCode code);

    OutputStream getOutputStream();

    String getResponseHeader();

    byte[] getContent();

    void sendRedirect(String path);

    Map getHeader();

    void setContentLength();

    void setContentType(String type);

    String toString();

    int getErrorCode();

    Charset getCharset();

    boolean isError();
}
