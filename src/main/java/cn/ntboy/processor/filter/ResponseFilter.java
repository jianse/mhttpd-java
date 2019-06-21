package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ResponseFilter implements Filter {
    private Logger logger = LogManager.getLogger(ResponseFilter.class);

    @Override
    public void doFilter(Request req, Response res, FilterChain chain) throws Exception {
        chain.doFilter(req,res);
        res.doResponse();
        logger.info("{} {} {} {}",req.getPath(),req.getMethod(),res.getErrorCode(),res.getHeader());
    }
}
