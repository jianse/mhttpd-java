package cn.ntboy.processor;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import cn.ntboy.processor.filter.*;

import java.io.IOException;
import java.util.ArrayList;

public class Processor {

    ArrayList<Filter> filters = new ArrayList<>();

    public Processor() {
        filters.add(new FileNotFoundFilter());
        filters.add(new VisitDirectoryFilter());
        filters.add(new DefaultIndexFilter());
        filters.add(new SendFileFilter());
        filters.add(new CGIFilter());
        filters.add(new ContentLengthFilter());
        filters.add(new ContentTypeFilter());
    }

    public void process(Request request, Response response) throws IOException {
        for (Filter filter : filters) {
            FilterState state = filter.doFilter(request, response);
            if (state != FilterState.CONTINUE) {
                return;
            }
        }
    }
}
