package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;

import java.io.IOException;
import java.util.ArrayList;

public interface FilterChain{

    void doFilter(Request request,Response response)throws Exception;

}
