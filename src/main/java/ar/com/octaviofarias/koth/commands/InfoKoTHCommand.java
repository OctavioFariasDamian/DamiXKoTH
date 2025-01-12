package ar.com.octaviofarias.koth.commands;

import ar.com.octaviofarias.koth.DamiXKoTH;
import ar.com.octaviofarias.koth.KoTHManager;
import ar.com.octaviofarias.koth.model.ActiveKoTH;
import ar.com.octaviofarias.koth.model.KoTH;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;

import static ar.com.octaviofarias.koth.utils.KoTHUtils.sendMessage;

public class InfoKoTHCommand implements KoTHSubCommand {
    
    @Override
    public String getName() {
        return "info";
    }

    @Override
    public boolean canConsoleUse() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length == 0){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.info.usage"));
            return;
        }
        String name = args[0];

        Optional<KoTH> optionalKoTH = KoTHManager.getKoTH(name);
        if(optionalKoTH.isEmpty()){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.unknown-koth"));
            return;
        }
        
        KoTH koth = optionalKoTH.get();
        
        for (String s : DamiXKoTH.getMessages().getMessageList("commands.info.header")) {
            sendMessage(sender, s
                    .replace("%name%", name)
                    .replace("%captureTime%", String.valueOf(koth.getCaptureTime()))
                    .replace("%firstPoint%", (koth.getFirstPosition() != null ? DamiXKoTH.getSettings().translateLocation(koth.getFirstPosition()) : DamiXKoTH.getSettings().getSetting("unavailable-placeholder")))
                    .replace("%secondPoint%", (koth.getSecondPosition() != null ? DamiXKoTH.getSettings().translateLocation(koth.getSecondPosition()) : DamiXKoTH.getSettings().getSetting("unavailable-placeholder"))));
        }

        if(!koth.getRewards().getCommands().isEmpty() || !koth.getRewards().getItems().isEmpty())
            for (String s : DamiXKoTH.getMessages().getMessageList("commands.info.rewards")) {
                sendMessage(sender, s.replace("%commandsCount%", String.valueOf(koth.getRewards().getCommands().size()))
                        .replace("%itemsCount%", String.valueOf(koth.getRewards().getItems().size())));
            }

        if(KoTHManager.isKoTHStarted(koth)) {
            Optional<ActiveKoTH> akoth = KoTHManager.getActiveKoTH(koth.getName());
            if(akoth.isPresent())
                for (String s : DamiXKoTH.getMessages().getMessageList("commands.info.started")) {
                sendMessage(sender, s
                        .replace("%countdown%", String.valueOf(akoth.get().getReamingTime()))
                        .replace("%dominatingTime%", String.valueOf(akoth.get().getDominatingTime()))
                        .replace("%dominating%", akoth.get().getDominating() == null ? DamiXKoTH.getSettings().getSetting("nobody-placeholder") : akoth.get().getDominating().getName()));
                }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return args.length == 1 ? KoTHManager.getKoTHs().stream().map(KoTH::getName).toList() : List.of();
    }
}

