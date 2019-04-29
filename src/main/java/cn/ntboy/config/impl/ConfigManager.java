package cn.ntboy.config.impl;

import cn.ntboy.config.IConfigManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigManager implements IConfigManager {

    private static final IConfigManager instance=new ConfigManager();

    private static final String BUILT_IN_CONFIG_MANAGER= "BUILT_IN_CONFIG_MANAGER";
    private static final String CONFIG_FILE_CONFIG_MANAGER = "CONFIG_FILE_CONFIG_MANAGER";
    private static final String COMMAND_LINE_CONFIG_MANAGER= "COMMAND_LINE_CONFIG_MANAGER";

    private Map<String,IConfigManager> registration = new HashMap<>();
    
    private Map<String,String> config = new HashMap<String, String>();

    private ConfigManager() {
        registration.put(BUILT_IN_CONFIG_MANAGER,BuiltInConfigManager.getInstance());
        registration.put(CONFIG_FILE_CONFIG_MANAGER,ConfigFileConfigManager.getInstance());
        registration.put(COMMAND_LINE_CONFIG_MANAGER,CommandLineConfigManager.getInstance());

    }

    public String getConfigItem(String key) {
        return config.get(key);
    }

    public void setConfigItem(String key,String value){
        config.put(key,value);
    }

    @Override
    public void reloadConfig(String[] args) {

    }

    @Override
    public Set<String> getKeySet() {
        return config.keySet();
    }

    public void loadConfig(String[] args) {
        //copy built-in
        IConfigManager manager = registration.get(BUILT_IN_CONFIG_MANAGER);
        Set<String> keySet = manager.getKeySet();
        for(String key:keySet){
            this.setConfigItem(key,manager.getConfigItem(key));
        }

        //copy
    }


    public static IConfigManager getInstance(){
        return instance;
    }
}
