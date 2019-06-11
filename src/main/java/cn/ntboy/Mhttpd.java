package cn.ntboy;

import cn.ntboy.mhttpd.startup.HostRoleSet;
import cn.ntboy.mhttpd.Server;
import cn.ntboy.mhttpd.startup.Bootstrap;
import cn.ntboy.mhttpd.startup.ConnectorCreateRule;
import cn.ntboy.mhttpd.startup.MhttpdBaseConfigurationSource;
import cn.ntboy.mhttpd.util.CommandLineParser;
import cn.ntboy.mhttpd.util.file.ConfigFileLoader;
import cn.ntboy.mhttpd.util.file.ConfigurationSource;
import cn.ntboy.mhttpd.util.res.StringManager;
import org.apache.commons.digester3.Digester;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;

public class Mhttpd{

    private static final Logger  log = LogManager.getLogger(Mhttpd.class);
    private static final StringManager sm =StringManager.getManager(Mhttpd.class);

    public static final String SERVER_XML = "conf/server.xml";
    private static boolean start = true;
    private static boolean useDefault = false;
    private boolean loaded = false;
    private String configFile = SERVER_XML;
    private Server server;

    public static void main(String[] args) throws Exception {
        log.debug(sm.getString("mhttpd.CommandLineArgs",Arrays.toString(args)));
        CommandLineParser parser = getCommandLineParser();
        parser.parse(args);
        if (start) {
            Mhttpd mhttpd = new Mhttpd();
            mhttpd.load();
            Server server = mhttpd.getServer();
            server.start();
            server.await();
        }
    }

    private static CommandLineParser getCommandLineParser() {
        CommandLineParser parser = new CommandLineParser();
        parser.addOption("help", "h", CommandLineParser.ArgState.NO_ARG, (op, i, arg) -> {
            Mhttpd.start = false;
            System.out.println("this is a help text");
        });
        parser.addOption("usedefault", "d", CommandLineParser.ArgState.NO_ARG, (op, i, arg) -> {
            log.debug("use default config");
            useDefault = true;
        });
        return parser;
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

        if(useDefault){
            ConfigFileLoader.setSource(
                    new MhttpdBaseConfigurationSource(
                            Bootstrap.getMhttpdBaseFile(), "server-embed.xml"));
        }else {
            ConfigFileLoader.setSource(
                    new MhttpdBaseConfigurationSource(
                            Bootstrap.getMhttpdBaseFile(), getConfigFile()));
        }
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

        //Executor
        digester.addObjectCreate("Server/Service/Executor",
                "cn.ntboy.mhttpd.core.StandardThreadExecutor",
                "className");
        digester.addSetProperties("Server/Service/Executor");
        digester.addSetNext("Server/Service/Executor",
                "addExecutor",
                "cn.ntboy.mhttpd.Executor");

        //Connector
        digester.addRule("Server/Service/Connector",
                new ConnectorCreateRule());
        digester.addSetProperties("Server/Service/Connector");
        digester.addSetNext("Server/Service/Connector",
                "addConnector",
                "cn.ntboy.mhttpd.util.net.Connector");

        //Contexts
        digester.addObjectCreate("Server/Service/Contexts",
                "cn.ntboy.mhttpd.core.StandardContexts",
                "className");
        digester.addSetNext("Server/Service/Contexts",
                "setContexts",
                "cn.ntboy.mhttpd.Contexts");
        digester.addObjectCreate("Server/Service/Contexts/Context",
                "cn.ntboy.mhttpd.core.StandardContext",
                "className");
        digester.addSetProperties("Server/Service/Contexts/Context");
        digester.addSetNext("Server/Service/Contexts/Context",
                "addContext",
                "cn.ntboy.mhttpd.Context");


        //Engine/Container
//        digester.addObjectCreate("Server/Service/Engine",
//                "cn.ntboy.mhttpd.core.StandardEngine",
//                "className");
//        digester.addSetProperties("Server/Service/Engine");
//        digester.addSetNext("Server/Service/Engine",
//                "setContainer",
//                "cn.ntboy.mhttpd.Engine");
//
//        digester.addRuleSet(new HostRoleSet("Server/Service/Engine/Host") );
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
