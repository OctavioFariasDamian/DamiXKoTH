package ar.com.octaviofarias.koth.commands;

import ar.com.octaviofarias.koth.DamiXKoTH;
import ar.com.octaviofarias.koth.KoTHManager;
import ar.com.octaviofarias.koth.model.KoTH;
import ar.com.octaviofarias.koth.model.KoTHScheduler;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
                        .replace("%hour%", String.valueOf(scheduler.getFormatedHour()))
                        .replace("%minute%", String.valueOf(scheduler.getFormatedMinutes()))
                        .replace("%second%", String.valueOf(scheduler.getFormatedSeconds()))
                        .replace("%duration%", String.valueOf(scheduler.getDuration())));
                i++;
            }
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
            KoTHScheduler scheduler = new KoTHScheduler(day, hour, minute, second, duration);
            koTH.getSchedulers().add(scheduler);
            KoTHManager.update(koTH);

            KoTHManager.checkSchedulers();

            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.schedulers.add.successfully")
                    .replace("%name%", koTH.getName())
                    .replace("%id%", String.valueOf(koTH.getSchedulers().size()-1))
                    .replace("%day%", StringUtils.capitalize(day.name().toLowerCase()))
                    .replace("%hour%", String.valueOf(scheduler.getFormatedHour()))
                    .replace("%minute%", String.valueOf(scheduler.getFormatedMinutes()))
                    .replace("%second%", String.valueOf(scheduler.getFormatedSeconds()))
                    .replace("%duration%", String.valueOf(duration)));

        }else if(args[0].equalsIgnoreCase("remove")) {
            if(args.length < 3){
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.schedulers.remove.usage"));
                return;
            }

            KoTH koth = KoTHManager.getKoTH(args[1]);

            if(koth == null){
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.unknown-koth"));
                return;
            }

            int index;

            try{
                index = Integer.parseInt(args[2]);
            }catch(NumberFormatException e){
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.invalid-number"));
                return;
            }

            if(index < 0 || index >= koth.getSchedulers().size()){
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.schedulers.remove.out-of-index"));
                return;
            }

            koth.getSchedulers().remove(index);

            KoTHManager.update(koth);

            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.schedulers.remove.successfuly")
                    .replace("%id%", String.valueOf(index))
                    .replace("%name%", koth.getName()));

            KoTHManager.checkSchedulers();

        }else if(args[0].equalsIgnoreCase("next")) {
            KoTH selected = null;
            long min = Long.MAX_VALUE;
            ZonedDateTime dt = null;
            for (KoTH koTH : KoTHManager.getKoTHs()) {
                if (KoTHManager.isKoTHStarted(koTH)) continue;
                for (KoTHScheduler scheduler : koTH.getSchedulers()) {
                    ZonedDateTime now = ZonedDateTime.now(KoTHManager.getZone());
                    ZonedDateTime nextEvent = now.with(scheduler.getDay())
                            .withHour(scheduler.getHour())
                            .withMinute(scheduler.getMinutes())
                            .withSecond(scheduler.getSeconds())
                            .withNano(0);

                    if (now.isAfter(nextEvent)) {
                        nextEvent = nextEvent.plusWeeks(1);
                    }

                    long delay = TimeUnit.SECONDS.convert(nextEvent.toEpochSecond() - now.toEpochSecond(), TimeUnit.SECONDS);
                    if(delay < min){
                        dt = nextEvent;
                        selected = koTH;
                        min = delay;
                    }
                }
            }

            if(selected == null){
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.schedulers.next.empty"));
                return;
            }

            ZonedDateTime now = ZonedDateTime.now(KoTHManager.getZone());

            Duration duration = Duration.between(now, dt);

            long totalSeconds = duration.getSeconds();
            long hours = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            long seconds = totalSeconds % 60;

            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.schedulers.next.successfully")
                    .replace("%name%", selected.getName())
                    .replace("%time%", hours+"h " + minutes+"m " + seconds +"s"

                            ));

        } else{
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.schedulers.usage"));
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) return List.of("add", "list", "remove", "next");
        return args.length == 2 ? KoTHManager.getKoTHs().stream().map(KoTH::getName).toList() : List.of();
    }
}
