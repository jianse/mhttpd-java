package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;

import java.io.IOException;

public class CGIFilter implements Filter {
    @Override
    public FilterState doFilter(Request request, Response response) throws IOException {
        return FilterState.CONTINUE;
    }
}
