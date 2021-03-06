package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import cn.ntboy.mhttpd.protocol.http.HttpResponse;
import cn.ntboy.processor.RequestUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class CGIFilter implements Filter {
    private Logger logger = LogManager.getLogger(CGIFilter.class);

    @Override
    public void doFilter(Request req, Response res, FilterChain chain) throws Exception {
        logger.debug("cgi");
        if(RequestUtil.isCGI(req)){
            Path path = RequestUtil.getVisitPath(req);
            Runtime runtime = Runtime.getRuntime();
            String[] envp=RequestUtil.createCGIEnv(req);
            Process process = runtime.exec(path.toString(), envp, path.getParent().toFile());

            if(req.getMethod().equals("POST")&&req.getRequestBody()!=null){

                OutputStream os = process.getOutputStream();
                os.write(req.getRequestBody());
                os.flush();
                os.close();
            }

            try {
                process.waitFor(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            InputStream stream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line=null;
            while(!(line=reader.readLine()).isEmpty()){
//                line = reader.readLine();
//                System.out.println(line);

                String[] split = line.split(":");
                res.setHeader(split[0].trim(),split[1].trim());

            }

            if (res.getContentType() == null||res.getContentType().isEmpty()) {
                res.setContentType("text/html");
            }


            OutputStreamWriter writer = new OutputStreamWriter(res.getOutputStream(),res.getCharset());
            long l = reader.transferTo(writer);
            writer.flush();
            reader.close();
        }
        chain.doFilter(req,res);
    }
}
