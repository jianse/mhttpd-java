package cn.ntboy.mhttpd;

public interface Server extends Lifecycle{

    String getAddress();

    void setAddress(String address);

    int getPort();

    void setPort(int port);

    String getShutdown();

    void setShutdown(String command);

    void await();

    void addService(Service service);

    Service findService(String name);

    Service[] findServices();

    void removeService(Service service);

}
