package cn.ntboy.processor;

import cn.ntboy.mhttpd.Request;

import java.nio.file.Path;
import java.nio.file.Paths;

public class RequestUtil {
    public static Path getVisitPath(Request request) {
        Path cntPath = Paths.get(request.getContext().getPath());
        Path reqPath = Paths.get(request.getPath());
        Path relativize = cntPath.relativize(reqPath);
        String docBase = request.getContext().getDocBase();
        return Paths.get(docBase, relativize.toString());
    }
}