package ar.com.octaviofarias.koth.commands;

import ar.com.octaviofarias.koth.DamiXKoTH;
import ar.com.octaviofarias.koth.KoTHManager;
import ar.com.octaviofarias.koth.model.KoTH;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static ar.com.octaviofarias.koth.utils.KoTHUtils.sendMessage;

public class SetSecondPositionKoTHCommand implements KoTHSubCommand{
    @Override
    public String getName() {
        return "setsecondposition";
    }

    @Override
    public boolean canConsoleUse() {
        return false;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length == 0){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.setsecondposition.usage"));
            return;
        }
        String name = args[0];

        KoTH koth = KoTHManager.getKoTH(name);
        if(koth == null){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.unknown-koth"));
            return;
        }

        if(KoTHManager.isKoTHStarted(koth)){
            sendMessage(sender,DamiXKoTH.getMessages().getMessage("commands.setsecondposition.started").replace("%name%", name));
            return;
        }
        Block targetBlock = ((Player) sender).getTargetBlock(null, 5);
        if(!targetBlock.isCollidable()){
            sendMessage(sender,DamiXKoTH.getMessages().getMessage("commands.setsecondposition.not-solid-block"));
            return;
        }

        Location l = targetBlock.getLocation();
        koth.setSecondPosition(l);
        KoTHManager.update(koth);
        sendMessage(sender,DamiXKoTH.getMessages().getMessage("commands.setsecondposition.successfully")
                .replace("%name%", koth.getName())
                .replace("%x%", String.valueOf(l.getBlockX()))
                .replace("%y%", String.valueOf(l.getBlockY()))
                .replace("%z%", String.valueOf(l.getBlockZ()))
                .replace("%world%", l.getWorld().getName()));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return args.length == 1 ? KoTHManager.getKoTHs().stream().map(KoTH::getName).toList() : List.of();
    }
}
