package org.leralix.tan.gui.user;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.gui.user.player.PlayerMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends BasicGui {

    public MainMenu(Player player) {
        super(player, Lang.HEADER_MAIN_MENU, 3);
        open();
    }

    @Override
    public void open() {

        gui.setItem(1, 5, getTimeIcon());

        int nationPosition = 2;
        int regionPosition = 4;
        int townPosition = 6;
        int playerPosition = 8;

        if (Constants.enableRegion()) {
            if (Constants.enableNation()) {
                gui.setItem(2, nationPosition, getNationButton(tanPlayer));
            } else {
                regionPosition = 3;
                townPosition = 5;
                playerPosition = 7;
            }
            gui.setItem(2, regionPosition, getRegionButton(tanPlayer));
        } else {
            townPosition = 4;
            playerPosition = 6;
        }

        gui.setItem(2, townPosition, getTownButton(tanPlayer));
        gui.setItem(2, playerPosition, getPlayerButton(tanPlayer));

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, HumanEntity::closeInventory));

        gui.open(player);
    }

    private @NotNull GuiItem getTimeIcon() {

        TimeZoneManager timeManager = TimeZoneManager.getInstance();

        IconKey icon = timeManager.isDayForServer() ?
                IconKey.TIMEZONE_ICON_DAY :
                IconKey.TIMEZONE_ICON_NIGHT;


        return iconManager.get(icon)
                .setName(Lang.GUI_SERVER_TIME.get(tanPlayer))
                .setDescription(
                        Lang.CURRENT_SERVER_TIME.get(timeManager.formatDateNowForServer().get(langType)),
                        Lang.CURRENT_PLAYER_TIME.get(timeManager.formatDateNowForPlayer(tanPlayer).get(langType))
                )
                .asGuiItem(player, langType);
    }

    private GuiItem getNationButton(ITanPlayer tanPlayer) {
        return iconManager.get(IconKey.NATION_BASE_ICON)
                .setName(Lang.GUI_KINGDOM_ICON.get(tanPlayer))
                .setDescription(Lang.GUI_WARNING_STILL_IN_DEV.get())
                .setAction(action -> TanChatUtils.message(player, Lang.GUI_WARNING_STILL_IN_DEV.get(tanPlayer), SoundEnum.NOT_ALLOWED))
                .asGuiItem(player, langType);
    }

    private GuiItem getRegionButton(ITanPlayer tanPlayer) {

        List<FilledLang> description = new ArrayList<>();

        if (tanPlayer.hasRegion()) {
            RegionData regionData = tanPlayer.getRegion();
            description.add(Lang.GUI_REGION_ICON_DESC1_REGION.get(regionData.getColoredName()));
            description.add(Lang.GUI_REGION_ICON_DESC2_REGION.get(regionData.getRank(tanPlayer).getColoredName()));
        } else {
            description.add(Lang.GUI_REGION_ICON_DESC1_NO_REGION.get());
        }


        return iconManager.get(IconKey.REGION_BASE_ICON)
                .setName(Lang.GUI_REGION_ICON.get(tanPlayer))
                .setDescription(description)
                .setAction(action -> PlayerGUI.dispatchPlayerRegion(player))
                .asGuiItem(player, langType);
    }

    private GuiItem getTownButton(ITanPlayer tanPlayer) {

        List<FilledLang> description = new ArrayList<>();
        if (tanPlayer.hasTown()) {
            description.add(Lang.GUI_TOWN_ICON_DESC1_HAVE_TOWN.get(tanPlayer.getTown().getColoredName()));
            description.add(Lang.GUI_TOWN_ICON_DESC2_HAVE_TOWN.get(tanPlayer.getTown().getRank(tanPlayer).getColoredName()));
        } else {
            description.add(Lang.GUI_TOWN_ICON_DESC1_NO_TOWN.get());
        }

        return iconManager.get(IconKey.TOWN_BASE_ICON)
                .setName(Lang.GUI_TOWN_ICON.get(tanPlayer))
                .setDescription(description)
                .setAction(action -> PlayerGUI.dispatchPlayerTown(player))
                .asGuiItem(player, langType);
    }

    private GuiItem getPlayerButton(ITanPlayer tanPlayer) {
        return iconManager.get(IconKey.PLAYER_BASE_ICON)
                .setName(Lang.GUI_PLAYER_MENU_ICON.get(tanPlayer, player.getName()))
                .setAction(action -> new PlayerMenu(player))
                .asGuiItem(player, langType);
    }


}
