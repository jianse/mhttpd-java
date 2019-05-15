package cn.ntboy.mhttpd.util.file;

public class ConfigFileLoader{
    private static ConfigurationSource source;

    private ConfigFileLoader() {
    }

    public static ConfigurationSource getSource() {

        if (ConfigFileLoader.source == null) {
            throw new IllegalStateException();
        }
        return source;
    }

    public static void setSource(ConfigurationSource source) {
        if (ConfigFileLoader.source == null) {
            ConfigFileLoader.source = source;
        }
    }

}
