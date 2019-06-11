package cn.ntboy.mhttpd;

import cn.ntboy.mhttpd.protocol.http.HTTPStatusCode;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Map;

public interface Response{

    public void sendError(int ec);
    public void sendError(HTTPStatusCode code);

    public OutputStream getOutputStream();

    public String getResponseHeader();

    public byte[] getContent();

    public void sendRedirect(String path);

    public Map getHeader();

    public void setContentLength();

    public void setContentType(String type);

    public String toString();
}
