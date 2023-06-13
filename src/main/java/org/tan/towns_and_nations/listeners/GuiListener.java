package org.tan.towns_and_nations.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.GUI.GuiManager;
import org.tan.towns_and_nations.TownsAndNations;
import org.tan.towns_and_nations.storage.PlayerChatListenerStorage;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class GuiListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getClick().equals(ClickType.LEFT)) {
            return;
        }

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getItemMeta() == null) {
            return;
        }

        String itemName = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());
        Material itemType = itemStack.getType();
        Player player = (Player) event.getWhoClicked();
        PlayerDataClass playerStat = PlayerStatStorage.getStatUUID(player.getUniqueId().toString());
        Logger logger = TownsAndNations.getPluginLogger();

        boolean back = itemType.equals(Material.ARROW) && itemName.equals("Back");
        String title = ChatColor.stripColor(event.getView().getTitle());
        logger.info("Title name: " + title);

        if (title.equalsIgnoreCase("Towns and Nations")) {
            handleMainMenuClick(player, itemType, itemName);
        } else if (title.equalsIgnoreCase("Profil")) {
            event.setCancelled(true);
        } else if (title.equalsIgnoreCase("Town")) {
            handleTownMenuClick(player, itemStack, itemName, playerStat);
        } else if (title.equalsIgnoreCase("Search Town")) {
            event.setCancelled(true);
        } else if (title.equalsIgnoreCase("Town Menu")) {
            handleTownMenuActions(player, itemStack, itemName);
        } else if (title.equalsIgnoreCase("Town Members")) {
            event.setCancelled(true);
        } else if (title.equalsIgnoreCase("Town Relation")) {
            handleTownRelationActions(player, itemStack, itemName);
        } else if (title.equalsIgnoreCase("Town Relation - War")) {
            handleTownRelationInteraction(player, itemStack, itemName);
        } else if (title.equalsIgnoreCase("Town Relation - selection")) {
            handleRelationSelection(player, itemStack);
        } else if (title.equalsIgnoreCase("Town Settings")) {
            handleTownSettings(player, itemStack, itemName, playerStat);
        } else if (title.equalsIgnoreCase("Region")) {
            event.setCancelled(true);
        } else if (title.equalsIgnoreCase("Kingdom")) {
            event.setCancelled(true);
        }else{
            return;
        }

        if (back) {
            GuiManager.OpenMainMenu(player);
        }
        event.setCancelled(true);
    }

    private void handleMainMenuClick(Player player, Material item, String itemName) {
        if (item.equals(Material.PLAYER_HEAD)) {
            if (itemName.equals("Kingdom")) {
                player.sendMessage("Encore en dev");
            } else if (itemName.equals("Region")) {
                player.sendMessage("Encore en dev");
            } else if (itemName.equals("Town")) {
                GuiManager.OpenTownMenu(player);
            } else if (itemName.equals("Profil")) {
                GuiManager.OpenProfileMenu(player);
            }
        } else if (item.equals(Material.ARROW) && itemName.equals("Quit")) {
            player.closeInventory();
        }
    }

    private void handleTownMenuClick(Player player, ItemStack item, String itemName, PlayerDataClass playerStat) {
        if (checkItem(item, Material.GRASS_BLOCK, "Create new Town")){
            if (playerStat.getBalance() < 100) {
                player.sendMessage("You don't have enough money");
            } else {
                player.sendMessage("Write the name of the town in the chat");
                player.closeInventory();
                PlayerChatListenerStorage.addPlayer(player);
            }
        } else if (checkItem(item, Material.ANVIL, "Join a Town")) {
            GuiManager.OpenSearchTownMenu(player);
        }
    }

    private void handleTownMenuActions(Player player, ItemStack item, String itemName) {
        if (checkItem(item, Material.PLAYER_HEAD, "Members")) {
            GuiManager.OpenTownMemberList(player);
        } else if (checkItem(item, Material.PLAYER_HEAD, "Relations")) {
            GuiManager.OpenTownRelation(player);
        } else if (checkItem(item, Material.PLAYER_HEAD, "Settings")) {
            GuiManager.OpenTownSettings(player);
        }
    }

    private void handleTownRelationActions(Player player, ItemStack item, String itemName) {
        if (checkItem(item, Material.IRON_SWORD, "War")) {
            GuiManager.OpenTownRelations(player, "war");
        }
    }

    private void handleTownRelationInteraction(Player player, ItemStack item, String itemName) {
        if (checkItem(item, Material.PLAYER_HEAD, "add town")) {
            GuiManager.OpenTownRelationInteraction(player, "add", "war");
        }
    }

    private void handleRelationSelection(Player player, ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();

        String townId = retreiveStringMetaData(meta,"townId");
        String action = retreiveStringMetaData(meta,"action");
        String relation = retreiveStringMetaData(meta,"relation");

        // Création du dictionnaire associant les combinaisons à leurs commandes
        Map<String, Map<String, Runnable>> commandDict = new HashMap<>();

        // Définition des actions et relations possibles
        String[] actions = {"add", "remove"};
        String[] relations = {"war", "nap", "alliance", "embargo"};

        // Création des commandes pour chaque combinaison
        for (String act : actions) {
            commandDict.put(act, new HashMap<>());
            for (String rel : relations) {
                commandDict.get(act).put(rel, () -> {
                    // Commande spécifique pour l'action et la relation actuelles
                });
            }
        }

        // Exécution de la commande correspondant aux métadonnées
        if (commandDict.containsKey(action) && commandDict.get(action).containsKey(relation)) {
            commandDict.get(action).get(relation).run();
        }

    }

    private void handleTownSettings(Player player, ItemStack item, String itemName, PlayerDataClass playerStat) {
        if (checkItem(item, Material.BARRIER, "Leave Town")) {
            if (TownDataStorage.getTown(playerStat.getTownId()).getUuidLeader().equals(playerStat.getUuid())) {
                player.sendMessage("You can't leave a town you are the leader, you need to disband it or give the leadership to someone else");
            } else {
                TownDataStorage.getTown(playerStat.getTownId()).removePlayer(player.getUniqueId().toString());
                playerStat.setTownId(null);
                player.sendMessage("You left the town");
                player.closeInventory();
            }
        } else if (checkItem(item, Material.BARRIER, "Delete Town")) {
            if (!TownDataStorage.getTown(playerStat.getTownId()).getUuidLeader().equals(playerStat.getUuid())) {
                player.sendMessage("You can't delete a town if you are not the leader");
            } else {
                TownDataStorage.removeTown(playerStat.getTownId());
                playerStat.setTownId(null);
                player.closeInventory();
                player.sendMessage("Town deleted");
            }
        }
    }

    private boolean checkItem(ItemStack item, Material materialtest, String nameTest) {
        Material itemMaterial = item.getType();
        String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        return itemMaterial.equals(materialtest) && itemName.equals(nameTest);
    }

    private String retreiveStringMetaData(ItemMeta meta, String keyId){

        NamespacedKey key = new NamespacedKey(TownsAndNations.getPlugin(), keyId);
        if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            String selectedTownID = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            return selectedTownID;
        }
        else
            return null;

    }
}
