package ar.com.octaviofarias.koth.hook;

import ar.com.octaviofarias.koth.hook.placeholderapi.HookPlaceholderAPI;

import java.util.ArrayList;
import java.util.List;

public class HooksManager {

    private static final List<KoTHHook> hooks = new ArrayList<>();

    public static void setup(){
        hooks.add(new HookPlaceholderAPI());

        for (KoTHHook hook : hooks) {
            if(hook.isAvailable()) hook.onEnable();
        }

    }
}
