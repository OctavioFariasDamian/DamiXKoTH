package ar.com.octaviofarias.koth.utils.menu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(e.getClickedInventory() == null) return;
        if(e.getClickedInventory().getHolder() instanceof Menu m){
            e.setCancelled(true);
            for(MenuButton b : m.getButtons()){
                if(e.getSlot() == b.getSlot()) b.getAction().onClick(e.getClick());
            }
        }
    }
}
