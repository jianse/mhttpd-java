package cn.ntboy.mhttpd;

import cn.ntboy.mhttpd.protocol.http.HTTPStatusCode;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Map;

public interface Response{

    void sendError(int ec);
    void sendError(HTTPStatusCode code);


    public OutputStream getOutputStream();

    ByteBuffer toByteBuffer();

    public String getResponseHeader();

    public byte[] getContent();

    void sendRedirect(String path);

    Map getHeader();
}
