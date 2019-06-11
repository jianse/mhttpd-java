package cn.ntboy;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;

public class Servlet {

    @Getter
    @Setter
    private String baseDir="D:/www";
    private String defaultIndex="index.{html,htm}";

    void service(Request request, Response response) throws IOException {
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
        System.out.println(request.getPath());
        Path path = Paths.get(baseDir,request.getPath());

        if(Files.isDirectory(path)){
            if(!request.getPath().endsWith("/")){
                response.sendRedirect(request.getPath()+"/");
                return;
            }
            DirectoryStream<Path> stream = Files.newDirectoryStream(path, defaultIndex);
            for (Path item : stream) {
                if(!Files.isDirectory(item)){
                    path=item;
                    break;
                }
            }

        }
        if(!Files.isDirectory(path)&&Files.exists(path)){
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
        }else {
            response.sendError(404);
            response.getOutputStream().write("404".getBytes());
            return;
        }


    }

    protected void doPost(Request request, Response response) {

    }
}
