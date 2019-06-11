package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import cn.ntboy.processor.RequestUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultIndexFilter implements Filter{

    Logger logger = LogManager.getLogger(DefaultIndexFilter.class);

    @Override
    public FilterState doInRequest(Request request, Response response) throws IOException {
        if(request.getPath().endsWith("/")){
            logger.debug("start default index filter:"+System.currentTimeMillis());
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

            logger.debug("before 404 default index filter:"+System.currentTimeMillis());
            response.sendError(404);
            return FilterState.BREAK;
        }
        return FilterState.CONTINUE;
    }

    @Override
    public FilterState doInResponse(Request request, Response response) throws IOException {
        return FilterState.CONTINUE;
    }
}
