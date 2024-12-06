package ar.com.octaviofarias.koth.configuration;

import ar.com.octaviofarias.koth.utils.configuration.DamianConfig;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

public class MessagesConfig extends DamianConfig {
    private final Object2ObjectOpenHashMap<String, String> messagesDefault = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<String, String> messages = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<String, List<String>> messagesListDefault = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<String, List<String>> messagesList = new Object2ObjectOpenHashMap<>();

    public MessagesConfig(String name, File folder, boolean serverResource, Plugin plugin) {
        super(name, folder, serverResource, plugin);
        registerConfig();
        for(String key : asConfig().getKeys(true)){
            if(asConfig().isList(key))
                messagesList.put(key, asConfig().getStringList(key));
            else messages.put(key, asConfig().getString(key));
        }
    }

    public String getMessage(String path){
        if(!messages.containsKey(path)){
            if(!messagesDefault.containsKey(path)){
                return "Unknown message: " + path;
            }
            return messagesDefault.get(path);
        }
        return messages.get(path);
    }

    public List<String> getMessageList(String path){
        if(!messagesList.containsKey(path)){
            if(!messagesListDefault.containsKey(path)){
                return List.of("Unknown message: " + path);
            }
            return messagesListDefault.get(path);
        }
        return messagesList.get(path);
    }

    private void addDefault(String path, String message){
        messagesDefault.put(path, message);
    }

    private void addListDefault(String path, List<String> messages){
        messagesListDefault.put(path, messages);
    }
}
