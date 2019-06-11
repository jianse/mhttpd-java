package cn.ntboy;

public class StringUtils {
    public static byte[] Chars2Bytes(char[] src){
        String str = new String(src);
        return str.getBytes();
    }
}
