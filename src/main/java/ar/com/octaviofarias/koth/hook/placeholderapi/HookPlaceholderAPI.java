package ar.com.octaviofarias.koth.hook.placeholderapi;

import ar.com.octaviofarias.koth.hook.KoTHHook;

public class HookPlaceholderAPI implements KoTHHook {

    @Override
    public String getPluginName() {
        return "PlaceholderAPI";
    }

    @Override
    public void onEnable() {
        new KoTHPlaceholderExpansion().register();
    }

}
