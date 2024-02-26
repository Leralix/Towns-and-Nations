package org.tan.TownsAndNations.GUI;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.MessageKey;
import org.tan.TownsAndNations.storage.PlayerChatListenerStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.HeadUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.tan.TownsAndNations.GUI.GuiManager2.OpenTownChangeOwnershipPlayerSelect;
import static org.tan.TownsAndNations.enums.ChatCategory.*;
import static org.tan.TownsAndNations.utils.TownUtil.deleteTown;

public class AdminGUI {
    public static void OpenMainMenu(Player player){


        String name = "Main menu - Admin";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        ItemStack TownHead = HeadUtils.makeSkull(Lang.GUI_TOWN_ICON.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=");
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, "Quit");

        HeadUtils.setLore(TownHead, Lang.ADMIN_GUI_TOWN_DESC.get());
        HeadUtils.setLore(playerHead, Lang.ADMIN_GUI_PLAYER_DESC.get());

        GuiItem Town = ItemBuilder.from(TownHead).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuDebug(player);
        });

        GuiItem Player = ItemBuilder.from(playerHead).asGuiItem(event -> event.setCancelled(true));
        GuiItem Back = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            player.closeInventory();
        });

        gui.setItem(14,Town);
        gui.setItem(16,Player);
        gui.setItem(18,Back);

        gui.open(player);
    }

    public static void OpenTownMenuDebug(Player player){


        String name = "Main menu - Admin";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        int i = 0;
        for (TownData townData : TownDataStorage.getTownMap().values()) {


            ItemStack townIcon = HeadUtils.getTownIcon(townData);



            HeadUtils.setLore(townIcon,
                    Lang.GUI_TOWN_INFO_DESC0.get(townData.getDescription()),
                    Lang.GUI_TOWN_INFO_DESC1.get(Bukkit.getServer().getOfflinePlayer(UUID.fromString(townData.getLeaderID())).getName()),
                    Lang.GUI_TOWN_INFO_DESC2.get(townData.getPlayerList().size()),
                    Lang.GUI_TOWN_INFO_DESC3.get(townData.getNumberOfClaimedChunk()),
                    "",
                    Lang.ADMIN_GUI_LEFT_CLICK_TO_MANAGE_TOWN.get()
            );

            GuiItem _townIteration = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);

                OpenSpecificTownMenu(player, townData);

            });

            gui.setItem(i, _townIteration);
            i++;

        }
        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.get());
        GuiItem _back = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenMainMenu(player);
        });

        gui.setItem(3,1, _back);

        gui.open(player);



    }

    private static void OpenSpecificTownMenu(Player player, TownData townData) {


        String name = "Main menu - Admin";
        int nRow = 3;

        Gui gui = Gui.gui()
                .title(Component.text(name))
                .type(GuiType.CHEST)
                .rows(nRow)
                .create();

        ItemStack changeTownName = HeadUtils.getCustomLoreItem(Material.NAME_TAG,
                Lang.ADMIN_GUI_CHANGE_TOWN_NAME.get(),
                Lang.ADMIN_GUI_CHANGE_TOWN_NAME_DESC1.get(),
                Lang.ADMIN_GUI_CHANGE_TOWN_NAME_DESC2.get(townData.getName()));
        ItemStack changeTownDescription = HeadUtils.getCustomLoreItem(Material.WRITABLE_BOOK,
                Lang.ADMIN_GUI_CHANGE_TOWN_DESCRIPTION.get(),
                Lang.ADMIN_GUI_CHANGE_TOWN_DESCRIPTION_DESC1.get(townData.getDescription()));
        ItemStack changeTownLeader = HeadUtils.getCustomLoreItem(Material.PLAYER_HEAD,
                Lang.ADMIN_GUI_CHANGE_TOWN_LEADER.get(),
                Lang.ADMIN_GUI_CHANGE_TOWN_LEADER_DESC1.get(Bukkit.getServer().getOfflinePlayer(UUID.fromString(townData.getLeaderID())).getName()));
        ItemStack deleteTown = HeadUtils.getCustomLoreItem(Material.BARRIER,
                Lang.ADMIN_GUI_DELETE_TOWN.get(),
                Lang.ADMIN_GUI_DELETE_TOWN_DESC1.get(townData.getName()));

        ItemStack getBackArrow = HeadUtils.getCustomLoreItem(Material.ARROW, Lang.GUI_BACK_ARROW.get());


        GuiItem _changeTownName = ItemBuilder.from(changeTownName).asGuiItem(event -> {

            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            Map<MessageKey, String> data = new HashMap<>();

            data.put(MessageKey.TOWN_ID,townData.getID());
            data.put(MessageKey.COST,Integer.toString(0));

            PlayerChatListenerStorage.addPlayer(CHANGE_TOWN_NAME,player,data);
            player.closeInventory();

        });
        GuiItem _changeTownDescription = ItemBuilder.from(changeTownDescription).asGuiItem(event -> {
            player.closeInventory();
            player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get());
            Map<MessageKey, String> data = new HashMap<>();
            data.put(MessageKey.TOWN_ID,townData.getID());
            PlayerChatListenerStorage.addPlayer(CHANGE_TOWN_DESCRIPTION,player,data);
            event.setCancelled(true);
        });

        GuiItem _changeTownLeader = ItemBuilder.from(changeTownLeader).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownChangeOwnershipPlayerSelect(player, townData);
        });
        GuiItem _deleteTown = ItemBuilder.from(deleteTown).asGuiItem(event -> {
            event.setCancelled(true);
            deleteTown(townData);

            player.closeInventory();
            player.sendMessage(ChatUtils.getTANString() + Lang.CHAT_PLAYER_TOWN_SUCCESSFULLY_DELETED.get());
        });
        GuiItem _back = ItemBuilder.from(getBackArrow).asGuiItem(event -> {
            event.setCancelled(true);
            OpenTownMenuDebug(player);
        });

        gui.setItem(3,1, _back);
        gui.setItem(2,2, _changeTownName);
        gui.setItem(2,4, _changeTownDescription);
        gui.setItem(2,6, _changeTownLeader);
        gui.setItem(2,8, _deleteTown);



        gui.open(player);
    }
}
