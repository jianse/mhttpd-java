package cn.ntboy.mhttpd.util.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.SocketChannel;

public class NioChannel implements ByteChannel, ScatteringByteChannel, GatheringByteChannel{

    protected static final ByteBuffer emptyBuf = ByteBuffer.allocate(0);
    protected SocketChannel sc=null;
    public SocketChannel getIOChannel(){
        return sc;
    }
    public void setIOChannel( SocketChannel socketChannel){
        this.sc=socketChannel;
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        return sc.write(srcs,offset,length);
    }

    @Override
    public long write(ByteBuffer[] srcs) throws IOException {
        return sc.write(srcs);
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        return sc.read(dsts,offset,length);
    }

    @Override
    public long read(ByteBuffer[] dsts) throws IOException {
        return sc.read(dsts);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return sc.read(dst);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return sc.write(src);
    }

    @Override
    public boolean isOpen() {
        return sc.isOpen();
    }

    @Override
    public void close() throws IOException {
        getIOChannel().socket().close();
        getIOChannel().close();
    }

    public void close(boolean force) throws IOException{
        if(isOpen()||force) close();
    }

}
