package cn.ntboy.mhttpd;

public interface Engine extends Lifecycle{

    String getDefaultHost();

    void setDefaultHost(String defaultHost);

    Service getService();

    void setService(Service service);
}
