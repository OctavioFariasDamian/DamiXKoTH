package ar.com.octaviofarias.koth.utils;

import ar.com.octaviofarias.koth.DamiXKoTH;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings({"deprecation", "unused"})
public class KoTHUtils {

    private final static int CENTER_PX = 154;

    public static boolean isLocationInCuboid(Location loc, Location loc1, Location loc2) {

        if (!loc.getWorld().equals(loc1.getWorld()) || !loc1.getWorld().equals(loc2.getWorld())) {
            return false;
        }

        double minX = Math.min(loc1.getX(), loc2.getX());
        double maxX = Math.max(loc1.getX(), loc2.getX());

        double minY = Math.min(loc1.getY(), loc2.getY());
        double maxY = Math.max(loc1.getY(), loc2.getY());

        double minZ = Math.min(loc1.getZ(), loc2.getZ());
        double maxZ = Math.max(loc1.getZ(), loc2.getZ());

        return loc.getX() >= minX && loc.getX() <= maxX &&
                loc.getY() >= minY && loc.getY() <= maxY &&
                loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }
    
    public static List<String> filterSuggestions(List<String> suggestions, String currentInput) {
        if (currentInput.isEmpty()) {
            return suggestions;
        }

        return suggestions.stream()
                .filter(s -> s.toLowerCase().startsWith(currentInput.toLowerCase()))
                .collect(Collectors.toList());
    }


    public static ChatColor getChatColorFromCode(char code) {
        return switch (code) {
            case '0' -> ChatColor.BLACK;
            case '1' -> ChatColor.DARK_BLUE;
            case '2' -> ChatColor.DARK_GREEN;
            case '3' -> ChatColor.DARK_AQUA;
            case '4' -> ChatColor.DARK_RED;
            case '5' -> ChatColor.DARK_PURPLE;
            case '6' -> ChatColor.GOLD;
            case '7' -> ChatColor.GRAY;
            case '8' -> ChatColor.DARK_GRAY;
            case '9' -> ChatColor.BLUE;
            case 'a' -> ChatColor.GREEN;
            case 'b' -> ChatColor.AQUA;
            case 'c' -> ChatColor.RED;
            case 'd' -> ChatColor.LIGHT_PURPLE;
            case 'e' -> ChatColor.YELLOW;
            case 'f' -> ChatColor.WHITE;
            case 'k' -> ChatColor.MAGIC;
            case 'l' -> ChatColor.BOLD;
            case 'm' -> ChatColor.STRIKETHROUGH;
            case 'n' -> ChatColor.UNDERLINE;
            case 'o' -> ChatColor.ITALIC;
            case 'r' -> ChatColor.RESET;
            default -> null; // Código inválido
        };
    }

    public static String processColors(List<ChatColor> chatColors) {
        ChatColor lastColor = null;
        Set<ChatColor> decorations = new HashSet<>();
        boolean hasReset = false;

        for (ChatColor color : chatColors) {
            if (color == ChatColor.RESET) {
                hasReset = true;
                decorations.clear();
                lastColor = null;
            }

            else if (isColor(color)) {
                lastColor = color;
            }

            else if (isDecoration(color)) {
                decorations.add(color);
            }
        }

        if (hasReset) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        if (lastColor != null) {
            result.append(lastColor);
        }

        for (ChatColor decoration : decorations) {
            result.append(decoration.toString());
        }

        return result.toString();
    }

    private static boolean isColor(ChatColor color) {
        return color.isColor();
    }

    private static boolean isDecoration(ChatColor color) {
        return color.isFormat();
    }

    public static void sendMessage(CommandSender sender, String s) {
        if(sender instanceof Player && Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) s = PlaceholderAPI.setPlaceholders((Player) sender, s);
        if(s.startsWith("<center> ")) s = getCenteredMessage(s.replace("<center> ", ""));
        else if(s.startsWith("<underline:")){
            if(s.length() == 12){
                sendUnderline(sender, s.charAt(11));
            }else{
                sendUnderline(sender, s.replace("<underline:", ""));
            }
            return;
        }
        sender.sendMessage(color(s.replace("%prefix%", DamiXKoTH.getMessages().getMessage("prefix"))));
    }

    public static void sendMessageWithoutPlaceholders(CommandSender sender, String s) {
        if(s.startsWith("<center> ")) s = getCenteredMessage(s.replace("<center> ", ""));
        else if(s.startsWith("<underline:")){
            if(s.length() == 12){
                sendUnderline(sender, s.charAt(11));
            }else{
                sendUnderline(sender, s.replace("<underline:", ""));
            }
            return;
        }
        sender.sendMessage(color(s.replace("%prefix%", DamiXKoTH.getMessages().getMessage("prefix"))));
    }

    public static String formatTime(int seconds, String format) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        Date time = new Date(0, 0, 0, hours, minutes, remainingSeconds);

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(time);
    }

    public static void sendMessage(CommandSender sender, List<String> list) {
        for (String s : list) {
            sendMessage(sender, s);
        }
    }

    public static int getFreeSlots(Player player) {
        Inventory inventory = player.getInventory();
        int freeSlots = 0;

        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.getType() == org.bukkit.Material.AIR) {
                freeSlots++;
            }
        }

        return freeSlots;
    }

    public static List<String> colorateList(List<String> lines) {
        lines.replaceAll(KoTHUtils::color);
        return lines;
    }

    public static String color(String message) {
        Pattern hexPattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = hexPattern.matcher(message);

        while (matcher.find()) {
            String hexColor = matcher.group();
            StringBuilder minecraftColor = new StringBuilder("§x");
            for (char c : hexColor.substring(1).toCharArray()) {
                minecraftColor.append("§").append(c);
            }
            message = message.replace(hexColor, minecraftColor.toString());
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String getCenteredMessage(String message){
        message = color(message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : message.toCharArray()){
            if(c == '§'){
                previousCode = true;
            }else if(previousCode){
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            }else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb.toString()+message;
    }

    public static void sendCenteredMessage(CommandSender s, String message){
        s.sendMessage(getCenteredMessage(message));
    }

    public static void sendUnderline(CommandSender sender, String hex) {
        sender.sendMessage(color(hex+getUnderline()));
    }

    public static String getUnderline(){
        return "&m                                                                          ";
    }

    public static void sendUnderline(CommandSender sender, char color) {
        sender.sendMessage(color("&"+color+getUnderline()));
    }

}
