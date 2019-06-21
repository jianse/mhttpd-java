package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import cn.ntboy.processor.RequestUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ContentTypeFilter implements Filter {

    @Override
    public void doFilter(Request req, Response res, FilterChain chain) throws Exception {
        chain.doFilter(req,res);
        if(res.isError()){
            res.setContentType("text/html");
        }else {
            String type = Files.probeContentType(RequestUtil.getVisitPath(req));
//            System.out.println(type);
            res.setContentType(type);
        }

    }
}
