package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;

import java.io.IOException;

public interface Filter {

    /**
     * 请求到达时调用此方法
     * @param request 请求
     * @param response 响应
     * @return 过滤器的状态 表示继续执行过滤器还是发回响应
     * @throws IOException ioe
     */
    FilterState doInRequest(Request request, Response response) throws IOException;

    FilterState doInResponse(Request request, Response response) throws IOException;
}
