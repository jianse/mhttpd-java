package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import cn.ntboy.processor.filter.Filter;
import cn.ntboy.processor.filter.FilterState;

import java.io.IOException;

public class ContentLengthFilter implements Filter {
    @Override
    public FilterState doFilter(Request request, Response response) throws IOException {
        response.setContentLength();
        return FilterState.CONTINUE;
    }
}
