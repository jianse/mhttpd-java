package cn.ntboy.mhttpd.startup;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.RuleSet;

public class HostRoleSet implements RuleSet {
    private final String prefix;

    public HostRoleSet(String prefix) {
        this.prefix=prefix;
    }

    @Override
    public String getNamespaceURI() {
        return null;
    }

    @Override
    public void addRuleInstances(Digester digester) {
        digester.addObjectCreate(prefix,
                "cn.ntboy.mhttpd.core.StandardHost",
                "className");

        digester.addSetProperties(prefix);
        digester.addSetNext(prefix,
                "addChild",
                "cn.ntboy.mhttpd.Container");
    }
}
