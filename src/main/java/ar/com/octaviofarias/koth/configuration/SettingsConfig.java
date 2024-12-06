package ar.com.octaviofarias.koth.configuration;

import ar.com.octaviofarias.koth.utils.configuration.DamianConfig;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

public class SettingsConfig extends DamianConfig {
    private final Object2ObjectOpenHashMap<String, String> settingsDefault = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<String, String> settings = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<String, List<String>> settingsListDefault = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<String, List<String>> settingsList = new Object2ObjectOpenHashMap<>();

    public SettingsConfig(String name, File folder, boolean serverResource, Plugin plugin) {
        super(name, folder, serverResource, plugin);
        registerConfig();
        for(String key : asConfig().getKeys(true)){
            if(asConfig().isList(key))
                settingsList.put(key, asConfig().getStringList(key));
            else settings.put(key, asConfig().getString(key));
        }
    }

    public String getSetting(String path){
        if(!settings.containsKey(path)){
            if(!settingsDefault.containsKey(path)){
                return "Unknown setting: " + path;
            }
            return settingsDefault.get(path);
        }
        return settings.get(path);
    }

    public List<String> getSettingList(String path){
        if(!settingsList.containsKey(path)){
            if(!settingsListDefault.containsKey(path)){
                return List.of("Unknown setting list: " + path);
            }
            return settingsListDefault.get(path);
        }
        return settingsList.get(path);
    }

    private void addDefault(String path, String message){
        settingsDefault.put(path, message);
    }

    private void addListDefault(String path, List<String> settings){
        settingsListDefault.put(path, settings);
    }
}
