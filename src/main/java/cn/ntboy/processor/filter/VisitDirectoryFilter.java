package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import cn.ntboy.processor.RequestUtil;
import cn.ntboy.processor.filter.Filter;
import cn.ntboy.processor.filter.FilterState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class VisitDirectoryFilter implements Filter {
    @Override
    public FilterState doInRequest(Request request, Response response) {
        if(request.getPath().endsWith("/")){

            return FilterState.CONTINUE;
        }

        Path file = RequestUtil.getVisitPath(request);

        if(Files.isDirectory(file)){
            response.sendRedirect(request.getPath()+"/");
            return FilterState.BREAK;
        }
        return FilterState.CONTINUE;
    }

    @Override
    public FilterState doInResponse(Request request, Response response) throws IOException {
        return FilterState.CONTINUE;
    }

}
