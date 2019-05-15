package cn.ntboy.http;

import java.net.Socket;

public class HttpHandler{

    Socket dataSocket;

    public HttpHandler(Socket dataSocket) {
        this.dataSocket = dataSocket;
    }
}
