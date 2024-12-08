package ar.com.octaviofarias.koth.commands;

import ar.com.octaviofarias.koth.DamiXKoTH;
import ar.com.octaviofarias.koth.KoTHManager;
import ar.com.octaviofarias.koth.model.KoTH;
import org.bukkit.command.CommandSender;

import java.util.List;

import static ar.com.octaviofarias.koth.utils.KoTHUtils.sendMessage;

public class ListKoTHCommand implements KoTHSubCommand{
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public boolean canConsoleUse() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(KoTHManager.getKoTHs().isEmpty()){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.list.empty-koths"));
            return;
        }
        sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.list.header"));
        for (KoTH koTH : KoTHManager.getKoTHs()) {
            String finalString = DamiXKoTH.getMessages().getMessage("commands.list.each")
                    .replace("%name%", koTH.getName())
                    .replace("%started%", (KoTHManager.isKoTHStarted(koTH) ?
                            DamiXKoTH.getSettings().getSetting("yes-placeholder") :
                            DamiXKoTH.getSettings().getSetting("no-placeholder")));
            sendMessage(sender, finalString);
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
