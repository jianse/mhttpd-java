package cn.ntboy.mhttpd;

public interface Engine extends Lifecycle{

    public String getDefaultHost();

    public void setDefaultHost(String defaultHost);

    public Service getService();

    public void setService(Service service);
}
