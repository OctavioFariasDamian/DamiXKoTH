package ar.com.octaviofarias.koth.commands;

import ar.com.octaviofarias.koth.DamiXKoTH;
import ar.com.octaviofarias.koth.KoTHManager;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.List;

import static ar.com.octaviofarias.koth.utils.KoTHUtils.sendMessage;

public class CreateKoTHCommand implements KoTHSubCommand{

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public boolean canConsoleUse() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length == 0){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.create.usage"));
            return;
        }
        String name = args[0];
        if(!StringUtils.isAlphanumeric(name)){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.create.invalid-name").replace("%name%", name));
            return;
        }

        if(KoTHManager.getKoTH(name).isPresent()){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.create.already-exists"));
            return;
        }

        try {
            KoTHManager.create(name);
        }catch (IOException e){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.create.error"));
            throw new RuntimeException(e);
        }
        sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.create.successfully").replace("%name%", name));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
