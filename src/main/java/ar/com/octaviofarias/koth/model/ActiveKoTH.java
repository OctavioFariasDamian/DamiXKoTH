package ar.com.octaviofarias.koth.model;


import ar.com.octaviofarias.koth.DamiXKoTH;
import ar.com.octaviofarias.koth.KoTHManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ar.com.octaviofarias.koth.utils.KoTHUtils.sendMessage;

public class ActiveKoTH {
    @NotNull
    @Getter
    private final KoTH koTH;
    @Getter
    private final int activeTime;
    @Getter @Setter
    private int reamingTime, dominatingTime;
    @Getter @Setter
    private Player dominating;

    @Nullable
    private BukkitTask task = null;

    public ActiveKoTH(int activeTime, @NotNull KoTH koTH) {
        this.activeTime = activeTime;
        this.koTH = koTH;
    }

    public void scheduler(){
        reamingTime = activeTime;
        this.dominatingTime = koTH.getCaptureTime();
        task = Bukkit.getScheduler().runTaskTimer(DamiXKoTH.getInstance(), () -> {
            if(reamingTime < 0 || dominatingTime < 0) {
                finish();
                return;
            }
            reamingTime--;
        }, 0L, 20L);
    }

    private void finish() {
        cancel();
        if(dominating != null || dominatingTime < 0){
            koTH.getRewards().give(dominating);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                for (String s : DamiXKoTH.getMessages().getMessageList("koth.finish-with-winner")) {
                    sendMessage(onlinePlayer, s.replace("%player%", dominating.getName()).replace("%koth%", koTH.getName()));
                }
            }
        }else{
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                for (String s : DamiXKoTH.getMessages().getMessageList("koth.finish-without-winner")) {
                    sendMessage(onlinePlayer, s.replace("%koth%", koTH.getName()));
                }
            }
        }
    }

    public void cancel() {
        assert task != null;
        task.cancel();
        KoTHManager.getActiveKoTHs().remove(this);
    }
}
