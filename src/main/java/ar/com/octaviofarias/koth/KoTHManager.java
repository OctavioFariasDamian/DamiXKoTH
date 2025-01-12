package ar.com.octaviofarias.koth;

import ar.com.octaviofarias.koth.model.ActiveKoTH;
import ar.com.octaviofarias.koth.model.KoTH;
import ar.com.octaviofarias.koth.model.KoTHRewards;
import ar.com.octaviofarias.koth.model.KoTHScheduler;
import ar.com.octaviofarias.koth.configuration.KoTHConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

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

    private static final File kothsFolder = new File(DamiXKoTH.getInstance().getDataFolder(), "koths");

    /**
     * Check if a KoTH is started
     *
     * @param koTH the KoTH instance to check
     * @return if the KoTH is started
     */
    public static boolean isKoTHStarted(@NotNull KoTH koTH) {
        return activeKoTHs.stream().anyMatch(activeKoTH -> activeKoTH.getKoTH() == koTH);
    }

    /**
     * Creates a new KoTH and register it to the KoTHs folder file
     *
     * @param name The name of the KoTH to create
     * @throws IOException if the KoTH file couldn't be created
     */
    public static void create(@NotNull String name) throws IOException {
        checkFolder();
        File f = new File(kothsFolder, name+".yml");

        if(!f.exists()) {
            f.createNewFile();
        }

        KoTHConfiguration config = new KoTHConfiguration(name, kothsFolder, false, DamiXKoTH.getInstance());

        config.set("capture-time", 0);
        config.asConfig().createSection("rewards.items");
        config.set("rewards.commands", new ArrayList<>());
        config.set("schedulers", new ArrayList<>());
        config.saveConfig();

        KoTH koTH = new KoTH(name, 0, null, null, new KoTHRewards(new ArrayList<>(), new ArrayList<>()), f, config, new ArrayList<>());
        koTHs.add(koTH);
    }

    /**
     * Check if the KoTHs folder exists, if it doesn't make it
     */
    private static void checkFolder() {
        if(!kothsFolder.exists()) kothsFolder.mkdir();
    }

    /**
     * Get the {@link KoTH} instance from the name
     *
     * @param name The name of the KoTH to search
     * @return {@link Optional<KoTH>} instance, because the KoTH could not exist
     */
    public static Optional<KoTH> getKoTH(@NotNull String name){
        return koTHs.stream().filter(koTH -> koTH.getName().equals(name)).findAny();
    }

    /**
     * Get the {@link ActiveKoTH} instance from a KoTH name
     *
     * @param name The name of the active KoTH to search
     * @return {@link Optional<ActiveKoTH>} instance, because the active KoTH could not exist
     */
    public static Optional<ActiveKoTH> getActiveKoTH(@NotNull String name){
        return activeKoTHs.stream().filter(koTH -> koTH.getKoTH().getName().equals(name)).findAny();

    }
    /**
     * This method deletes a KoTH permanently from the server
     *
     * <p>The method receives the KoTH instance and try to delete the KoTH file
     * and remove it from the KoTHs list</p>
     *
     * @param koth The instance of the KoTH to delete
     *
     * @throws IOException if the received KoTH is started
     */
    public static void delete(@NotNull KoTH koth) throws IOException {
        if(isKoTHStarted(koth)){
            throw new IOException("I can't remove a koth when it is started! ("+koth.getName()+")");
        }
        koth.getFile().delete();
        koTHs.remove(koth);
    }


    /**
     * This method updates the KoTH config file
     *
     * @param koth The instance of the KoTH to update
     */
    public static void update(@NotNull KoTH koth) {
        KoTHConfiguration con = koth.getConfig();
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
            schedulers.add(scheduler.getDay().toString().toLowerCase()+";"+scheduler.getFormatedHour()+":"+scheduler.getFormatedMinutes()+":"+scheduler.getFormatedSeconds()+";"+scheduler.getDuration());
        }
        con.set("schedulers", schedulers);
        con.saveConfig();
    }

    /**
     * This method loads all server KoTHs
     */
    public static void loadKoTHs() {
        if(kothsFolder.exists()){
            if(kothsFolder.listFiles() != null)
                for(File f : Objects.requireNonNull(kothsFolder.listFiles())){
                    if(f.getName().endsWith(".yml")){
                        KoTHConfiguration dc = new KoTHConfiguration(f.getName().replace(".yml", ""), kothsFolder, false, DamiXKoTH.getInstance());
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

                        if(dc.asConfig().isList("schedulers")){
                            for (String s : dc.asConfig().getStringList("schedulers")) {
                                if(s.split(";").length != 3){
                                    DamiXKoTH.getInstance().getSLF4JLogger().warn("The scheduler: '{}' at '{}' has invalid format (dayOfWeek;hour:minutes:seconds;duration)", s, f.getName());
                                    continue;
                                }
                                DayOfWeek day;
                                int h, m, seg, d;

                                try {
                                    day = DayOfWeek.valueOf(s.split(";")[0].toUpperCase());
                                }catch (IllegalArgumentException e){
                                    DamiXKoTH.getInstance().getSLF4JLogger().warn("The day of week: '{}' from scheduler '{}' at '{}' is invalid. Valid: MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY AND SUNDAY", s.split(";")[0], s, f.getName());
                                    continue;
                                }

                                String[] time = s.split(";")[1].split(":");

                                if(time.length != 3){
                                    DamiXKoTH.getInstance().getSLF4JLogger().warn("The time format (hour:minutes:seconds): '{}' from scheduler '{}' at '{}' has invalid format (dayOfWeek;hour:minutes:seconds;duration)", s.split(";")[1], s, f.getName());
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
                                    d = Integer.parseInt(s.split(";")[2]);
                                }catch (NumberFormatException e){
                                    d = -1;
                                    if(!s.split(";")[2].equals("infinite")) {
                                        DamiXKoTH.getInstance().getSLF4JLogger().warn("The duration of active koth: '{}' from scheduler '{}' at '{}' has to be a number!", s.split(";")[2], s, f.getName());
                                        continue;
                                    }
                                }


                                schedulers.add(new KoTHScheduler(day, h, m, seg, d, d == -1));

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

    /**
     * This method checks and reload KoTHs schedulers for their automatic start
     *
     * <p>The method checks all KoTHs and their schedulers and creates the {@link BukkitTask}</p>
     */
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
                        ActiveKoTH activeKoTH = new ActiveKoTH(scheduler.getDuration(), koTH, scheduler.isInfintie());
                        activeKoTH.scheduler();
                        KoTHManager.getActiveKoTHs().add(activeKoTH);
                    }
                }, delay * 20L));
            }
        }
    }

}
