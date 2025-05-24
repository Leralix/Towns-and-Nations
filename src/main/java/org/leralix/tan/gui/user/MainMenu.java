package org.leralix.tan.gui.user;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.GuiUtil;

public class MainMenu extends BasicGui {

    public MainMenu(Player player) {
        super(player, Lang.HEADER_MAIN_MENU, 3);
    }

    public void open() {

        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        gui.setItem(2, 2, getNationButton(playerData));
        gui.setItem(2, 4, getRegionButton(playerData));
        gui.setItem(2, 6, getTownButton(playerData));
        gui.setItem(2, 8, getPlayerButton(playerData));

        gui.setItem(3,1, GuiUtil.createBackArrow(player, HumanEntity::closeInventory));

        gui.open(player);
    }

    private GuiItem getNationButton(PlayerData playerData) {
        return iconManager.get(IconKey.NATION_BASE_ICON)
                .setName(Lang.GUI_KINGDOM_ICON.get(playerData))
                .setDescription(Lang.GUI_WARNING_STILL_IN_DEV.get(playerData))
                .setAction(action -> {
                    player.sendMessage(Lang.GUI_WARNING_STILL_IN_DEV.get(playerData));
                    SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                })
                .asGuiItem(player);
    }

    private GuiItem getRegionButton(PlayerData playerData) {

        String description = playerData.hasRegion() ?
                Lang.GUI_REGION_ICON_DESC1_REGION.get(playerData.getRegion().getColoredName()) :
                Lang.GUI_REGION_ICON_DESC1_NO_REGION.get(playerData);

        return iconManager.get(IconKey.REGION_BASE_ICON)
                .setName(Lang.GUI_REGION_ICON.get(playerData))
                .setDescription(description)
                .setAction(action -> {
                    if (playerData.hasRegion()) {
                        PlayerGUI.dispatchPlayerRegion(player);
                    } else {
                        PlayerGUI.openNoRegionMenu(player);
                    }
                })
                .asGuiItem(player);
    }

    private GuiItem getTownButton(PlayerData playerData) {
        String description = playerData.hasTown() ?
                Lang.GUI_TOWN_ICON_DESC1_HAVE_TOWN.get(playerData, playerData.getTown().getColoredName()) :
                Lang.GUI_TOWN_ICON_DESC1_NO_TOWN.get(playerData);

        return iconManager.get(IconKey.TOWN_BASE_ICON)
                .setName(Lang.GUI_TOWN_ICON.get(playerData))
                .setDescription(description)
                .setAction(action -> {
                    if (playerData.hasRegion()) {
                        PlayerGUI.dispatchPlayerTown(player);
                    } else {
                        PlayerGUI.openNoTownMenu(player);
                    }
                })
                .asGuiItem(player);
    }

    private GuiItem getPlayerButton(PlayerData playerData) {
        return iconManager.get(IconKey.PLAYER_BASE_ICON)
                .setName(Lang.GUI_PLAYER_PROFILE_HEADER.get(playerData, player.getName()))
                .setDescription(Lang.GUI_PLAYER_PROFILE_DESC1.get(playerData, playerData.getBalance()))
                .setAction(action -> PlayerGUI.openPlayerProfileMenu(player))
                .asGuiItem(player);
    }


}
