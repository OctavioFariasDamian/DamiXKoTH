package ar.com.octaviofarias.koth.model;

import ar.com.octaviofarias.koth.KoTHManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * This class contains the items and commands rewards of a KoTH
 */
@Data
@AllArgsConstructor
public class KoTHRewards {

    private List<String> commands;
    private List<ItemStack> items;

    /**
     * This method give items to the player and execute commandos from console at player
     *
     * @param player The player to give the rewards
     */
    public void give(Player player){
        for(String s : commands){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null ?
                            PlaceholderAPI.setPlaceholders(player, s.replace("%player%", player.getName())) : s.replace("%player%", player.getName())));
        }

        for (ItemStack item : items) {
            if(player.getInventory().firstEmpty() != -1){
                player.getInventory().addItem(item);
            }else{
                player.getWorld().dropItem(player.getLocation().add(0, 1, 0), item);
            }
        }


    }

}
