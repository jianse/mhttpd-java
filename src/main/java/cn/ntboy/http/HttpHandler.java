package cn.ntboy.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpHandler{

    Socket dataSocket;

    public HttpHandler(Socket dataSocket) {
        this.dataSocket=dataSocket;
    }
}
