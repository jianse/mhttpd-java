package cn.ntboy.processor;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Processor {

    ArrayList<Filter> filters = new ArrayList<>();

    public Processor() {
        filters.add(new FileNotFoundFilter());
        filters.add(new VisitDirectoryFilter());
        filters.add(new DefaultIndexFilter());
    }

    public void process(Request request, Response response) throws IOException {
        for (Filter filter : filters) {
            FilterState state = filter.doFilter(request, response);
            if (state != FilterState.CONTINUE) {
                return;
            }
        }
        if(request.getContext().getType()=="cgi"){
            doCGI(request,response);
            return;
        }else if(request.getContext().getType()=="static"){
            doStatic(request,response);
        }


    }

    private void doCGI(Request request, Response response) {

    }

    private void doStatic(Request request, Response response) throws IOException {
        switch (request.getMethod()){
            case "GET":
                doGet(request,response);
                break;
            case "POST":
                doPost(request,response);
                break;
            default:
                response.sendError(405);
        }
    }

    protected void doGet(Request request, Response response) throws IOException {
        Path path = RequestUtil.getVisitPath(request);
        try (FileChannel fileChannel = FileChannel.open(path)) {
            ByteBuffer buf = ByteBuffer.allocate(1024);
            int len;
            while ((len=fileChannel.read(buf))>0){
                buf.rewind();
                response.getOutputStream().write(buf.array(),0,len);
            }
        } catch (IOException e) {
            //Ignore
            e.printStackTrace();
        }
    }

    protected void doPost(Request request, Response response) {

    }
}
