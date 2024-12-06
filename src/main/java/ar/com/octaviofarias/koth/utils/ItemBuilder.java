package ar.com.octaviofarias.koth.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

@SuppressWarnings({"UnusedReturnValue", "deprecation", "unused"})
public class ItemBuilder {

    private ItemStack stack;

    public ItemBuilder(Material mat) {
        stack = new ItemStack(mat);
    }

    public ItemBuilder(Material mat, short sh) {
        stack = new ItemStack(mat, 1, sh);
    }

    public ItemMeta getItemMeta() {
        return stack.getItemMeta();
    }

    public ItemBuilder setColor(Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
        meta.setColor(color);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setGlow (boolean glow) {
        if (glow) {
            addEnchant(Enchantment.KNOCKBACK, 1);
            addItemFlag(ItemFlag.HIDE_ENCHANTS);
        } else {
            ItemMeta meta = getItemMeta();
            for (Enchantment enchantment : meta.getEnchants().keySet()) {
                meta.removeEnchant(enchantment);
            }
        }
        return this;
    }

    public ItemBuilder setUnbreakable (boolean unbreakable) {
        ItemMeta meta = stack.getItemMeta();
        meta.setUnbreakable(unbreakable);
        stack.setItemMeta(meta);
        return this;
    }


    public ItemBuilder setAmount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public ItemBuilder setItemMeta(ItemMeta meta) {
        stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setSkullValue(String base64) {
        SkullMeta sm = (SkullMeta) stack.getItemMeta();
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        URL url;
        try {
            String decoded = new String(Base64.getDecoder().decode(base64));
            String decodedFormatted = decoded.replaceAll("\\s", "");
            JsonObject jsonObject = new Gson().fromJson(decodedFormatted, JsonObject.class);
            String urlText = jsonObject.get("textures").getAsJsonObject().get("SKIN")
                    .getAsJsonObject().get("url").getAsString();

            url = new URL(urlText);
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
        textures.setSkin(url);
        profile.setTextures(textures);
        sm.setOwnerProfile(profile);

        setItemMeta(sm);
        return this;
    }


    public ItemBuilder setMaterial(Material material){
        stack.setType(material);
        return this;
    }

    public ItemBuilder setHead(String owner) {
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner(owner);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setDisplayName(String displayname) {
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(KoTHUtils.color(displayname));
        setItemMeta(meta);
        return this;
    }

    private static PlayerProfile getProfile(String url) {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID()); // Get a new player profile
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = new URL(url); // The URL to the skin, for example: https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid URL", exception);
        }
        textures.setSkin(urlObject); // Set the skin of the player profile to the URL
        profile.setTextures(textures); // Set the textures back to the profile
        return profile;
    }

    public ItemBuilder setItemStack (ItemStack stack) {
        this.stack = stack;
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = getItemMeta();
        meta.setLore(KoTHUtils.colorateList(lore));
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeLore(int index) {
        ItemMeta meta = getItemMeta();
        ArrayList<String> lorel;
        if(meta.getLore() == null) lorel = new ArrayList<>();
        else
            lorel = new ArrayList<>(meta.getLore());
        lorel.remove(index);
        meta.setLore(lorel);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addLore (String lore) {
        ItemMeta meta = getItemMeta();
        ArrayList<String> lorel;
        if(meta.getLore() == null) lorel = new ArrayList<>();
        else
            lorel = new ArrayList<>(meta.getLore());
        lorel.add(KoTHUtils.color(lore));
        meta.setLore(lorel);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        ItemMeta meta = getItemMeta();
        meta.addEnchant(enchantment, level, true);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag flag) {
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(flag);
        setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return stack;
    }

}
