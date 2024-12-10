package ar.com.octaviofarias.koth.commands;

import ar.com.octaviofarias.koth.DamiXKoTH;
import ar.com.octaviofarias.koth.KoTHManager;
import ar.com.octaviofarias.koth.model.KoTH;
import ar.com.octaviofarias.koth.model.KoTHScheduler;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;

import java.time.DayOfWeek;
import java.util.List;

import static ar.com.octaviofarias.koth.utils.KoTHUtils.sendCenteredMessage;
import static ar.com.octaviofarias.koth.utils.KoTHUtils.sendMessage;

public class SchedulersKoTHCommand implements KoTHSubCommand{
    @Override
    public String getName() {
        return "schedulers";
    }

    @Override
    public boolean canConsoleUse() {
        return true;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length == 0){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.schedulers.usage"));
            return;
        }

        if(args[0].equalsIgnoreCase("list")){
            if(args.length < 2){
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.schedulers.list.usage"));
                return;
            }
            KoTH koTH = KoTHManager.getKoTH(args[1]);
            if(koTH == null){
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.unknown-koth"));
                return;
            }
            if(koTH.getSchedulers().isEmpty()){
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.schedulers.list.empty"));
                return;
            }
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.schedulers.list.header"));
            int i = 0;
            for (KoTHScheduler scheduler : koTH.getSchedulers()) {
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.schedulers.list.each")
                        .replace("%id%", String.valueOf(i))
                        .replace("%day%", StringUtils.capitalize(scheduler.getDay().toString().toLowerCase()))
                        .replace("%hour%", String.valueOf(scheduler.getHour()))
                        .replace("%minute%", String.valueOf(scheduler.getMinutes()))
                        .replace("%second%", String.valueOf(scheduler.getSeconds()))
                        .replace("%duration%", String.valueOf(scheduler.getDuration())));
                i++;
            }
            return;
        }else if(args[0].equalsIgnoreCase("add")){
            if(args.length < 5){
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.schedulers.add.usage"));
                return;
            }
            KoTH koTH = KoTHManager.getKoTH(args[1]);
            if(koTH == null){
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.unknown-koth"));
                return;
            }

            DayOfWeek day;
            int hour, minute, second;

            try {
                day = DayOfWeek.valueOf(args[2]);
            }catch (IllegalArgumentException e){
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.schedulers.add.invalid-day-of-week"));
                return;
            }

            String[] time = args[3].split(":");

            if(time.length != 3){
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.schedulers.add.invalid-time-format"));
                return;
            }

            try {
                hour = Integer.parseInt(time[0]);
                minute = Integer.parseInt(time[1]);
                second = Integer.parseInt(time[2]);
            }catch (NumberFormatException e){
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.schedulers.add.invalid-time-format"));
                return;
            }

            int duration;

            try{
                duration = Integer.parseInt(args[4]);
            }catch (NumberFormatException e){
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.invalid-number"));
                return;
            }

            koTH.getSchedulers().add(new KoTHScheduler(day, hour, minute, second, duration));
            KoTHManager.update(koTH);

            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.schedulers.add.successfully")
                    .replace("%name%", koTH.getName())
                    .replace("%id%", String.valueOf(koTH.getSchedulers().size()-1))
                    .replace("%day%", StringUtils.capitalize(day.name().toLowerCase()))
                    .replace("%hour%", String.valueOf(hour))
                    .replace("%minute%", String.valueOf(minute))
                    .replace("%second%", String.valueOf(second))
                    .replace("%duration%", String.valueOf(duration)));

            return;
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) return List.of("add", "list", "remove", "next");
        return args.length == 2 ? KoTHManager.getKoTHs().stream().map(KoTH::getName).toList() : List.of();
    }
}
