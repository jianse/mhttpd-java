package cn.ntboy.config.impl;

import cn.ntboy.config.IConfigManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class BuiltInConfigManager implements IConfigManager{

    private static final BuiltInConfigManager instance = new BuiltInConfigManager();

    private Map<String, String> config = new HashMap<String, String>();

    private BuiltInConfigManager() {
        config.put("port", "18080");
        config.put("maxUser", "8");
    }

    public Set<String> getKeySet() {
        return config.keySet();
    }

    public String getConfigItem(String key) {
        return config.get(key);
    }

    public void setConfigItem(String key, String value) {
        config.put(key, value);
    }

    @Override
    public void reloadConfig(String[] args) {

    }

    public void loadConfig(String[] args) {

    }

    public static IConfigManager getInstance() {
        return instance;
    }
}
