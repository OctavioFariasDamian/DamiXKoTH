package ar.com.octaviofarias.koth.model;


import ar.com.octaviofarias.koth.DamiXKoTH;
import ar.com.octaviofarias.koth.KoTHManager;
import ar.com.octaviofarias.koth.utils.KoTHUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ar.com.octaviofarias.koth.utils.KoTHUtils.sendMessage;

/**
 * This class manage the information and task of an active KoTH
 */
public class ActiveKoTH {
    @NotNull
    @Getter
    private final KoTH koTH;
    private final boolean infinite;
    @Getter
    private final int maxTime;
    @Getter @Setter
    private int reamingTime, dominatingTime;
    @Getter @Setter
    private Player dominating;

    @Nullable
    private BukkitTask task = null;

    public ActiveKoTH(int maxTime, @NotNull KoTH koTH, boolean infinite) {
        this.maxTime = maxTime;
        this.koTH = koTH;
        this.infinite = infinite;
    }

    /**
     * This method start the scheduling task of the active KoTH
     *
     * <p>In the task, every second, is checked the capturer and the reaming time</p>
     */
    public void scheduler(){
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            String path = infinite ? "koth.start-infinite" : "koth.start";
            for (String s : DamiXKoTH.getMessages().getMessageList(path)) {
                sendMessage(onlinePlayer, s.replace("%koth%", getKoTH().getName())
                        .replace("%duration%", String.valueOf(maxTime)));
            }
        }
        reamingTime = maxTime;
        this.dominatingTime = koTH.getCaptureTime();
        KoTHManager.checkSchedulers();
        task = Bukkit.getScheduler().runTaskTimer(DamiXKoTH.getInstance(), () -> {
            if((reamingTime < 0 && !infinite) || dominatingTime < 0) {
                finish();
                return;
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                assert getKoTH().getFirstPosition() != null;
                assert getKoTH().getSecondPosition() != null;
                if(KoTHUtils.isLocationInCuboid(player.getLocation(), getKoTH().getFirstPosition(), getKoTH().getSecondPosition())){
                    if(getDominating() == player) continue;
                    if(getDominating() == null){
                        setDominatingTime(koTH.getCaptureTime());
                        setDominating(player);
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            for (String s : DamiXKoTH.getMessages().getMessageList("koth.now-dominating")) {
                                sendMessage(onlinePlayer, s.replace("%player%", player.getName()).replace("%koth%", getKoTH().getName()));
                            }
                        }
                        continue;
                    }
                }
                if(getDominating() == player){
                    boolean var = false;
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if(KoTHUtils.isLocationInCuboid(onlinePlayer.getLocation(), getKoTH().getFirstPosition(), getKoTH().getSecondPosition())) {
                            setDominatingTime(koTH.getCaptureTime());
                            setDominating(player);
                            var = true;
                            for (Player op : Bukkit.getOnlinePlayers()) {
                                for (String s : DamiXKoTH.getMessages().getMessageList("koth.now-dominating")) {
                                    sendMessage(op, s.replace("%player%", onlinePlayer.getName()).replace("%koth%", getKoTH().getName()));
                                }
                            }

                        }
                    }
                    if(!var) {
                        setDominating(null);
                        setDominatingTime(getKoTH().getCaptureTime());
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            for (String s : DamiXKoTH.getMessages().getMessageList("koth.no-one-dominating")) {
                                sendMessage(onlinePlayer, s.replace("%koth%", koTH.getName()));
                            }
                        }

                    }
                }

            }

            if(!infinite) reamingTime--;
            if(dominating != null) dominatingTime--;
        }, 0L, 20L);
    }

    /**
     * This method finish the active KoTH giving rewards and sending finish breadcast
     */
    private void finish() {
        stop();
        KoTHManager.checkSchedulers();
        if(dominating != null && dominatingTime < 0){
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

    /**
     * This method stops the active KoTH's task and removes from the active KoTHS list
     */
    public void stop() {
        assert task != null;
        task.cancel();
        KoTHManager.getActiveKoTHs().remove(this);
    }
}
