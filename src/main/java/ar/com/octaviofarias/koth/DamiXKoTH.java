package ar.com.octaviofarias.koth;

import ar.com.octaviofarias.koth.commands.KoTHCommand;
import ar.com.octaviofarias.koth.configuration.MessagesConfig;
import ar.com.octaviofarias.koth.configuration.SettingsConfig;
import ar.com.octaviofarias.koth.listeners.KoTHListener;
import ar.com.octaviofarias.koth.utils.menu.MenuListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class DamiXKoTH extends JavaPlugin {

    @Getter
    private static DamiXKoTH instance;

    @Getter
    private static MessagesConfig messages;
    @Getter
    private static SettingsConfig settings;

    @Override
    public void onEnable() {
        instance = this;
        messages = new MessagesConfig("messages", getDataFolder(), true, this);
        settings = new SettingsConfig("settings", getDataFolder(), true, this);

        KoTHManager.loadKoTHs();

        Objects.requireNonNull(getCommand("koth")).setTabCompleter(new KoTHCommand());
        Objects.requireNonNull(getCommand("koth")).setExecutor(new KoTHCommand());

        getServer().getPluginManager().registerEvents(new KoTHListener(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
    }

    @Override
    public void onDisable() {

    }
}
