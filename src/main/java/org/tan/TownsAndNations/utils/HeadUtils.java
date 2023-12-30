package org.tan.TownsAndNations.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.tan.TownsAndNations.utils.EconomyUtil.getBalance;

public class HeadUtils {



    public static ItemStack getPlayerHeadInformation(Player p){
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();

        skullMeta.setOwningPlayer(p);
        skullMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + p.getName());

        head.setItemMeta(skullMeta);

        PlayerData playerData = PlayerDataStorage.get(p);
        TownData playerTown = TownDataStorage.get(playerData);


        if(playerTown != null){
            setLore(head,
                    Lang.GUI_PLAYER_PROFILE_DESC1.getTranslation(getBalance(p)),
                    Lang.GUI_PLAYER_PROFILE_DESC2.getTranslation(playerTown.getName()),
                    Lang.GUI_PLAYER_PROFILE_DESC3.getTranslation(playerTown.getRank(playerData.getTownRankID()).getColoredName())
            );

        }
        else {
            setLore(head,
                    Lang.GUI_PLAYER_PROFILE_DESC1.getTranslation(EconomyUtil.getBalance(p)),
                    Lang.GUI_PLAYER_PROFILE_NO_TOWN.getTranslation()
            );
        }

        return head;
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
        UUID id = UUID.nameUUIDFromBytes(base64EncodedString.getBytes());
        int less = (int)id.getLeastSignificantBits();
        int most = (int)id.getMostSignificantBits();
        ItemStack skull = Bukkit.getUnsafe().modifyItemStack(new ItemStack(Material.PLAYER_HEAD),
                "{SkullOwner:{Id:[I;" + less * most + "," + (less >> 23) + "," + most/less + "," +
                        most * 8731 + "],Properties:{textures:[{Value:\"" + base64EncodedString + "\"}]}}}");

        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + name);
        skull.setItemMeta(meta);
        return skull;
    }

    public static ItemStack getTownIcon(String TownId){

        TownData town = TownDataStorage.get(TownId);
        ItemStack itemStack = town.getTownIconItemStack();
        if(itemStack == null){
            return HeadUtils.getPlayerHead(town.getName(), Bukkit.getOfflinePlayer(UUID.fromString(town.getUuidLeader())));
        }
        else {
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + town.getName());
            itemStack.setItemMeta(meta);
            return itemStack;
        }
    }

    public static ItemStack getTownIconWithInformations(String TownId){

        TownData town = TownDataStorage.get(TownId);
        ItemStack icon = HeadUtils.getTownIcon(town.getID());
        if (icon == null){
            icon =  HeadUtils.getPlayerHead(town.getName(), Bukkit.getOfflinePlayer(UUID.fromString(town.getUuidLeader())));
        }
        ItemMeta meta = icon.getItemMeta();
        List<String> lore = new ArrayList<>();

        lore.add(Lang.GUI_TOWN_INFO_DESC0.getTranslation(town.getDescription()));
        lore.add(Lang.GUI_TOWN_INFO_DESC1.getTranslation(Bukkit.getOfflinePlayer(UUID.fromString(town.getUuidLeader())).getName()));
        lore.add(Lang.GUI_TOWN_INFO_DESC2.getTranslation(town.getPlayerList().size()));
        lore.add(Lang.GUI_TOWN_INFO_DESC3.getTranslation(town.getChunkSettings().getNumberOfClaimedChunk()));

        meta.setLore(lore);
        icon.setItemMeta(meta);
        return icon;
    }

    public static ItemStack getTownIconWithInformations(String TownId,String ownTownID){

        TownData town = TownDataStorage.get(TownId);
        ItemStack icon = town.getTownIconItemStack();

        if (icon == null){
            icon =  HeadUtils.getPlayerHead(town.getName(), Bukkit.getOfflinePlayer(UUID.fromString(town.getUuidLeader())));
        }
        ItemMeta meta = icon.getItemMeta();
        List<String> lore = new ArrayList<>();

        TownRelation relation = town.getRelationWith(ownTownID);
        String relationName;
        if(relation == null){
            relationName = Lang.GUI_TOWN_RELATION_NEUTRAL.getTranslation();
        }
        else {
            relationName = relation.getColor() + relation.getName();
        }

        lore.add(Lang.GUI_TOWN_INFO_DESC0.getTranslation(town.getDescription()));
        lore.add(Lang.GUI_TOWN_INFO_DESC1.getTranslation(Bukkit.getOfflinePlayer(UUID.fromString(town.getUuidLeader())).getName()));
        lore.add(Lang.GUI_TOWN_INFO_DESC2.getTranslation(town.getPlayerList().size()));
        lore.add(Lang.GUI_TOWN_INFO_DESC3.getTranslation(town.getChunkSettings().getNumberOfClaimedChunk()));
        lore.add(Lang.GUI_TOWN_INFO_TOWN_RELATION.getTranslation(relationName));

        meta.setLore(lore);
        icon.setItemMeta(meta);
        return icon;
    }

    public static ItemStack getCustomLoreItem(Material itemMaterial, String itemName){
        return getCustomLoreItem(itemMaterial,itemName,(String)null);
    }
    public static ItemStack getCustomLoreItem(Material itemMaterial, String itemName, String... loreLines){
        ItemStack item = new ItemStack(itemMaterial);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + itemName);

        if(loreLines != null){
            List<String> lore = Arrays.asList(loreLines);
            meta.setLore(lore);
        }


        item.setItemMeta(meta);
        return item;
    }

    public static void setLore(ItemStack itemStack, List<String> lore){
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
    }

    public static void setLore(ItemStack itemStack, String... loreLines) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = Arrays.asList(loreLines);
        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);
    }

    public static void addLore(ItemStack itemStack, String... loreLines) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = itemMeta.getLore();
        if(lore == null){
            lore = new ArrayList<>();
        }
        lore.addAll(Arrays.asList(loreLines));
        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);
    }

}
