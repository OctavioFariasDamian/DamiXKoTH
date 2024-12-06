package ar.com.octaviofarias.koth.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface KoTHSubCommand {
    String getName();
    boolean canConsoleUse();
    void onCommand(CommandSender sender, String[] args);
    List<String> onTabComplete(CommandSender sender, String[] args);
}
