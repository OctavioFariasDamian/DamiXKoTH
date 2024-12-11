package ar.com.octaviofarias.koth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
@AllArgsConstructor
public class KoTHRewards {
    private List<String> commands;
    private List<ItemStack> items;

    public void give(Player player){
        for(String s : commands){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null ?
                            PlaceholderAPI.setPlaceholders(player, s.replace("%player%", player.getName())) : s.replace("%player%", player.getName())));
        }
        int i = 0;
        for (ItemStack item : items) {
            if(item == null)
            {
                items.remove(i);
                continue;
            }
            if(player.getInventory().firstEmpty() != -1){
                player.getInventory().addItem(item);
            }else{
                player.getWorld().dropItem(player.getLocation().add(0, 1, 0), item);
            }
            i++;
        }
    }

}
