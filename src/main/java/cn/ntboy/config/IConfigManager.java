package cn.ntboy.config;

import java.util.Map;
import java.util.Set;

public interface IConfigManager{
    String getConfigItem(String key);

    void setConfigItem(String key, String value);

    void reloadConfig(String[] args);
    void loadConfig(String[] args);

    Set<String> getKeySet();

//    Map<String,String> getConfigMap();
//    void

}
