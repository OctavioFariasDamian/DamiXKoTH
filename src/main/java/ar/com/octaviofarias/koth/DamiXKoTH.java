package ar.com.octaviofarias.koth;

import ar.com.octaviofarias.koth.commands.KoTHCommand;
import ar.com.octaviofarias.koth.configuration.MessagesConfig;
import ar.com.octaviofarias.koth.configuration.SettingsConfig;
import ar.com.octaviofarias.koth.listeners.KoTHListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.ZoneId;
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

        try {
            KoTHManager.setZone(ZoneId.of(settings.getSetting("time-zone")));
        } catch (Exception e) {
            getSLF4JLogger().error("The Time Zone in settings.yml file is invalid or null. Without it defined the plugin can't start, please check the config");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        KoTHManager.loadKoTHs();
        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) new KoTHPlaceholderExpansion().register();

        Objects.requireNonNull(getCommand("koth")).setTabCompleter(new KoTHCommand());
        Objects.requireNonNull(getCommand("koth")).setExecutor(new KoTHCommand());

        getServer().getPluginManager().registerEvents(new KoTHListener(), this);
        KoTHManager.checkSchedulers();

    }

    @Override
    public void onDisable() {

    }
}
