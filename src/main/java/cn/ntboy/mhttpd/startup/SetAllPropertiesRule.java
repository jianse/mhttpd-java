package cn.ntboy.mhttpd.startup;

import org.apache.commons.digester3.Rule;
import org.xml.sax.Attributes;

public class SetAllPropertiesRule extends Rule{

    public SetAllPropertiesRule() {
    }

    @Override
    public void begin(String namespace, String nameX, Attributes attributes) throws Exception {
        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getLocalName(i);
            if ("".equals(name)) {
                name = attributes.getQName(i);
            }
            String value = attributes.getValue(i);
            System.out.println(name + ":" + value);
        }
    }
}
