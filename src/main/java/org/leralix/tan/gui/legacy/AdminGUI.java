package org.leralix.tan.gui.legacy;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.admin.AdminManagePlayer;
import org.leralix.tan.gui.admin.AdminManageTown;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


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
            IconBuilder potentialOverlordIcon = potentialOverlord.getIconWithInformations(tanPlayer.getLang());
            potentialOverlordIcon.addDescription(Lang.GUI_GENERIC_CLICK_TO_SELECT.get());

            potentialOverlordIcon.setAction(action -> {
                territoryData.setOverlord(potentialOverlord);
                new AdminManageTown(player, territoryData);
            });
            guiItems.add(potentialOverlordIcon.asGuiItem(player, langType));
        }
        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> new AdminManageTown(player, territoryData),
                p -> openChooseNewOverlord(player, territoryData, page + 1),
                p -> openChooseNewOverlord(player, territoryData, page - 1));

        gui.open(player);

    }

    public static void setPlayerTown(Player player, ITanPlayer targetPlayer, int page) {
        LangType langType = PlayerDataStorage.getInstance().get(player).getLang();

        Gui gui = GuiUtil.createChestGui(Lang.HEADER_ADMIN_SET_PLAYER_TOWN.get(langType), 6);

        ArrayList<GuiItem> guiItems = new ArrayList<>();


        for (TownData townData : TownDataStorage.getInstance().getAll().values()) {
            IconBuilder townIcon = townData.getIconWithInformations(targetPlayer.getLang());
            townIcon.setClickToAcceptMessage(Lang.ADMIN_GUI_LEFT_CLICK_TO_MANAGE_TOWN);
            townIcon.setAction(action -> {
                townData.addPlayer(targetPlayer);
                new AdminManagePlayer(player, targetPlayer);
            });
            guiItems.add(townIcon.asGuiItem(player, langType));
        }

        GuiUtil.createIterator(gui, guiItems, page, player,
                p -> new AdminManagePlayer(player, targetPlayer),
                p -> setPlayerTown(player, targetPlayer, page + 1),
                p -> setPlayerTown(player, targetPlayer, page - 1)
        );
        gui.open(player);

    }
}
