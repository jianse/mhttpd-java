package cn.ntboy.processor;

import cn.ntboy.mhttpd.Request;
import cn.ntboy.mhttpd.Response;
import cn.ntboy.processor.filter.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class Processor {
    private static final Logger logger = LogManager.getLogger(Processor.class);

    private ArrayList<Filter> filters = new ArrayList<>();

    public Processor() {
        filters.add(new ContentLengthFilter());
        filters.add(new ContentTypeFilter());
        filters.add(new FileNotFoundFilter());
        filters.add(new VisitDirectoryFilter());
        filters.add(new DefaultIndexFilter());
        filters.add(new SendFileFilter());
        filters.add(new CGIFilter());
    }

    public void process(Request request, Response response) throws IOException {
        Stack<Filter> filterStack = new Stack<>();
        for (Filter filter : this.filters) {
            filterStack.push(filter);
            FilterState state = filter.doInRequest(request, response);
            if (state != FilterState.CONTINUE) {
                break;
            }
        }
        while (!(filterStack.empty())){
            Filter filter = filterStack.pop();
            filter.doInResponse(request,response);
        }
        logger.debug("doFilter done");
    }
}
