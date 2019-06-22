package cn.ntboy.processor.filter;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;

import java.util.ArrayList;

public class ArrayListFilterChain implements FilterChain {
    private ArrayList<Filter> filters = new ArrayList<>();

    private Integer pos = 0;

    public ArrayListFilterChain() {
        filters.add(new CloseChannelFilter());
        filters.add(new ResponseFilter());
        filters.add(new ContentLengthFilter());
        filters.add(new ContentTypeFilter());
        filters.add(new ServerInternalErrorFilter());
        filters.add(new FileNotFoundFilter());
        filters.add(new VisitDirectoryFilter());
        filters.add(new DefaultIndexFilter());

        filters.add(new SendFileFilter());
        filters.add(new CGIFilter());
    }

    @Override
    public void doFilter(Request request, Response response) throws Exception {
        if(pos<filters.size()){
            Filter filter = filters.get(pos);
            pos++;
            filter.doFilter(request,response,this);
        }
    }
}
