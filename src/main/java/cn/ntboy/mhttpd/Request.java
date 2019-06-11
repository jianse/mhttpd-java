package cn.ntboy.mhttpd;

import java.nio.charset.Charset;

public interface Request {
    /**
     * 得到请求的方法
     * @return 请求方法字符串
     */
    String getMethod();


    /**
     * 设置请求方法
     * @param method 方法字符串
     */
    void setMethod(String method);

    /**
     * 得到请求中带的参数
     * @param key 请求参数的键
     * @return 请求参数的值
     */
    String getParameter(String key);

    /**
     * 设置请求参数
     * @param key 参数的键
     * @param value 参数的值
     */
    void setParameter(String key, String value);

    /**
     * 设置请求文件的路径
     * @param path 文件的路径
     */
    void setPath(String path);

    /**
     * 获得请求资源的路径
     * @return 请求资源的路径
     */
    String getPath();

    /**
     * 设置默认的字符集 默认字符集在不设置的情况下默认是utf-8
     * @param charset 字符集
     */
    void setDefaultCharset(Charset charset);

    /**
     * 设置字符集 字符集默认为空
     * @param charset 字符集
     */
    void setCharset(Charset charset);

    /**
     * 获得字符集 如果为空则返回默认字符集
     * @return 字符集
     */
    Charset getCharset();

    /**
     * 为调试
     * @return 对象内容字串
     */
    String toString();

    String getHeader(String key);

    String setHeader(String key, String value);

    String getUserAgent();

    String getReferer();

    void setContext(Context context);

    Context getContext();

    String getProtocol();
    /**
     * 设置请求的协议
     * @param protocol 协议
     */
    void setProtocol(String protocol);

    String getQueryString();
    void setQueryString(String queryString);

    String getContentType();
    void setContentType(String contentType);

    String getContentLength();

    byte[] getRequestBody();

    void setRequestBody(byte[] body);
}
