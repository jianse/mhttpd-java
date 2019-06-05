package cn.ntboy.mhttpd.core;

import cn.ntboy.mhttpd.Context;
import cn.ntboy.mhttpd.Contexts;

import java.util.*;

public class StandardContexts implements Contexts {

    private HashMap<String,Context> contexts=new HashMap<>();

    @Override
    public void addContext(Context context) {
        String path = context.getPath();
        contexts.put(path,context);
    }

    public Context getContext(String path){
//        System.out.println(path);
        String key=getFirstMatchKey(path);
//        System.out.println(key);
        return contexts.get(key);
    }

    private String getFirstMatchKey(String path) {
        List<String> list = getSortedKeyList();
//        System.out.println(list);
        for (String key : list) {
            if(path.startsWith(key)){
                return key;
            }
        }
        return "/";
    }

    private List<String> getSortedKeyList() {
        Set<String> keySet = contexts.keySet();
        List<String> list = new ArrayList<>(keySet);
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int p1= o1.split("[\\/]").length;
                int p2=o2.split("[\\/]").length;
                return p2-p1;
            }
        });
        return list;
    }

}
