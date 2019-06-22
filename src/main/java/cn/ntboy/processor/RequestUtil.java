package cn.ntboy.processor;

import cn.ntboy.mhttpd.Request;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class RequestUtil {
    public static Path getVisitPath(Request request) {
        Path cntPath = Paths.get(request.getContext().getPath());
        Path reqPath = Paths.get(request.getPath());
        Path relativize = cntPath.relativize(reqPath);
        String docBase = request.getContext().getDocBase();
        return Paths.get(docBase, relativize.toString());
    }

    public static boolean isCGI(Request request){
        return request.getContext().getType().equals("cgi");
    }

    public static boolean isStatic(Request request){
        return request.getContext().getType().equals("static");
    }

    public static String[] createCGIEnv(Request request){
        SocketChannel channel = (SocketChannel)request.getSelectionKey().channel();
        InetSocketAddress localAddress=null;
        InetSocketAddress remoteAddress=null;
        try {
            localAddress = (InetSocketAddress) channel.getLocalAddress();
            remoteAddress = (InetSocketAddress)channel.getRemoteAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> list = new ArrayList<>();
        setEnvWithCheck(list, "SERVER_NAME=", localAddress != null ? localAddress.toString() : null);
        list.add("GATEWAY_INTERFACE=CGI/1.1");
        list.add("SERVER_SOFTWARE=mhttpd/1.0");
        list.add("SERVER_PORT="+ (localAddress != null ? localAddress.getPort() : 0));
        list.add("SERVER_PROTOCOL="+request.getProtocol());
        list.add("REQUEST_METHOD="+request.getMethod());
        setEnvWithCheck(list, "HTTP_ACCEPT=", request.getHeader("Accept"));
        setEnvWithCheck(list, "HTTP_REFERER=", request.getReferer());
        setEnvWithCheck(list, "SCRIPT_NAME=", request.getPath());
        setEnvWithCheck(list, "HTTP_USER_AGENT=", request.getUserAgent());
        setEnvWithCheck(list, "QUERY_STRING=", request.getQueryString());
        setEnvWithCheck(list, "CONTENT_TYPE=", request.getContentType());
        setEnvWithCheck(list, "CONTENT_LENGTH=", request.getContentLength());
        setEnvWithCheck(list,"HTTP_COOKIE=",request.getHeader("Cookie"));

        return list.toArray(new String[0]);
    }


    private static void setEnvWithCheck(ArrayList<String> list, String key, String value) {
        if (value != null&&!value.isBlank()) {
            list.add(key + value);
        }
    }
}