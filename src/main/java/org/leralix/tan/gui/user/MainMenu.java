package org.leralix.tan.gui.user;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.player.PlayerMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.Constants;
import org.leralix.tan.utils.GuiUtil;

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

        if(Constants.enableRegion()){
            if(Constants.enableNation()){
                gui.setItem(2, 2, getNationButton(playerData));
            }
            gui.setItem(2, 4, getRegionButton(playerData));
        }

        gui.setItem(2, 6, getTownButton(playerData));
        gui.setItem(2, 8, getPlayerButton(playerData));

        gui.setItem(3,1, GuiUtil.createBackArrow(player, HumanEntity::closeInventory));

        gui.open(player);
    }

    private @NotNull GuiItem getTimeIcon() {

        TimeZoneManager timeManager = TimeZoneManager.getInstance();

        IconKey icon = timeManager.isDayForServer() ?
                IconKey.TIMEZONE_ICON_DAY :
                IconKey.TIMEZONE_ICON_NIGHT;


        return iconManager.get(icon)
                .setName(Lang.GUI_SERVER_TIME.get(playerData))
                .setDescription(
                        Lang.CURRENT_SERVER_TIME.get(playerData, timeManager.formatDateNowForServer()),
                        Lang.CURRENT_PLAYER_TIME.get(playerData, timeManager.formatDateNowForPlayer(playerData))
                        )
                .asGuiItem(player);
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

        List<String> description = new ArrayList<>();

        if(playerData.hasRegion()){
            RegionData regionData = playerData.getRegion();
            description.add(Lang.GUI_REGION_ICON_DESC1_REGION.get(playerData, regionData.getColoredName()));
            description.add(Lang.GUI_REGION_ICON_DESC2_REGION.get(playerData, regionData.getRank(playerData).getColoredName()));
        }
        else {
            description.add(Lang.GUI_REGION_ICON_DESC1_NO_REGION.get(playerData));
        }
        description.add(Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData));


        return iconManager.get(IconKey.REGION_BASE_ICON)
                .setName(Lang.GUI_REGION_ICON.get(playerData))
                .setDescription(description)
                .setAction(action -> PlayerGUI.dispatchPlayerRegion(player))
                .asGuiItem(player);
    }

    private GuiItem getTownButton(PlayerData playerData) {

        List<String> description = new ArrayList<>();
        if(playerData.hasTown()){
            description.add(Lang.GUI_TOWN_ICON_DESC1_HAVE_TOWN.get(playerData, playerData.getTown().getColoredName()));
            description.add(Lang.GUI_TOWN_ICON_DESC2_HAVE_TOWN.get(playerData, playerData.getTown().getRank(playerData).getColoredName()));
        }
        else {
            description.add(Lang.GUI_TOWN_ICON_DESC1_NO_TOWN.get(playerData));
        }
        description.add(Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData));

        return iconManager.get(IconKey.TOWN_BASE_ICON)
                .setName(Lang.GUI_TOWN_ICON.get(playerData))
                .setDescription(description)
                .setAction(action -> PlayerGUI.dispatchPlayerTown(player))
                .asGuiItem(player);
    }

    private GuiItem getPlayerButton(PlayerData playerData) {
        return iconManager.get(IconKey.PLAYER_BASE_ICON)
                .setName(Lang.GUI_PLAYER_MENU_ICON.get(playerData, player.getName()))
                .setDescription(Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData))
                .setAction(action -> new PlayerMenu(player))
                .asGuiItem(player);
    }


}
