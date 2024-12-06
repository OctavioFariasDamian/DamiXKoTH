package ar.com.octaviofarias.koth.listeners;

import ar.com.octaviofarias.koth.DamiXKoTH;
import ar.com.octaviofarias.koth.KoTHManager;
import ar.com.octaviofarias.koth.commands.RewardsKoTHCommand;
import ar.com.octaviofarias.koth.model.ActiveKoTH;
import ar.com.octaviofarias.koth.model.KoTH;
import ar.com.octaviofarias.koth.utils.KoTHUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ar.com.octaviofarias.koth.utils.KoTHUtils.sendMessage;

public class KoTHListener implements Listener {


    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        for(ActiveKoTH koTH : KoTHManager.getActiveKoTHs()){
            assert koTH.getKoTH().getFirstPosition() != null;
            assert koTH.getKoTH().getSecondPosition() != null;
            if(KoTHUtils.isLocationInCuboid(player.getLocation(), koTH.getKoTH().getFirstPosition(), koTH.getKoTH().getSecondPosition())){
                if(koTH.getDominating() == player) return;
                if(koTH.getDominating() == null){
                    koTH.setDominatingTime(koTH.getKoTH().getCaptureTime());
                    koTH.setDominating(player);
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        for (String s : DamiXKoTH.getMessages().getMessageList("koth.now-dominating")) {
                            sendMessage(onlinePlayer, s.replace("%player%", player.getName()).replace("%koth%", koTH.getKoTH().getName()));
                        }
                    }
                    return;
                }
            }
            if(koTH.getDominating() == player){
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if(KoTHUtils.isLocationInCuboid(onlinePlayer.getLocation(), koTH.getKoTH().getFirstPosition(), koTH.getKoTH().getSecondPosition())) {
                        koTH.setDominatingTime(koTH.getKoTH().getCaptureTime());
                        koTH.setDominating(player);
                        for (Player op : Bukkit.getOnlinePlayers()) {
                            for (String s : DamiXKoTH.getMessages().getMessageList("koth.now-dominating")) {
                                sendMessage(op, s.replace("%player%", onlinePlayer.getName()).replace("%koth%", koTH.getKoTH().getName()));
                            }
                        }
                        return;
                    }
                }
                koTH.setDominating(null);
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    for (String s : DamiXKoTH.getMessages().getMessageList("koth.no-one-dominating")) {
                        sendMessage(onlinePlayer, s.replace("%koth%", koTH.getKoTH().getName()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        RewardsKoTHCommand.getEditingItemsReward().remove(e.getPlayer());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        Player p = (Player) e.getPlayer();
        if(e.getInventory().getHolder() instanceof RewardsKoTHCommand.RewardsMenu menu){
            KoTH koTH = RewardsKoTHCommand.getEditingItemsReward().get(p);
            if(koTH == null) return;

            List<ItemStack> items = new ArrayList<>(Arrays.asList(e.getInventory().getContents()));

            koTH.getRewards().setItems(items);
            KoTHManager.update(koTH);
            sendMessage(p, DamiXKoTH.getMessages().getMessage("commands.rewards.items-updated").replace("%koth%", koTH.getName()));
            RewardsKoTHCommand.getEditingItemsReward().remove(p);
        }
    }

}
