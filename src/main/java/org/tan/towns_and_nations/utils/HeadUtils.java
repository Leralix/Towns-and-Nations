package org.tan.towns_and_nations.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.tan.towns_and_nations.DataClass.TownDataClass;

import java.lang.reflect.Field;
import java.util.UUID;

public class HeadUtils {


    public static ItemStack getPlayerHead(String headName, Player p){
        ItemStack PlayerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) PlayerHead.getItemMeta();
        skullMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + headName);
        skullMeta.setOwningPlayer(p);
        PlayerHead.setItemMeta(skullMeta);
        return PlayerHead;
    }

    public static ItemStack getPlayerHead(String headName, OfflinePlayer p){
        ItemStack PlayerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) PlayerHead.getItemMeta();
        skullMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + headName);
        skullMeta.setOwningPlayer(p);
        PlayerHead.setItemMeta(skullMeta);
        return PlayerHead;
    }

    public static ItemStack makeSkull(String name, String base64EncodedString) {
        final ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        assert meta != null;
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", base64EncodedString));
        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + name);
        skull.setItemMeta(meta);
        return skull;
    }

    public static ItemStack getTownIcon(String TownId){

        if(TownId == null){
            System.out.println("Erreur critique: Fonction accesible seulement a un joueur qui a une ville apellée par un joueur qui n'en possède pas");
            return null;
        }

        TownDataClass town = TownDataStorage.getTown(TownId);
        ItemStack itemStack = town.getTownIconItemStack();
        if (itemStack == null){
            return HeadUtils.getPlayerHead(town.getTownName(), Bukkit.getOfflinePlayer(UUID.fromString(town.getUuidLeader())));
        }
        else {
            return itemStack;
        }

    }


}
