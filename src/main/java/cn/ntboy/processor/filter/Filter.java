package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;

import java.io.IOException;

public interface Filter {

    void doFilter(Request req,Response res,FilterChain chain) throws Exception;
}
