package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import cn.ntboy.mhttpd.util.net.Poller;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * 这个过滤器是用来处理长连接 和关闭连接的
 */
public class CloseChannelFilter implements Filter {

    private boolean KeepAliveEnable=false;

    @Override
    public void doFilter(Request req, Response res, FilterChain chain) throws Exception {
        chain.doFilter(req,res);
        if(!res.isKeepAlive()){
            //不支持长连接
            SelectionKey key = req.getSelectionKey();

            Poller poller = (Poller)key.attachment();
            poller.cancelledKey(key);
        }
    }
}
