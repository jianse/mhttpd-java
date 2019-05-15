package cn.ntboy.mhttpd.startup;

import java.io.File;

public class Bootstrap{
    private static final File mhttpdBaseFile;

    static {
        String userDir = System.getProperty("user.dir");
        File ud = new File(userDir);
        mhttpdBaseFile = ud.getParentFile();
    }

    public static File getMhttpdBaseFile() {
        return mhttpdBaseFile;
    }
}
