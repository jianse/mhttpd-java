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
    public void doFilter(Request req, Response res, FilterChain chain) throws Exception {
        if(!req.getPath().endsWith("/")) {
            Path file = RequestUtil.getVisitPath(req);

            if (Files.isDirectory(file)) {
                res.sendRedirect(req.getPath() + "/");
//                System.out.println(res);
                return;
            }
        }
        chain.doFilter(req,res);
    }
}
