package ar.com.octaviofarias.koth.utils.menu;

import ar.com.octaviofarias.koth.utils.ItemBuilder;

public interface MenuButton {

    int getSlot();
    ItemBuilder getItem();
    ButtonAction getAction();
}
