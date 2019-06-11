package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import cn.ntboy.processor.RequestUtil;
import net.sf.jmimemagic.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ContentTypeFilter implements Filter {
    @Override
    public FilterState doFilter(Request request, Response response) throws IOException {
        String type = Files.probeContentType(RequestUtil.getVisitPath(request));
        System.out.println(type);
        response.setContentType(type);
        return FilterState.CONTINUE;
    }
}
