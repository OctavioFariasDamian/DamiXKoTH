package ar.com.octaviofarias.koth.commands;

import ar.com.octaviofarias.koth.DamiXKoTH;
import ar.com.octaviofarias.koth.KoTHManager;
import ar.com.octaviofarias.koth.model.KoTH;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static ar.com.octaviofarias.koth.utils.KoTHUtils.sendMessage;

public class RemoveKoTHCommand implements KoTHSubCommand{

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public boolean canConsoleUse() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length == 0){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.remove.usage"));
            return;
        }
        String name = args[0];

        Optional<KoTH> optionalKoth = KoTHManager.getKoTH(name);
        if(optionalKoth.isEmpty()){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.unknown-koth"));
            return;
        }

        KoTH koth = optionalKoth.get();

        if(KoTHManager.isKoTHStarted(koth)){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.remove.started").replace("%name%", name));
            return;
        }
        try {
            KoTHManager.delete(koth);
        } catch (IOException e) {
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.remove.error"));
            throw new RuntimeException(e);
        }
        sendMessage(sender,DamiXKoTH.getMessages().getMessage("commands.remove.successfully").replace("%name%", name));


    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return args.length == 1 ? KoTHManager.getKoTHs().stream().map(KoTH::getName).toList() : List.of();
    }
}
