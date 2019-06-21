package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;

import java.io.IOException;

public class ServerInternalErrorFilter implements Filter {

    @Override
    public void doFilter(Request req, Response res, FilterChain chain) throws Exception {
        try{
            chain.doFilter(req,res);
        }catch (Throwable t){
            res.sendError(400);
            res.getOutputStream().write(t.toString().getBytes());
        }
    }
}
