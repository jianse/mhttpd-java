package cn.ntboy.mhttpd;

import lombok.ToString;


public interface Context {
    public String getPath();
    public void setPath(String path);

    public String getDocBase();
    public void setDocBase(String docBase);

    public String getType();
    public void setType(String type);
}
