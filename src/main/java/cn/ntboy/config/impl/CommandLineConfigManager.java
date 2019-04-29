package cn.ntboy.config.impl;

import cn.ntboy.config.IConfigManager;

import java.util.Set;

class CommandLineConfigManager implements IConfigManager {

    private CommandLineConfigManager() {
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

    private static final CommandLineConfigManager instance = new CommandLineConfigManager();

    public static IConfigManager getInstance(){
        return instance;
    }
}
