package ar.com.octaviofarias.koth.commands;

import ar.com.octaviofarias.koth.DamiXKoTH;
import ar.com.octaviofarias.koth.utils.KoTHUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static ar.com.octaviofarias.koth.utils.KoTHUtils.sendMessage;

public class KoTHCommand implements TabExecutor {

    private final List<KoTHSubCommand> subCommands = new ArrayList<>();


    public KoTHCommand(){
        subCommands.add(new CreateKoTHCommand());
        subCommands.add(new RemoveKoTHCommand());
        subCommands.add(new SetCaptureTimeKoTHCommand());
        subCommands.add(new SetFirstPositionKoTHCommand());
        subCommands.add(new SetSecondPositionKoTHCommand());
        subCommands.add(new RewardsKoTHCommand());
        subCommands.add(new StartKoTHCommand());
        subCommands.add(new StopKoTHCommand());
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(args.length == 0){
            if(commandSender.hasPermission("damixkoth")){
                sendMessage(commandSender, DamiXKoTH.getMessages().getMessageList("commands.help"));
                return false;
            }

            sendMessage(commandSender, DamiXKoTH.getMessages().getMessage("no-permission"));
            return false;

        }
        for (KoTHSubCommand subCommand : subCommands) {
            if(subCommand.getName().equalsIgnoreCase(args[0])){
                if(!subCommand.canConsoleUse() && commandSender instanceof ConsoleCommandSender){
                    sendMessage(commandSender, DamiXKoTH.getMessages().getMessage("commands.only-players"));
                    return false;
                }

                if(!commandSender.hasPermission("damixkoth."+subCommand.getName())){
                    sendMessage(commandSender, DamiXKoTH.getMessages().getMessage("no-permission"));
                    return false;
                }

                ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(args));
                arrayList.removeFirst();
                String[] str = new String[arrayList.size()];
                for(int x=0; x<arrayList.size();x++){
                    str[x] = arrayList.get(x);
                }

                subCommand.onCommand(commandSender, str);
                return false;
            }
        }
        sendMessage(commandSender, DamiXKoTH.getMessages().getMessage("commands.unknown-subcommand"));
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 0) return List.of();
        List<String> ret = new ArrayList<>();
        String input = args[args.length-1];
        if(args.length == 1){
            for(KoTHSubCommand subCommand : subCommands) {
                ret.add(subCommand.getName());
            }
        } else {
            for (KoTHSubCommand subCommand : subCommands) {
                if(subCommand.getName().equalsIgnoreCase(args[0])){
                    ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(args));
                    arrayList.removeFirst();
                    String[] str = new String[arrayList.size()];
                    for(int x=0; x<arrayList.size();x++){
                        str[x] = arrayList.get(x);
                    }
                    ret = subCommand.onTabComplete(commandSender, str);
                }
            }
        }
        return KoTHUtils.filterSuggestions(ret, input);
    }
}
