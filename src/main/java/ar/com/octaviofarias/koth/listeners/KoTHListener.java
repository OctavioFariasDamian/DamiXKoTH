package ar.com.octaviofarias.koth.listeners;

import ar.com.octaviofarias.koth.DamiXKoTH;
import ar.com.octaviofarias.koth.KoTHManager;
import ar.com.octaviofarias.koth.commands.RewardsKoTHCommand;
import ar.com.octaviofarias.koth.model.KoTH;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static ar.com.octaviofarias.koth.utils.KoTHUtils.sendMessage;

public class KoTHListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        RewardsKoTHCommand.getEditingItemsReward().remove(e.getPlayer());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        Player p = (Player) e.getPlayer();
        if(e.getInventory().getHolder() == null) return;
        if(e.getInventory().getHolder() instanceof RewardsKoTHCommand.RewardsMenu){
            KoTH koTH = RewardsKoTHCommand.getEditingItemsReward().get(p);
            if(koTH == null) return;

            List<ItemStack> items = new ArrayList<>();

            for (@Nullable ItemStack content : e.getInventory().getContents()) {
                if(content == null || content.getType() == Material.AIR) continue;
                items.add(content);
            }

            koTH.getRewards().setItems(items);
            KoTHManager.update(koTH);
            sendMessage(p, DamiXKoTH.getMessages().getMessage("commands.rewards.items-updated").replace("%koth%", koTH.getName()));
            RewardsKoTHCommand.getEditingItemsReward().remove(p);
        }
    }

}
