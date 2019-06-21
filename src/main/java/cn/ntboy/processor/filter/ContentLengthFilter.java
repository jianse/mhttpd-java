package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import cn.ntboy.processor.filter.Filter;
import cn.ntboy.processor.filter.FilterState;

import java.io.IOException;

public class ContentLengthFilter implements Filter {

    @Override
    public void doFilter(Request req, Response res, FilterChain chain) throws Exception {
        chain.doFilter(req,res);
        res.setContentLength();
    }
}
