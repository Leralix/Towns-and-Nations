package org.tan.towns_and_nations.commands;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.GUI.GuiManager2;
import org.tan.towns_and_nations.utils.HeadUtils;
import org.tan.towns_and_nations.utils.PlayerChatListenerStorage;
import org.tan.towns_and_nations.utils.PlayerStatStorage;
import org.tan.towns_and_nations.utils.TownDataStorage;

import java.io.IOException;
import java.util.*;

public class DebugCommand implements CommandExecutor, TabExecutor {

    private String[] commandes = {"playerstats", "savestats", "itemtab", "getplayerstorage", "gettownstats", "spawnvillager", "newgui"};

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if (!sender.hasPermission("townandnation.admin")) {
            sender.sendMessage("You do not have the permission to do that");
            return false;
        }
        if(args == null){
            sender.sendMessage("Unknown debug, commands are:");
            for (String commande : commandes) {
                sender.sendMessage(commande);
            }
            return false;
        }
        Player player = (Player) sender;
        if (label.equalsIgnoreCase("tandebug")) {
            sender.sendMessage(args[0]);
            switch (args[0]) {

                case "newgui":
                    GuiManager2.openMainMenu(player);
                    break;
                case "addnewfeatures":
                    LinkedHashMap<String, TownDataClass> towns  = TownDataStorage.getTownList();
                    for (Map.Entry<String, TownDataClass> e : towns.entrySet()) {
                        TownDataClass value = e.getValue();
                        value.setOverlord(Bukkit.getServer().getOfflinePlayer(UUID.fromString(value.getUuidLeader())).getName());
                    }
                    player.sendMessage("Commande executée");
                    break;

                case "addmoney":
                    if (args.length < 3) {
                        player.sendMessage("Not enough arguments");
                    } else if (args.length == 3) {
                        PlayerDataClass target = PlayerStatStorage.findStatUUID(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString());
                        int amount = 0;
                        try {
                            amount = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            player.sendMessage("Invalid Syntax for the amount of money");
                            throw new RuntimeException(e);
                        }

                        target.addToBalance(amount);
                        player.sendMessage("Added " + amount + " Ecu to " + target.getPlayerName());
                    } else {
                        player.sendMessage("Too many arguments");
                    }
                    break;

                case "spawnvillager":
                    sender.sendMessage("test");
                    Villager villager = player.getWorld().spawn(player.getLocation(), Villager.class);
                    villager.setAI(false);
                    villager.setCustomName("Goldsmith");
                    villager.setCustomNameVisible(true);
                    villager.setProfession(Villager.Profession.TOOLSMITH);
                    villager.setInvulnerable(true);
                    player.sendMessage("Villager created!");
                    break;

                case "chatstorage":
                    player.sendMessage(PlayerChatListenerStorage.getData().toString());
                    break;

                case "townstats":
                    player.sendMessage("Liste des villes:");
                    HashMap<String, TownDataClass> test = TownDataStorage.getTownList();
                    for (Map.Entry<String, TownDataClass> e : test.entrySet()) {
                        String key = e.getKey();
                        TownDataClass value = e.getValue();
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
                        if (TownDataStorage.getTown(stat.getTownId()) != null) {
                            townName = TownDataStorage.getTown(stat.getTownId()).getTownName();
                        } else
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
                default:
                    player.sendMessage("Unknown debug, commands are:");
                    for (String commande : commandes) {
                        player.sendMessage(commande);
                    }
            }
            return true;
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}


