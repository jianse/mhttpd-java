package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ServerInternalErrorFilter implements Filter {

    private Logger logger = LogManager.getLogger(ServerInternalErrorFilter.class);

    @Override
    public void doFilter(Request req, Response res, FilterChain chain) throws Exception {
        try{
            chain.doFilter(req,res);
        }catch (Throwable t){
            logger.error("Error filter",t);
            res.sendError(500);
            res.getOutputStream().write(t.toString().getBytes());
        }
    }
}
