package cn.ntboy.mhttpd.core;

import cn.ntboy.mhttpd.Context;
import cn.ntboy.mhttpd.startup.Bootstrap;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.file.Path;
import java.nio.file.Paths;

@ToString
public class StandardContext implements Context {

    @Getter
    @Setter
    private  String path;

    @Override
    public void setDocBase(String docBase) {
        Path path = Paths.get(docBase);
        if(path.isAbsolute()){
            this.docBase = docBase;
        }else {

            path = Paths.get(Bootstrap.getMhttpdBaseFile().getPath(),docBase);
//            System.out.println(path);
            this.docBase=path.toString();
        }

    }

    @Getter
    private String docBase;

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private String defaultIndex="index.{html,htm}";
}
