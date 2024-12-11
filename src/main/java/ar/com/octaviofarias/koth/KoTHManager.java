package ar.com.octaviofarias.koth;

import ar.com.octaviofarias.koth.model.ActiveKoTH;
import ar.com.octaviofarias.koth.model.KoTH;
import ar.com.octaviofarias.koth.model.KoTHRewards;
import ar.com.octaviofarias.koth.model.KoTHScheduler;
import ar.com.octaviofarias.koth.utils.configuration.DamianConfig;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"ResultOfMethodCallIgnored", "LoggingSimilarMessage"})
public class KoTHManager {
    @Getter
    private static final List<KoTH> koTHs = new ArrayList<>();
    @Getter
    private static final List<ActiveKoTH> activeKoTHs = new ArrayList<>();
    private static final List<BukkitTask> tasks = new ArrayList<>();

    public static boolean isKoTHStarted(KoTH koTH) {
        for (ActiveKoTH activeKoTH : activeKoTHs) {
            if (activeKoTH.getKoTH() == koTH) return true;
        }
        return false;
    }

    public static void create(String name) throws RuntimeException{
        File folder = new File(DamiXKoTH.getInstance().getDataFolder(), "koths");
        if(!folder.exists()) folder.mkdirs();
        File f = new File(folder, name+".yml");

        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        DamianConfig config = new DamianConfig(name, folder, false, DamiXKoTH.getInstance());

        config.set("capture-time", 0);
        config.asConfig().createSection("rewards.items");
        config.set("rewards.commands", new ArrayList<>());
        config.set("schedulers", new ArrayList<>());
        config.saveConfig();
        KoTH koTH = new KoTH(name, 0, null, null, new KoTHRewards(new ArrayList<>(), new ArrayList<>()), f, config, new ArrayList<>());
        koTHs.add(koTH);
    }

    public static KoTH getKoTH(String name){
        for (KoTH koTH : koTHs) {
            if(koTH.getName().equalsIgnoreCase(name)) return koTH;
        }
        return null;
    }

    public static ActiveKoTH getActiveKoTH(String name){
        for (ActiveKoTH koTH : activeKoTHs) {
            if(koTH.getKoTH().getName().equalsIgnoreCase(name)) return koTH;
        }
        return null;
    }

    public static void remove(KoTH koth) {
        if(isKoTHStarted(koth)){
            return;
        }
        koth.getFile().delete();
        koTHs.remove(koth);
    }

    public static void update(KoTH koth) {
        DamianConfig con = koth.getConfig();
        con.set("capture-time", koth.getCaptureTime());
        con.set("rewards.items", new ArrayList<>());
        for (int i = 0; i < koth.getRewards().getItems().size(); i++) {
            con.set("rewards.items."+i, koth.getRewards().getItems().get(i));
        }
        con.set("rewards.commands", koth.getRewards().getCommands());
        if(koth.getFirstPosition() != null) con.set("first-point", koth.getFirstPosition());
        if(koth.getSecondPosition() != null) con.set("second-point", koth.getSecondPosition());
        List<String> schedulers = new ArrayList<>();
        for (KoTHScheduler scheduler : koth.getSchedulers()) {
            schedulers.add(scheduler.getDay().toString().toLowerCase()+";"+scheduler.getHour()+":"+scheduler.getMinutes()+":"+scheduler.getSeconds()+";"+scheduler.getDuration());
        }
        con.set("schedulers", schedulers);
        con.saveConfig();
    }

    public static void loadKoTHs() {
        File folder = new File(DamiXKoTH.getInstance().getDataFolder(), "koths");
        if(folder.exists()){
            if(folder.listFiles() != null)
                for(File f : Objects.requireNonNull(folder.listFiles())){
                    if(f.getName().endsWith(".yml")){
                        DamianConfig dc = new DamianConfig(f.getName().replace(".yml", ""), folder, false, DamiXKoTH.getInstance());
                        int ct = dc.getInt("capture-time", 0);
                        List<ItemStack> items = new ArrayList<>();
                        if(dc.asConfig().isConfigurationSection("rewards.items")){
                            ConfigurationSection itemsSec = dc.asConfig().getConfigurationSection("rewards.items");
                            assert itemsSec != null;
                            for(String key : itemsSec.getKeys(false)){
                                if(dc.asConfig().isItemStack("rewards.items."+key)){
                                    items.add(dc.asConfig().getItemStack("rewards.items."+key));
                                }
                            }
                        }

                        List<KoTHScheduler> schedulers = new ArrayList<>();

                        if(dc.getList("schedulers", String.class) != null){
                            for (String s : Objects.requireNonNull(dc.getList("schedulers", String.class))) {
                                if(s.split(";").length != 3){
                                    DamiXKoTH.getInstance().getSLF4JLogger().warn("The scheduler: '{}' at '{}' has invalid format (dayofweek;hour:minutes:seconds;duration)", s, f.getName());
                                    continue;
                                }
                                DayOfWeek day;
                                int h, m, seg, d;

                                try {
                                    day = DayOfWeek.valueOf(s.split(";")[0]);
                                }catch (IllegalArgumentException e){
                                    DamiXKoTH.getInstance().getSLF4JLogger().warn("The day of week: '{}' from scheduler '{}' at '{}' is invalid. Valid: MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY AND SUNDAY", s.split(";")[0], s, f.getName());
                                    continue;
                                }

                                String[] time = s.split(";")[1].split(":");
                                if(time.length != 3){
                                    DamiXKoTH.getInstance().getSLF4JLogger().warn("The time format (hour:minutes:seconds): '{}' from scheduler '{}' at '{}' has invalid format (dayofweek;hour:minutes:seconds;duration)", s.split(";")[1], s, f.getName());
                                    continue;
                                }

                                try{
                                    h = Integer.parseInt(time[0]);
                                    m = Integer.parseInt(time[1]);
                                    seg = Integer.parseInt(time[2]);
                                }catch (NumberFormatException e){
                                    DamiXKoTH.getInstance().getSLF4JLogger().warn("The time format (hour:minutes:seconds): '{}' from scheduler '{}' at '{}' has invalid format!", s.split(";")[1], s, f.getName());
                                    continue;
                                }

                                try{
                                    d = Integer.parseInt(time[2]);
                                }catch (NumberFormatException e){
                                    DamiXKoTH.getInstance().getSLF4JLogger().warn("The duration of active koth: '{}' from scheduler '{}' at '{}' has to be a number!", s.split(";")[2], s, f.getName());
                                    continue;
                                }
                                schedulers.add(new KoTHScheduler(day, h, m, seg, d));

                            }
                        }

                        List<String> commands = new ArrayList<>();
                        if(dc.getList("rewards.commands", String.class) != null) commands = new ArrayList<>(Objects.requireNonNull(dc.getList("rewards.commands", String.class)));
                        Location l1 = null, l2 = null;
                        if(dc.asConfig().isLocation("first-point")) l1 = dc.asConfig().getLocation("first-point");
                        if(dc.asConfig().isLocation("second-point")) l2 = dc.asConfig().getLocation("second-point");
                        KoTH koth = new KoTH(f.getName().replace(".yml", ""),
                                ct, l1, l2, new KoTHRewards(commands, items), f, dc, schedulers);
                        koTHs.add(koth);
                    }
                }
        }
    }

    @Getter
    @Setter
    private static ZoneId zone;

    public static void checkSchedulers(){
        for (BukkitTask task : tasks) {
            task.cancel();
        }
        tasks.clear();
        for (KoTH koTH : getKoTHs()) {
            if(isKoTHStarted(koTH)) continue;
            for (KoTHScheduler scheduler : koTH.getSchedulers()) {
                ZonedDateTime now = ZonedDateTime.now(zone);
                ZonedDateTime nextEvent = now.with(scheduler.getDay())
                        .withHour(scheduler.getHour())
                        .withMinute(scheduler.getMinutes())
                        .withSecond(scheduler.getSeconds())
                        .withNano(0);

                if (now.isAfter(nextEvent)) {
                    nextEvent = nextEvent.plusWeeks(1);
                }

                long delay = TimeUnit.SECONDS.convert(nextEvent.toEpochSecond() - now.toEpochSecond(), TimeUnit.SECONDS);

                tasks.add(Bukkit.getScheduler().runTaskLater(DamiXKoTH.getInstance(), () -> {
                    if(!isKoTHStarted(koTH)) {
                        ActiveKoTH activeKoTH = new ActiveKoTH(scheduler.getDuration(), koTH);
                        activeKoTH.scheduler();
                        KoTHManager.getActiveKoTHs().add(activeKoTH);
                    }
                }, delay * 20L));
            }
        }
    }

}
