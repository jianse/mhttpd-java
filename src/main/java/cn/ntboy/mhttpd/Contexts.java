package cn.ntboy.mhttpd;

public interface Contexts {
    public void addContext(Context context);

    public Context getContext(String path);
}
