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

    private Logger logger = LogManager.getLogger(DefaultIndexFilter.class);


    @Override
    public void doFilter(Request req, Response res, FilterChain chain) throws Exception {
        if(req.getPath().endsWith("/")){
            Path path = RequestUtil.getVisitPath(req);
            DirectoryStream<Path> stream = null;
            try {
                stream = Files.newDirectoryStream(path, req.getContext().getDefaultIndex());
            } catch (IOException e) {
                OutputStream os = res.getOutputStream();
                os.write(e.toString().getBytes());
                res.sendError(500);
            }
            if (stream != null) {
                for (Path item : stream) {
                    if(!Files.isDirectory(item)){
                        String p = req.getPath() + item.getName(item.getNameCount()-1);
                        req.setPath(p);
                        chain.doFilter(req,res);
                        return;
                    }
                }
            }

            res.sendError(404);
            return;
        }
        chain.doFilter(req,res);
    }
}
