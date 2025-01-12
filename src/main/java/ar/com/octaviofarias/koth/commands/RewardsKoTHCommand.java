package ar.com.octaviofarias.koth.commands;

import ar.com.octaviofarias.koth.DamiXKoTH;
import ar.com.octaviofarias.koth.KoTHManager;
import ar.com.octaviofarias.koth.model.KoTH;
import ar.com.octaviofarias.koth.utils.menu.Menu;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static ar.com.octaviofarias.koth.utils.KoTHUtils.sendMessage;
import static ar.com.octaviofarias.koth.utils.KoTHUtils.sendMessageWithoutPlaceholders;

public class RewardsKoTHCommand implements KoTHSubCommand{

    @Getter
    private static final Map<Player, KoTH> editingItemsReward = new HashMap<>();

    @Override
    public String getName() {
        return "rewards";
    }

    @Override
    public boolean canConsoleUse() {
        return false;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length < 2){
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.rewards.usage"));
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
            sendMessage(sender,DamiXKoTH.getMessages().getMessage("commands.rewards.started").replace("%name%", name));
            return;
        }

        if(args[1].equalsIgnoreCase("items")){
            if(sender instanceof ConsoleCommandSender){
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.only-players"));
                return;
            }
            new RewardsMenu(koth).open((Player) sender);
            getEditingItemsReward().put((Player) sender, koth);
        }else if(args[1].equalsIgnoreCase("commands")){
            if(args.length == 2){
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.rewards.usage1"));
                return;
            }
            if(args[2].equalsIgnoreCase("list")){

                if(koth.getRewards().getCommands().isEmpty()){
                    sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.rewards.commands.empty"));
                    return;
                }
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.rewards.commands.list.header").replace("%name%", koth.getName()));
                int i = 0;
                for (String command : koth.getRewards().getCommands()) {
                    sendMessageWithoutPlaceholders(sender, DamiXKoTH.getMessages().getMessage("commands.rewards.commands.list.each")
                            .replace("%command%", command).replace("%id%", String.valueOf(i)));
                    i++;
                }

            }
            else if(args[2].equalsIgnoreCase("add")){
                if(args.length < 4){
                    sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.rewards.commands.add.usage"));
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }
                String finalS = sb.toString().trim();
                koth.getRewards().getCommands().add(finalS);
                KoTHManager.update(koth);

                sendMessageWithoutPlaceholders(sender, DamiXKoTH.getMessages().getMessage("commands.rewards.commands.add.successfully")
                        .replace("%command%", finalS).replace("%id%", String.valueOf(koth.getRewards().getCommands().size()-1)));
            }else if(args[2].equalsIgnoreCase("remove")){
                if(args.length != 4){
                    sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.rewards.commands.remove.usage"));
                    return;
                }

                int index;

                try{
                    index = Integer.parseInt(args[3]);
                }catch (NumberFormatException e){
                    sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.invalid-number"));
                    return;
                }

                if(index > koth.getRewards().getCommands().size()-1){
                    sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.rewards.command.remove.out-of-index"));
                    return;
                }
                String cmd = koth.getRewards().getCommands().get(index);
                koth.getRewards().getCommands().remove(index);
                sendMessageWithoutPlaceholders(sender, DamiXKoTH.getMessages().getMessage("commands.rewards.commands.remove.successfully")
                        .replace("%command%", cmd).replace("%id%", String.valueOf(index)));

            }else{
                sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.rewards.usage1"));
            }
        }else{
            sendMessage(sender, DamiXKoTH.getMessages().getMessage("commands.rewards.usage"));
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {

        if(args.length == 1 ) return KoTHManager.getKoTHs().stream().map(KoTH::getName).toList();
        else if(args.length == 2) return List.of("items", "commands");
        else if(args.length == 3 && args[1].equalsIgnoreCase("commands")) return List.of("add", "remove", "list");
        else if(args.length == 4 && args[1].equalsIgnoreCase("commands") && args[2].equalsIgnoreCase("remove")){
            Optional<KoTH> koTH = KoTHManager.getKoTH(args[1]);
            if(koTH.isPresent()){
                List<String> l = new ArrayList<>();
                for(int i = 0; i < koTH.get().getRewards().getCommands().size() ; i++) l.add(String.valueOf(i));
                return l;
            }
        }
        return List.of();
    }

    public static class RewardsMenu extends Menu {

        protected RewardsMenu(KoTH koth) {
            super(DamiXKoTH.getSettings().getSetting("menus.rewards.title").replace("%name%", koth.getName()), Integer.parseInt(DamiXKoTH.getSettings().getSetting("menus.rewards.rows")));

            int i = 0;
            for (ItemStack item : koth.getRewards().getItems()) {
                if(i > 54) break;
                getInventory().setItem(i, item);
                i++;
            }
            write();
        }


    }
}
