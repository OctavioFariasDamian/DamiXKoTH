package ar.com.octaviofarias.koth.configuration;

import ar.com.octaviofarias.koth.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class KoTHConfiguration {

    private final String name;
    private final File file;
    private FileConfiguration config = null;
    private final Plugin plugin;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public KoTHConfiguration(String name, File folder, boolean serverResource, Plugin plugin) {
        this.name = name + ".yml";
        this.file = new File(folder, this.name);
        this.plugin = plugin;
        if(serverResource)
            registerConfig();
        else {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException("Couldn't create file: " + file.getAbsolutePath(), e);
                }
            }
            config = YamlConfiguration.loadConfiguration(file);
        }

    }

    public void set(String path, Object value){
        asConfig().set(path, value);
    }

    public Location getLocation(String s)
    {
        if(getString(s+".world") == null || Bukkit.getWorld(getString(s+".world")) == null) return null;
        return new Location(Bukkit.getWorld(s+".world"), getDouble(s+".x"),
                getDouble(s+".y"),
                getDouble(s+".z"),
                (float) getDouble(s+".yaw"),
                (float) getDouble(s+".pitch"));
    }

    @NotNull
    public String getString(String path, String def){
        if(asConfig().getString(path) == null){
            set(path, def);
            saveConfig();
            return def;
        }
        return Objects.requireNonNull(asConfig().getString(path));
    }

    public int getInt(String path, int def){
        if(!asConfig().contains(path)){
            set(path, def);
            saveConfig();
            return def;
        }
        return asConfig().getInt(path);
    }

    public double getDouble(String path, double def){
        if(!asConfig().contains(path)){
            set(path, def);
            saveConfig();
            return def;
        }
        return asConfig().getDouble(path);
    }

    public int getInt(String path){
        return asConfig().getInt(path);
    }

    public double getDouble(String path){
        return asConfig().getDouble(path);
    }

    public Material getMaterial(String path, Material def){
        if(!asConfig().contains(path)){
            set(path, def.name());
            saveConfig();
            return def;
        }
        return Material.matchMaterial(path);
    }

    @SuppressWarnings("unchecked") @Nullable
    public <T> List<T> getList(String path, Class<T> clazz) {
        if (!config.contains(path)) {
            return null;
        }

        List<?> rawList = config.getList(path);
        if (rawList == null) {
            return null;
        }

        return rawList.stream()
                .filter(clazz::isInstance)
                .map(element -> (T) element)
                .collect(Collectors.toList());
    }


    @Nullable
    public ItemBuilder getItem(String path){
        if(!asConfig().contains(path)) return null;
        Material mat = getMaterial(path+".type", Material.STONE);
        int amount = getInt(path+".amount", 1);
        String displayName = null;
        if(asConfig().contains(path+".name")){
            displayName = getString(path+".name");
        }
        List<String> lore = getList(path+".lore", String.class);
        List<String> flags = getList(path+".flags", String.class);

        ItemBuilder b = new ItemBuilder(mat);
        b.setAmount(amount);
        if(displayName != null) b.setDisplayName(displayName);
        if(lore != null) b.setLore(lore);
        if(flags != null) {
            for(String flag : flags){
                ItemFlag itf = null;
                try {
                    itf = ItemFlag.valueOf(flag.toUpperCase());
                }catch (IllegalArgumentException e){
                    plugin.getLogger().warning("Invalid ItemFlag in: " + file.getAbsolutePath() + ", in path: " + path + ": " + flag);
                }
                if(itf != null) b.addItemFlag(itf);
            }
        }
        return b;
    }

    public String getString(String s) {
        if(asConfig().getString(s) == null){
            return null;
        }
        return Objects.requireNonNull(asConfig().getString(s));
    }

    public void registerConfig() {
        if (!file.exists()) {
            plugin.saveResource(name, false);
        }
        try {
            config = YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't load file: " + file.getAbsolutePath(), e);
        }
    }

    public void saveConfig() {
        try {
            if (config != null) {
                config.save(file);
            }
        } catch (IOException e) {
            throw new RuntimeException("Couldn't save file: " + file.getAbsolutePath(), e);
        }
    }

    public FileConfiguration asConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
        // Cargar valores predeterminados si es necesario
        // InputStream defConfigStream = RambleCore.getInstance().getResource(name);
        // if (defConfigStream != null) {
        //     YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
        //     config.setDefaults(defConfig);
        // }
    }
}
