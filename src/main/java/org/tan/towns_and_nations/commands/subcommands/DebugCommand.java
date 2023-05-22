package org.tan.towns_and_nations.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.utils.PlayerChatListenerStorage;
import org.tan.towns_and_nations.utils.PlayerStatStorage;
import org.tan.towns_and_nations.utils.TownDataStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DebugCommand extends SubCommand {


    private String[] commandes = {"playerstats","savestats","itemtab","getplayerstorage","gettownstats"};
    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public String getDescription() {
        return "debug everything in Towns and Nations";
    }
    public int getArguments(){ return 3;}

    @Override
    public String getSyntax() {
        return "/tan debug <DebugCommand>";
    }

    @Override
    public void perform(Player player, String[] args){
        if (args.length < 2){
            player.sendMessage("Not enough arguments");
            player.sendMessage("Correct Syntax: " + getSyntax());
        }
        else if(args.length == 2){
            switch(args[1]){

                case "getplayerstorage":
                    player.sendMessage(PlayerChatListenerStorage.getData().toString());
                    break;

                case "gettownstats":
                    player.sendMessage("Liste des villes:");
                    HashMap<String, TownDataClass> test = TownDataStorage.getTownList();
                    for (Map.Entry<String, TownDataClass> e : test.entrySet()) {
                        String key = e.getKey();
                        TownDataClass value  = e.getValue();
                        player.sendMessage(key + ": " + value.getTownName());
                    }
                    player.sendMessage("Prochaine clef: " + TownDataStorage.newTownId);

                    break;

                case "playerstats":
                    ArrayList<PlayerDataClass> stats = PlayerStatStorage.getStats();
                    for (PlayerDataClass stat : stats) {
                        String name = stat.getPlayerName();
                        int balance = stat.getBalance();
                        String townName;
                        if (TownDataStorage.getTown(stat.getTownId())!= null){
                            townName = TownDataStorage.getTown(stat.getTownId()).getTownName();
                        }
                        else
                            townName = null;

                        player.sendMessage(name +
                                ": " + balance +
                                " ecu, town: " + townName);
                    }
                    break;

                case "savestats":
                    try {
                        PlayerStatStorage.saveStats();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                case "itemtab":

                    Inventory inventory = Bukkit.createInventory(player,27, ChatColor.BLACK + "Debug Item Menu");

                    ItemStack rareGold = new ItemStack(Material.GOLD_NUGGET,64);
                    ItemMeta rareGoldMeta = rareGold.getItemMeta();
                    rareGoldMeta.setDisplayName("Rare gold");
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add("Can be used to create Money");
                    rareGoldMeta.setLore(lore);
                    rareGoldMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                    rareGoldMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    rareGold.setItemMeta(rareGoldMeta);
                    inventory.setItem(1,rareGold);

                    player.openInventory(inventory);
                    break;

                default:
                    player.sendMessage("Unkown debug, commands are:");
                    for (String commande : commandes){
                        player.sendMessage(commande);
                    }
            }

        }
        else {
            player.sendMessage("Too many arguments");
            player.sendMessage("Correct Syntax: " + getSyntax());
        }
    }

}
