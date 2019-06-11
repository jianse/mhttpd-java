package cn.ntboy.mhttpd;

import lombok.ToString;


public interface Context {
    String getPath();
    void setPath(String path);

    String getDocBase();
    void setDocBase(String docBase);

    String getType();
    void setType(String type);

    String getDefaultIndex();
    void setDefaultIndex(String index);
}
