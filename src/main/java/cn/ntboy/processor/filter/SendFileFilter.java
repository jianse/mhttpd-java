package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import cn.ntboy.processor.RequestUtil;
import cn.ntboy.processor.filter.Filter;
import cn.ntboy.processor.filter.FilterState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SendFileFilter implements Filter {
    private static Logger logger = LogManager.getLogger(SendFileFilter.class);

    @Override
    public void doFilter(Request req, Response res, FilterChain chain) throws Exception {
        if(req.getContext().getType().equals("static")){
            Path path = RequestUtil.getVisitPath(req);
            //logger.debug("response : {}",res);
            Files.copy(path,res.getOutputStream());
            return;
        }
        chain.doFilter(req,res);
    }
}
