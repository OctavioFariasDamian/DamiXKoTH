package ar.com.octaviofarias.koth.commands;

import ar.com.octaviofarias.koth.DamiXKoTH;
import ar.com.octaviofarias.koth.KoTHManager;
import ar.com.octaviofarias.koth.model.KoTH;
import org.bukkit.command.CommandSender;

import java.util.List;

import static ar.com.octaviofarias.koth.utils.KoTHUtils.sendMessage;

public class SetCaptureTimeKoTHCommand implements KoTHSubCommand{

    @Override
    public String getName() {
        return "setcapturetime";
    }

    @Override
    public boolean canConsoleUse() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length < 2){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.setcapturetime.usage"));
            return;
        }
        String name = args[0];

        KoTH koth = KoTHManager.getKoTH(name);

        if(koth == null){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.unknown-koth"));
            return;
        }

        int captureTime;

        try{
            captureTime = Integer.parseInt(args[1]);
        }catch (NumberFormatException e){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.invalid-number"));
            return;
        }

        koth.setCaptureTime(captureTime);
        KoTHManager.update(koth);
        sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.setcapturetime.successfully").replace("%name%", koth.getName()).replace("%time%", String.valueOf(captureTime)));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return args.length == 1 ? KoTHManager.getKoTHs().stream().map(KoTH::getName).toList() : List.of();
    }
}
