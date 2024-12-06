package ar.com.octaviofarias.koth.commands;

import ar.com.octaviofarias.koth.DamiXKoTH;
import ar.com.octaviofarias.koth.KoTHManager;
import ar.com.octaviofarias.koth.model.ActiveKoTH;
import ar.com.octaviofarias.koth.model.KoTH;
import org.bukkit.command.CommandSender;

import java.util.List;

import static ar.com.octaviofarias.koth.utils.KoTHUtils.sendMessage;

public class StartKoTHCommand implements KoTHSubCommand{
    @Override
    public String getName() {
        return "start";
    }

    @Override
    public boolean canConsoleUse() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length != 2){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.start.usage"));
            return;
        }
        String name = args[0];

        KoTH koth = KoTHManager.getKoTH(name);
        if(koth == null){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.unknown-koth"));
            return;
        }

        if(KoTHManager.isKoTHStarted(koth)){
            sendMessage(sender,DamiXKoTH.getMessages().getMessage("commands.start.started").replace("%name%", name));
            return;
        }

        if(koth.getFirstPosition() == null){
            sendMessage(sender,DamiXKoTH.getMessages().getMessage("commands.start.error1").replace("%name%", name));
            return;
        }

        if(koth.getSecondPosition() == null){
            sendMessage(sender,DamiXKoTH.getMessages().getMessage("commands.start.error2").replace("%name%", name));
            return;
        }

        if(koth.getRewards().getItems().isEmpty() && koth.getRewards().getCommands().isEmpty()){
            sendMessage(sender,DamiXKoTH.getMessages().getMessage("commands.start.error3").replace("%name%", name));
            return;
        }

        if(koth.getCaptureTime() == 0){
            sendMessage(sender,DamiXKoTH.getMessages().getMessage("commands.start.error4").replace("%name%", name));
            return;
        }

        int activeTime;

        try{
            activeTime = Integer.parseInt(args[1]);
        }catch (NumberFormatException e){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.invalid-number"));
            return;
        }

        ActiveKoTH activeKoTH = new ActiveKoTH(activeTime, koth);
        activeKoTH.scheduler();
        KoTHManager.getActiveKoTHs().add(activeKoTH);
        sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.start.successfully").replace("%name%", koth.getName()).replace("%time%", String.valueOf(activeTime)));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return args.length == 1 ? KoTHManager.getKoTHs().stream().map(KoTH::getName).toList() : List.of();
    }
}
