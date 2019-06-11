package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import cn.ntboy.processor.RequestUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultIndexFilter implements Filter{

    @Override
    public FilterState doFilter(Request request, Response response) throws IOException {
        if(request.getPath().endsWith("/")){
            Path path = RequestUtil.getVisitPath(request);
            DirectoryStream<Path> stream = null;
            try {
                stream = Files.newDirectoryStream(path, request.getContext().getDefaultIndex());
            } catch (IOException e) {
                OutputStream os = response.getOutputStream();
                os.write(e.toString().getBytes());
                response.sendError(500);
            }
            if (stream != null) {
                for (Path item : stream) {
                    if(!Files.isDirectory(item)){
                        String p = request.getPath() + item.getName(item.getNameCount()-1);
                        request.setPath(p);
                        return FilterState.CONTINUE;
                    }
                }
            }
            response.sendError(404);
            return FilterState.BREAK;
        }
        return FilterState.CONTINUE;
    }
}
