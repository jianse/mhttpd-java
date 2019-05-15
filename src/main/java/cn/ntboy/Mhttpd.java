package cn.ntboy;

import cn.ntboy.mhttpd.Server;
import cn.ntboy.mhttpd.startup.Bootstrap;
import cn.ntboy.mhttpd.startup.ConnectorCreateRule;
import cn.ntboy.mhttpd.startup.MhttpdBaseConfigurationSource;
import cn.ntboy.mhttpd.util.file.ConfigFileLoader;
import cn.ntboy.mhttpd.util.file.ConfigurationSource;
import org.apache.commons.digester3.Digester;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.InputStream;

public class Mhttpd{
    public static final String SERVER_XML = "conf/server.xml";
    private boolean loaded = false;
    private String configFile = SERVER_XML;
    private Server server;

    public static void main(String[] args) throws Exception {
        Mhttpd mhttpd = new Mhttpd();

//        mhttpd.init();
        mhttpd.load();
        Server server = mhttpd.getServer();

        server.start();
        server.await();
    }

    private Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    /**
     * Start a new server instance via <code>server.xml</code>
     */
    private void load() {
        if (loaded) {
            return;
        }
        loaded = true;

        ConfigFileLoader.setSource(
                new MhttpdBaseConfigurationSource(
                        Bootstrap.getMhttpdBaseFile(), getConfigFile()));
        File file = configFile();

        Digester digester = createStartDigester();
        try (ConfigurationSource.Resource resource = ConfigFileLoader.getSource().getServerXml()) {
            InputStream inputStream = resource.getInputStream();
            InputSource inputSource = new InputSource(resource.getUri().toURL().toString());
            inputSource.setByteStream(inputStream);
            digester.push(this);
            digester.parse(inputSource);
        } catch (Exception e) {
            return;
        }

    }

    private Digester createStartDigester() {
        Digester digester = new Digester();
        digester.setValidating(false);

        //Server
        digester.addObjectCreate("Server",
                "cn.ntboy.mhttpd.core.StandardServer");
        digester.addSetProperties("Server");
        digester.addSetNext("Server",
                "setServer",
                "cn.ntboy.mhttpd.Server");

        //Service
        digester.addObjectCreate("Server/Service",
                "cn.ntboy.mhttpd.core.StandardService");
        digester.addSetProperties("Server/Service");
        digester.addSetNext("Server/Service",
                "addService",
                "cn.ntboy.mhttpd.Service");

        //Connector
        digester.addRule("Server/Service/Connector",
                new ConnectorCreateRule());
        digester.addSetProperties("Server/Service/Connector");
        digester.addSetNext("Server/Service/Connector",
                "addConnector",
                "cn.ntboy.mhttpd.util.net.Connector");

        return digester;
    }

    protected File configFile() {
        File file = new File(configFile);
        if (!file.isAbsolute()) {
            file = new File(Bootstrap.getMhttpdBaseFile(), configFile);
        }
        return file;
    }

    private String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    private void init(ConfigurationSource source) {

    }
}
