package cn.ntboy.mhttpd;

public interface Server extends Lifecycle{

    public String getAddress();

    public void setAddress(String address);

    public int getPort();

    public void setPort(int port);

    public String getShutdown();

    public void setShutdown(String command);

    public void await();

    public void addService(Service service);

    public Service findService(String name);

    public Service[] findServices();

    public void removeService(Service service);

}
