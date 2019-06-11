package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import cn.ntboy.mhttpd.startup.Bootstrap;
import cn.ntboy.processor.RequestUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileNotFoundFilter implements Filter {

    private String notFindPath="www/html/404.html";

    @Override
    public FilterState doInRequest(Request request, Response response) throws IOException {
        Path path = RequestUtil.getVisitPath(request);
        if(!Files.exists(path)){
            response.sendError(404);
            write404Page2Res(response);
            return FilterState.BREAK;
        }
        return FilterState.CONTINUE;
    }

    private void write404Page2Res(Response response) throws IOException {
        Path ntf = Paths.get(Bootstrap.getMhttpdBaseFile().getPath(), notFindPath);
        System.out.println(ntf);
        Files.copy(ntf,response.getOutputStream());
    }

    @Override
    public FilterState doInResponse(Request request, Response response) throws IOException {
        if(response.getErrorCode()==404){
            write404Page2Res(response);
        }
        return FilterState.CONTINUE;
    }
}
