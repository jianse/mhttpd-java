package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import cn.ntboy.processor.RequestUtil;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileNotFoundFilter implements Filter {
    @Override
    public FilterState doFilter(Request request, Response response) {
        Path path = RequestUtil.getVisitPath(request);
        if(!Files.exists(path)){
            response.sendError(404);
            return FilterState.BREAK;
        }
        return FilterState.CONTINUE;
    }
}
