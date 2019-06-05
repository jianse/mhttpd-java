package cn.ntboy.mhttpd.core;

import cn.ntboy.mhttpd.Context;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class StandardContext implements Context {

    @Getter
    @Setter
    private  String path;

    @Getter
    @Setter
    private String docBase;

    @Getter
    @Setter
    private String type;
}
