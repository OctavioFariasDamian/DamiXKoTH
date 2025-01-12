package ar.com.octaviofarias.koth.hook;

import org.bukkit.Bukkit;

public interface KoTHHook {

    String getPluginName();
    void onEnable();

    default boolean isAvailable(){
        return Bukkit.getPluginManager().isPluginEnabled(getPluginName());
    }
}
