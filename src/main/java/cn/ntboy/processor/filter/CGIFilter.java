package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import cn.ntboy.processor.RequestUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;

public class CGIFilter implements Filter {
    Logger logger = LogManager.getLogger(CGIFilter.class);
    @Override
    public FilterState doInRequest(Request request, Response response) throws IOException {
        logger.debug("cgi");
        if(RequestUtil.isCGI(request)){
            Path path = RequestUtil.getVisitPath(request);
            Runtime runtime = Runtime.getRuntime();
            String[] envp=RequestUtil.createCGIEnv(request);
            Process process = runtime.exec(path.toString(), envp, path.getParent().toFile());
            InputStream stream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String line = reader.readLine();
//            System.out.println(line);
            if(line!=null && line.contains("Content-Type")){
                String[] split = line.split(":");
                response.setContentType(split[1].trim());
            }else {
                throw new RuntimeException("cgi have no [Content-Type] header");
            }

            OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(),response.getCharset());
            long l = reader.transferTo(writer);
            writer.flush();
        }
        return FilterState.BREAK;
    }

    @Override
    public FilterState doInResponse(Request request, Response response) throws IOException {
        return FilterState.CONTINUE;
    }
}
