package cn.ntboy.processor;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class VisitDirectoryFilter implements Filter {
    @Override
    public FilterState doFilter(Request request, Response response) {
        if(request.getPath().endsWith("/")){

            return FilterState.CONTINUE;
        }

        Path file = RequestUtil.getVisitPath(request);

        System.out.println("file"+file);
        if(Files.isDirectory(file)){
            response.sendRedirect(request.getPath()+"/");
            return FilterState.BREAK;
        }
        return FilterState.CONTINUE;
    }

}
