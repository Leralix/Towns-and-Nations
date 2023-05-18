package org.tan.towns_and_nations.commands.subcommands;


import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.tan.towns_and_nations.commands.SubCommand;
import com.mojang.authlib.GameProfile;


import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.UUID;


public class OpenGuiCommand extends SubCommand  {
    @Override
    public String getName() {
        return "gui";
    }


    @Override
    public String getDescription() {
        return "open the Town and Nation's gui";
    }
    public int getArguments(){ return 2;}


    @Override
    public String getSyntax() {
        return "/tan gui";
    }

    @Override
    public void perform(Player player, String[] args){
        if (args.length == 1){

            getOpeningGui(player);
        }else if(args.length > 1){
            player.sendMessage("Too many arguments");
            player.sendMessage("Correct Syntax: /tan gui");
        }

    }

    private void getOpeningGui(Player player) {
        Inventory inventory = Bukkit.createInventory(player,27, ChatColor.BLACK + "Debug Item Menu");

        //Kingdom
        ItemStack KingdomHead = makeSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY5MTk2YjMzMGM2Yjg5NjJmMjNhZDU2MjdmYjZlY2NlNDcyZWFmNWM5ZDQ0Zjc5MWY2NzA5YzdkMGY0ZGVjZSJ9fX0=");
        ItemStack KingdomHead2 = getSkullFromName("X4RDONIAK");

        player.openInventory(inventory);

        return ;
}

    private ItemStack getSkullFromName(String name) {
        ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();

        skullMeta.setOwner(name);
        skullItem.setItemMeta(skullMeta);

        return skullItem;
    }

    public ItemStack makeSkull(String base64EncodedString) {
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
        skull.setItemMeta(meta);
        return skull;
    }

}



