package ar.com.octaviofarias.koth;

import ar.com.octaviofarias.koth.model.ActiveKoTH;
import ar.com.octaviofarias.koth.model.KoTH;
import ar.com.octaviofarias.koth.model.KoTHScheduler;
import ar.com.octaviofarias.koth.utils.KoTHUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

public class KoTHPlaceholderExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "damixkoth";
    }

    @Override
    public @NotNull String getAuthor() {
        return "DamianFarias";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if(params.equalsIgnoreCase("active_koths_count")) {
            return String.valueOf(KoTHManager.getActiveKoTHs().size());
        }
        else if(params.equalsIgnoreCase("active_koths_list")){
            StringBuilder ret = new StringBuilder();
            for (ActiveKoTH activeKoTH : KoTHManager.getActiveKoTHs()) {
                ret.append(activeKoTH.getKoTH().getName()).append(", ");
            }

            ret = new StringBuilder(ret.toString().trim());
            return ret.isEmpty() ? "" : ret.substring(0, ret.length()-1);
        }
        else if(params.toLowerCase().startsWith("active_time_")){
            String name = params.replace("active_time_", "");

            if(name.startsWith("[")){
                int index = Integer.parseInt(name.replace("[", ""));
                if(index < 0 || index >= KoTHManager.getActiveKoTHs().size()) return "";
                ActiveKoTH ak = KoTHManager.getActiveKoTHs().get(index);
                if(ak != null){
                    return KoTHUtils.formatTime(ak.getActiveTime(), DamiXKoTH.getSettings().getSetting("time-format"));
                }
            }

            ActiveKoTH ak = KoTHManager.getActiveKoTH(name);
            if(ak != null){
                return KoTHUtils.formatTime(ak.getActiveTime(), DamiXKoTH.getSettings().getSetting("time-format"));
            }
        }
        else if(params.toLowerCase().startsWith("reaming_time_")){
            String name = params.replace("reaming_time_", "");

            if(name.startsWith("[")){
                int index = Integer.parseInt(name.replace("[", ""));
                if(index < 0 || index >= KoTHManager.getActiveKoTHs().size()) return "";
                ActiveKoTH ak = KoTHManager.getActiveKoTHs().get(index);
                if(ak != null){
                    return KoTHUtils.formatTime(ak.getReamingTime(), DamiXKoTH.getSettings().getSetting("time-format"));
                }
            }

            ActiveKoTH ak = KoTHManager.getActiveKoTH(name);
            if(ak != null){
                return KoTHUtils.formatTime(ak.getReamingTime(), DamiXKoTH.getSettings().getSetting("time-format"));
            }
        }
        else if(params.toLowerCase().startsWith("dominating_time_")){
            String name = params.replace("dominating_time_", "");
            if(name.startsWith("[")){
                int index = Integer.parseInt(name.replace("[", ""));
                if(index < 0 || index >= KoTHManager.getActiveKoTHs().size()) return "";
                ActiveKoTH ak = KoTHManager.getActiveKoTHs().get(index);
                if(ak != null){
                    return KoTHUtils.formatTime(ak.getDominatingTime(), DamiXKoTH.getSettings().getSetting("time-format"));
                }
            }
            ActiveKoTH ak = KoTHManager.getActiveKoTH(name);
            if(ak != null){
                return KoTHUtils.formatTime(ak.getDominatingTime(), DamiXKoTH.getSettings().getSetting("time-format"));
            }
        }
        else if(params.equalsIgnoreCase("next")){
            KoTH selected = null;
            long min = Long.MAX_VALUE;
            ZonedDateTime dt = null;
            for (KoTH koTH : KoTHManager.getKoTHs()) {
                if (KoTHManager.isKoTHStarted(koTH)) continue;
                for (KoTHScheduler scheduler : koTH.getSchedulers()) {
                    ZonedDateTime now = ZonedDateTime.now(KoTHManager.getZone());
                    ZonedDateTime nextEvent = now.with(scheduler.getDay())
                            .withHour(scheduler.getHour())
                            .withMinute(scheduler.getMinutes())
                            .withSecond(scheduler.getSeconds())
                            .withNano(0);

                    if (now.isAfter(nextEvent)) {
                        nextEvent = nextEvent.plusWeeks(1);
                    }

                    long delay = TimeUnit.SECONDS.convert(nextEvent.toEpochSecond() - now.toEpochSecond(), TimeUnit.SECONDS);
                    if(delay < min){
                        dt = nextEvent;
                        selected = koTH;
                        min = delay;
                    }
                }
            }

            if(selected == null){
                return "";
            }

            ZonedDateTime now = ZonedDateTime.now(KoTHManager.getZone());

            Duration duration = Duration.between(now, dt);

            long totalSeconds = duration.getSeconds();
            long hours = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            long seconds = totalSeconds % 60;

            return hours+"h " + minutes+"m " + seconds +"s";
        }
        else if(params.toLowerCase().startsWith("dominating_name_")){
            String name = params.replace("dominating_name_", "");

            if(name.startsWith("[")){
                int index = Integer.parseInt(name.replace("[", ""));
                if(index < 0 || index >= KoTHManager.getActiveKoTHs().size()) return "";
                ActiveKoTH ak = KoTHManager.getActiveKoTHs().get(index);
                if(ak != null){
                    return ak.getDominating() == null ? DamiXKoTH.getSettings().getSetting("nobody-placeholder") : ak.getDominating().getName();
                }
            }

            ActiveKoTH ak = KoTHManager.getActiveKoTH(name);
            if(ak != null){
                return ak.getDominating() == null ? DamiXKoTH.getSettings().getSetting("nobody-placeholder") : ak.getDominating().getName();
            }
        }

        return "";
    }
}
