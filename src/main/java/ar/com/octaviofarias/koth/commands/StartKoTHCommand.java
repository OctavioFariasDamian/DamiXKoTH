package ar.com.octaviofarias.koth.commands;

import ar.com.octaviofarias.koth.DamiXKoTH;
import ar.com.octaviofarias.koth.KoTHManager;
import ar.com.octaviofarias.koth.model.ActiveKoTH;
import ar.com.octaviofarias.koth.model.KoTH;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;

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

        Optional<KoTH> optkoth = KoTHManager.getKoTH(name);
        if(optkoth.isEmpty()){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.unknown-koth"));
            return;
        }

        KoTH koth = optkoth.get();

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
            activeTime = -1;
            if(!args[1].equalsIgnoreCase("infinite")) {
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.invalid-activetime"));
                return;
            }
        }

        ActiveKoTH activeKoTH = new ActiveKoTH(activeTime, koth, activeTime == -1);
        activeKoTH.scheduler();
        KoTHManager.getActiveKoTHs().add(activeKoTH);
        if(activeTime != -1) sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.start.successfully")
                .replace("%name%", koth.getName())
                .replace("%time%", String.valueOf(activeTime)));
        else
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.start.successfully2")
                    .replace("%name%", koth.getName()));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return args.length == 1 ? KoTHManager.getKoTHs().stream().map(KoTH::getName).toList() : (args.length == 2 ? List.of("<seconds>", "infinite") : List.of());
    }
}
