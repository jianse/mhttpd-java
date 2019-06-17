package cn.ntboy;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class Poller implements Runnable{
    private Charset charset = StandardCharsets.ISO_8859_1;

    @Override
    public void run() {
        try {
            while (true) {
                int nSelect = selector.select(500);
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    if (key.isValid()&&key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int len=0;
                        do{
                            buffer.clear();
                            len = channel.read(buffer);
                            if(len>0){
                                buffer.flip();
                                System.out.println("len:"+len);
                                CharBuffer decode = charset.decode(buffer);
                                System.out.println("line:\n'"+decode.toString()+"'");
                            }else if(len<0){
                                System.out.println(len);
                                //对端链路关闭
                                key.cancel();
                                channel.close();
                            }
                        }while(len>0);

                    }
                    it.remove();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Getter
    @Setter
    private Selector selector;

}