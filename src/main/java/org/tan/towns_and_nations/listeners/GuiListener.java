package org.tan.towns_and_nations.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.GUI.GuiManager;
import org.tan.towns_and_nations.commands.subcommands.OpenGuiCommand;
import org.tan.towns_and_nations.utils.PlayerChatListenerStorage;
import org.tan.towns_and_nations.utils.PlayerStatStorage;
import org.tan.towns_and_nations.utils.TownDataStorage;

public class GuiListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event){

        if (!event.getClick().equals(ClickType.LEFT)) {
            return;
        }



        if(event.getCurrentItem() == null){
            return;
        }
        if(event.getCurrentItem().getItemMeta() == null){
            return;
        }

        ItemStack itemStack = event.getCurrentItem();
        Material item = itemStack.getType();
        String itemName = itemStack.getItemMeta().getDisplayName().substring(2);
        Player player = (Player) event.getWhoClicked();
        PlayerDataClass playerStat = PlayerStatStorage.findStatUUID(player.getUniqueId().toString());


        boolean back = item.equals(Material.ARROW) && itemName.equals("Back");
        String title = event.getView().getTitle();
        //Gui menu intro //////////
        if(title.equalsIgnoreCase(ChatColor.BLACK + "Towns and Nations")){
            //Kingdom
            if(item.equals(Material.PLAYER_HEAD) && itemName.equals("Kingdom"))
                event.getWhoClicked().sendMessage("Encore en dev");
            //Region
            if(item.equals(Material.PLAYER_HEAD) && itemName.equals("Region"))
                event.getWhoClicked().sendMessage("Encore en dev");
            //Town
            if(item.equals(Material.PLAYER_HEAD) && itemName.equals("Town"))
               GuiManager.OpenTownMenu((Player) event.getWhoClicked());
            //Profil
            if(item.equals(Material.PLAYER_HEAD) && itemName.equals("Profil"))
                GuiManager.OpenProfileMenu((Player) event.getWhoClicked());

            if(item.equals(Material.ARROW) && itemName.equals("Quit"))
                player.closeInventory();

            event.setCancelled(true);
        }

        //Gui menu Profil //////////
        if(title.equalsIgnoreCase(ChatColor.BLACK + "Profil")){
            event.setCancelled(true);
        }

        //Gui menu NoTown //////////
        if(title.equalsIgnoreCase(ChatColor.BLACK + "Town")){

            if(item.equals(Material.GRASS_BLOCK) && itemName.equals("Create new Town")){
                if(PlayerStatStorage.findStatUUID(player.getUniqueId().toString()).getBalance() < 100){
                    player.sendMessage("You don't have enough money");
                }
                else {
                    player.sendMessage("Write the name of the town in the chat");
                    player.closeInventory();
                    PlayerChatListenerStorage.addPlayer(player);
                }
            }

            if(item.equals(Material.ANVIL) && itemName.equals("Join a Town"))
                GuiManager.OpenSearchTownMenu(player);

            event.setCancelled(true);
        }

        //Gui menu SearchTown //////////
        if(title.equalsIgnoreCase(ChatColor.BLACK + "Search Town")){

            event.setCancelled(true);
        }

        //Gui menu Havetown //////////
        if(title.equalsIgnoreCase(ChatColor.BLACK + "Town Menu")){


            if(checkItem(itemStack, Material.PLAYER_HEAD, "Members")){
                GuiManager.OpenTownMemberList(player);
            }

            if(checkItem(itemStack, Material.PLAYER_HEAD, "Relations")){
                GuiManager.OpenTownRelation(player);
            }

            if(checkItem(itemStack, Material.PLAYER_HEAD, "Settings")){
                GuiManager.OpenTownSettings(player);
            }


            event.setCancelled(true);
        }

        //Gui menu townMembers //////////
        if(title.equalsIgnoreCase(ChatColor.BLACK + "Town Members")){
            event.setCancelled(true);
        }

        //Gui menu townRelation //////////
        if(title.equalsIgnoreCase(ChatColor.BLACK + "Town Relation")){

            if(checkItem(itemStack, Material.IRON_SWORD, "War")){
                GuiManager.OpenTownWarRelation(player);
            }

            event.setCancelled(true);
        }

        //Gui menu addTownRelation //////////
        if(title.equalsIgnoreCase(ChatColor.BLACK + "Town Relation - War")){

            if(checkItem(itemStack, Material.PLAYER_HEAD, "add town")){
                GuiManager.OpenTownRelationAdd(player,"war");
            }

            event.setCancelled(true);
        }

        //Gui menu TownSettings //////////
        if(title.equalsIgnoreCase(ChatColor.BLACK + "Town Settings")){

            if(checkItem(itemStack,Material.BARRIER, "Leave Town")){

                if(TownDataStorage.getTown(playerStat.getTownId()).getUuidLeader().equals(playerStat.getUuid())){
                    player.sendMessage("You can't leave a town you are the leader, you need to disband it or give the leadership to someone else");
                }
                else{

                    TownDataStorage.getTown(playerStat.getTownId()).removePlayer(player.getUniqueId().toString());
                    playerStat.setTownId(null);
                    player.sendMessage("You left the town");
                    player.closeInventory();
                }
            }

            if(checkItem(itemStack,Material.BARRIER, "Delete Town")){


                if(!TownDataStorage.getTown(playerStat.getTownId()).getUuidLeader().equals(playerStat.getUuid())){
                    player.sendMessage("You can't delete a town if you are not the leader");
                }
                else{
                    TownDataStorage.removeTown(playerStat.getTownId());
                    playerStat.setTownId(null);
                    player.closeInventory();
                    player.sendMessage("Town deleted");
                }
            }

            event.setCancelled(true);
        }

        //Gui menu Region //////////
        if(title.equalsIgnoreCase(ChatColor.BLACK + "Region")){
            event.setCancelled(true);
        }


        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Kingdom")){
            event.setCancelled(true);
        }

        if(back)
            GuiManager.OpenMainMenu((Player) event.getWhoClicked());

    }


    private boolean checkItem(ItemStack item, Material materialtest, String nameTest){

        Material itemMaterial = item.getType();
        String itemName = item.getItemMeta().getDisplayName().substring(2);
        System.out.println(itemName);
        return itemMaterial.equals(materialtest) && itemName.equals(nameTest);

    }

}
