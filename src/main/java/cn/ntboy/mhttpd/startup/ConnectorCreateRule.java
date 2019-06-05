package cn.ntboy.mhttpd.startup;

import cn.ntboy.mhttpd.Executor;
import cn.ntboy.mhttpd.Service;
import cn.ntboy.mhttpd.connector.Connector;
import org.apache.commons.digester3.Rule;
import org.xml.sax.Attributes;

public class ConnectorCreateRule extends Rule{

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        Service service = (Service) getDigester().peek();
        Executor ex =null;
        if(attributes.getValue("executor")!=null){
            ex =service.getExecutor(attributes.getValue("executor"));
        }
        Connector con = new Connector(attributes.getValue("protocol"));
        if(ex!=null){
            con.getProtocolHandler().setExecutor(ex);
//            setExecutor(con,ex);
        }
        getDigester().push(con);
    }

    @Override
    public void end(String namespace, String name) throws Exception {
        getDigester().pop();
    }
}
