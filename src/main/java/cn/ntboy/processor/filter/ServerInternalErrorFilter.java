package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;

import java.io.IOException;

public class ServerInternalErrorFilter implements Filter {
    @Override
    public FilterState doInRequest(Request request, Response response) throws IOException {
        return FilterState.CONTINUE;
    }

    @Override
    public FilterState doInResponse(Request request, Response response) throws IOException {

        return FilterState.CONTINUE;
    }
}
