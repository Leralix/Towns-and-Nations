package org.leralix.tan.gui.legacy;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.admin.AdminBrowsePlayers;
import org.leralix.tan.gui.admin.AdminManageTown;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


public class AdminGUI {

    private AdminGUI() {
        throw new IllegalStateException("Utility class");
    }

    public static void openChooseNewOverlord(Player player, TownData territoryData, int page) {
        LangType langType = PlayerDataStorage.getInstance().get(player).getLang();

        Gui gui = GuiUtil.createChestGui(Lang.HEADER_ADMIN_CHANGE_OVERLORD.get(langType, territoryData.getName()), 6);

        Collection<RegionData> territoryDataList = RegionDataStorage.getInstance().getAll().values();
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        List<GuiItem> guiItems = new ArrayList<>();

        for (TerritoryData potentialOverlord : territoryDataList) {
            ItemStack potentialOverlordIcon = potentialOverlord.getIconWithInformations(tanPlayer.getLang());
            HeadUtils.addLore(potentialOverlordIcon, Lang.GUI_GENERIC_CLICK_TO_SELECT.get(langType));

            guiItems.add(ItemBuilder.from(potentialOverlordIcon).asGuiItem(event -> {
                event.setCancelled(true);
                territoryData.setOverlord(potentialOverlord);
                new AdminManageTown(player, territoryData);
            }));
        }
        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> new AdminManageTown(player, territoryData),
                p -> openChooseNewOverlord(player, territoryData, page + 1),
                p -> openChooseNewOverlord(player, territoryData, page - 1));

        gui.open(player);

    }

    private static void openSpecificPlayerMenu(Player player, ITanPlayer tanPlayer) {
        LangType langType = PlayerDataStorage.getInstance().get(player).getLang();

        Gui gui = GuiUtil.createChestGui(Lang.HEADER_ADMIN_PLAYER_MENU.get(langType), 3);

        ItemStack playerHead = HeadUtils.getPlayerHeadInformation(Bukkit.getOfflinePlayer(UUID.fromString(tanPlayer.getID())));

        if (tanPlayer.hasTown()) {
            ItemStack removePlayerTown = HeadUtils.createCustomItemStack(Material.SPRUCE_DOOR,
                    Lang.ADMIN_GUI_TOWN_PLAYER_TOWN.get(langType, tanPlayer.getTown().getName()),
                    Lang.ADMIN_GUI_TOWN_PLAYER_TOWN_DESC1.get(langType),
                    Lang.ADMIN_GUI_TOWN_PLAYER_TOWN_DESC2.get(langType));


            GuiItem removePlayerTownGui = ItemBuilder.from(removePlayerTown).asGuiItem(event -> {
                event.setCancelled(true);
                TownData townData = tanPlayer.getTown();

                if (townData.isLeader(tanPlayer)) {
                    TanChatUtils.message(player, Lang.GUI_TOWN_MEMBER_CANT_KICK_LEADER.get(langType));
                    return;
                }
                townData.removePlayer(tanPlayer);

                TanChatUtils.message(player, Lang.ADMIN_GUI_TOWN_PLAYER_LEAVE_TOWN_SUCCESS.get(langType, tanPlayer.getNameStored(), townData.getName()));
                openSpecificPlayerMenu(player, tanPlayer);
            });
            gui.setItem(2, 2, removePlayerTownGui);
        } else {
            ItemStack addPlayerTown = HeadUtils.createCustomItemStack(Material.SPRUCE_DOOR, "Add player to town", "Add player to town");

            GuiItem addPlayerTownGui = ItemBuilder.from(addPlayerTown).asGuiItem(event -> {
                event.setCancelled(true);
                setPlayerTown(player, tanPlayer, 0);
            });
            gui.setItem(2, 2, addPlayerTownGui);
        }


        GuiItem playerHeadGui = ItemBuilder.from(playerHead).asGuiItem(event -> event.setCancelled(true));

        gui.setItem(1, 5, playerHeadGui);
        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new AdminBrowsePlayers(player)));

        gui.open(player);
    }

    private static void setPlayerTown(Player player, ITanPlayer tanPlayer, int page) {
        LangType langType = PlayerDataStorage.getInstance().get(player).getLang();

        Gui gui = GuiUtil.createChestGui(Lang.HEADER_ADMIN_SET_PLAYER_TOWN.get(langType), 6);

        ArrayList<GuiItem> guiItems = new ArrayList<>();


        for (TownData townData : TownDataStorage.getInstance().getAll().values()) {
            ItemStack townIcon = townData.getIconWithInformations(tanPlayer.getLang());
            HeadUtils.addLore(townIcon,
                    "",
                    Lang.ADMIN_GUI_LEFT_CLICK_TO_MANAGE_TOWN.get(langType)
            );
            GuiItem townIterationGui = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);
                townData.addPlayer(tanPlayer);
                openSpecificPlayerMenu(player, tanPlayer);
            });
            guiItems.add(townIterationGui);
        }

        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> openSpecificPlayerMenu(player, tanPlayer),
                p -> setPlayerTown(player, tanPlayer, page + 1),
                p -> setPlayerTown(player, tanPlayer, page - 1)
        );
        gui.open(player);

    }
}
