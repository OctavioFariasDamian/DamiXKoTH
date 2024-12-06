package ar.com.octaviofarias.koth.utils.menu;

import ar.com.octaviofarias.koth.utils.KoTHUtils;
import ar.com.octaviofarias.koth.utils.ItemBuilder;
import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public abstract class Menu implements InventoryHolder {

    private final Inventory inventory;
    @Getter
    private final Set<MenuButton> buttons = new HashSet<>();

    @SuppressWarnings("deprecation")
    public Menu(String title, int rows) {
        inventory = Bukkit.createInventory(this, rows*9, KoTHUtils.color(title));
    }

    public void open(Player player){
        write();
        player.openInventory(inventory);
    }

    public void refresh() {
        inventory.clear();
        write();
    }

    public void write(){
        for(MenuButton button : buttons){
            inventory.setItem(button.getSlot(), button.getItem().build());
        }
    }

    public void addButton(MenuButton button){
        buttons.add(button);
    }
    public void addButton(int slot, ItemBuilder builder, ButtonAction action){
        buttons.add(new MenuButton() {
            @Override
            public int getSlot() {
                return slot;
            }

            @Override
            public ItemBuilder getItem() {
                return builder;
            }

            @Override
            public ButtonAction getAction() {
                return action;
            }
        });
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
