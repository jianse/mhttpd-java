package cn.ntboy.processor;

import cn.ntboy.mhttpd.Request;

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
        ArrayList<String> list = new ArrayList<>();
        list.add("SERVER_NAME=mhttpd");
        list.add("SERVER_PROTOCOL="+request.getProtocol());
        list.add("REQUEST_METHOD="+request.getMethod());
        setEnvWithCheck(list, request.getUserAgent(), "HTTP_USER_AGENT=");
        setEnvWithCheck(list,request.getQueryString(),"QUERY_STRING=");
        setEnvWithCheck(list, request.getContentType(), "CONTENT_TYPE=");
        setEnvWithCheck(list,request.getContentLength(),"CONTENT_LENGTH=");
        setEnvWithCheck(list,request.getReferer(),"HTTP_REFERER=");
        return list.toArray(new String[0]);
    }

    private static void setEnvWithCheck(ArrayList<String> list, String value, String s) {
        if (value != null&&!value.isBlank()) {
            list.add(s + value);
        }
    }
}