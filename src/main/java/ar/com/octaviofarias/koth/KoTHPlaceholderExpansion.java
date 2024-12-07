package ar.com.octaviofarias.koth;

import ar.com.octaviofarias.koth.model.ActiveKoTH;
import ar.com.octaviofarias.koth.utils.KoTHUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            ActiveKoTH ak = KoTHManager.getActiveKoTH(name);
            if(ak != null){
                return KoTHUtils.formatTime(ak.getActiveTime(), DamiXKoTH.getSettings().getSetting("dominating-time-format"));
            }
        }
        else if(params.toLowerCase().startsWith("reaming_time_")){
            String name = params.replace("reaming_time_", "");
            ActiveKoTH ak = KoTHManager.getActiveKoTH(name);
            if(ak != null){
                return KoTHUtils.formatTime(ak.getReamingTime(), DamiXKoTH.getSettings().getSetting("dominating-time-format"));
            }
        }
        else if(params.toLowerCase().startsWith("dominating_time_")){
            String name = params.replace("dominating_time_", "");
            ActiveKoTH ak = KoTHManager.getActiveKoTH(name);
            if(ak != null){
                return KoTHUtils.formatTime(ak.getDominatingTime(), DamiXKoTH.getSettings().getSetting("dominating-time-format"));
            }
        }
        else if(params.toLowerCase().startsWith("dominating_name_")){
            String name = params.replace("dominating_name_", "");
            ActiveKoTH ak = KoTHManager.getActiveKoTH(name);
            if(ak != null){
                return ak.getDominating() == null ? DamiXKoTH.getSettings().getSetting("nobody-placeholder") : ak.getDominating().getName();
            }
        }

        return "";
    }
}
