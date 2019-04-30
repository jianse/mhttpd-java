package cn.ntboy.config.impl;

import cn.ntboy.config.IConfigManager;

import java.util.Set;

class ConfigFileConfigManager implements IConfigManager{

    private ConfigFileConfigManager() {
    }

    @Override
    public String getConfigItem(String key) {
        return null;
    }

    @Override
    public void setConfigItem(String key, String value) {

    }

    @Override
    public void reloadConfig(String[] args) {

    }

    @Override
    public void loadConfig(String[] args) {

    }

    @Override
    public Set<String> getKeySet() {
        return null;
    }

    private static final ConfigFileConfigManager instance = new ConfigFileConfigManager();

    public static IConfigManager getInstance() {
        return instance;
    }
}
