package ar.com.octaviofarias.koth.commands;

import ar.com.octaviofarias.koth.DamiXKoTH;
import ar.com.octaviofarias.koth.KoTHManager;
import ar.com.octaviofarias.koth.model.KoTH;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

import static ar.com.octaviofarias.koth.utils.KoTHUtils.sendMessage;

public class StopKoTHCommand implements KoTHSubCommand{
    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public boolean canConsoleUse() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length == 0){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.stop.usage"));
            return;
        }
        String name = args[0];

        Optional<KoTH> optkoth = KoTHManager.getKoTH(name);
        if(optkoth.isEmpty()){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.unknown-koth"));
            return;
        }

        KoTH koth = optkoth.get();

        if(!KoTHManager.isKoTHStarted(koth)){
            sendMessage(sender,DamiXKoTH.getMessages().getMessage("commands.stop.not-started").replace("%name%", name));
            return;
        }

        if(KoTHManager.getActiveKoTH(name).isEmpty()){
            sendMessage(sender,DamiXKoTH.getMessages().getMessage("commands.stop.not-started").replace("%name%", name));
            return;
        }

        KoTHManager.getActiveKoTH(name).get().stop();
        sendMessage(sender,DamiXKoTH.getMessages().getMessage("commands.stop.successfully").replace("%name%", name));

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            for (String s : DamiXKoTH.getMessages().getMessageList("koth.stopped")) {
                sendMessage(onlinePlayer, s.replace("%koth%", koth.getName()));
            }
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return args.length == 1 ? KoTHManager.getKoTHs().stream().map(KoTH::getName).toList() : List.of();
    }
}
