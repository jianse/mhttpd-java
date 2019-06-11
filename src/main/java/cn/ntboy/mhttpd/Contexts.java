package cn.ntboy.mhttpd;

public interface Contexts {
    void addContext(Context context);

    Context getContext(String path);
}
