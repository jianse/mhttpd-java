package cn.ntboy.mhttpd.startup;

import cn.ntboy.mhttpd.Service;
import cn.ntboy.mhttpd.util.net.Connector;
import org.apache.commons.digester3.Rule;
import org.xml.sax.Attributes;

public class ConnectorCreateRule extends Rule{

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        Service service = (Service) getDigester().peek();
        Connector con = new Connector(attributes.getValue("protocol"));
        getDigester().push(con);
    }

    @Override
    public void end(String namespace, String name) throws Exception {
        getDigester().pop();
    }
}
