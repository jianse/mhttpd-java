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

    private void write404Page2Res(Response response) throws IOException {
        Path ntf = Paths.get(Bootstrap.getMhttpdBaseFile().getPath(), notFindPath);
        System.out.println(ntf);
        Files.copy(ntf,response.getOutputStream());
    }

    @Override
    public void doFilter(Request req, Response res, FilterChain chain) throws Exception {
        Path path = RequestUtil.getVisitPath(req);
        if(!Files.exists(path)){
            res.sendError(404);
            write404Page2Res(res);
            return;
        }
        chain.doFilter(req,res);
        if(res.getErrorCode()==404){
            write404Page2Res(res);
        }
    }
}
