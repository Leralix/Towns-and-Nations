package org.leralix.tan.gui.user;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
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
                gui.setItem(2, 2, getNationButton(ITanPlayer));
            }
            gui.setItem(2, 4, getRegionButton(ITanPlayer));
        }

        gui.setItem(2, 6, getTownButton(ITanPlayer));
        gui.setItem(2, 8, getPlayerButton(ITanPlayer));

        gui.setItem(3,1, GuiUtil.createBackArrow(player, HumanEntity::closeInventory));

        gui.open(player);
    }

    private @NotNull GuiItem getTimeIcon() {

        TimeZoneManager timeManager = TimeZoneManager.getInstance();

        IconKey icon = timeManager.isDayForServer() ?
                IconKey.TIMEZONE_ICON_DAY :
                IconKey.TIMEZONE_ICON_NIGHT;


        return iconManager.get(icon)
                .setName(Lang.GUI_SERVER_TIME.get(ITanPlayer))
                .setDescription(
                        Lang.CURRENT_SERVER_TIME.get(ITanPlayer, timeManager.formatDateNowForServer()),
                        Lang.CURRENT_PLAYER_TIME.get(ITanPlayer, timeManager.formatDateNowForPlayer(ITanPlayer))
                        )
                .asGuiItem(player);
    }

    private GuiItem getNationButton(ITanPlayer ITanPlayer) {
        return iconManager.get(IconKey.NATION_BASE_ICON)
                .setName(Lang.GUI_KINGDOM_ICON.get(ITanPlayer))
                .setDescription(Lang.GUI_WARNING_STILL_IN_DEV.get(ITanPlayer))
                .setAction(action -> {
                    player.sendMessage(Lang.GUI_WARNING_STILL_IN_DEV.get(ITanPlayer));
                    SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                })
                .asGuiItem(player);
    }

    private GuiItem getRegionButton(ITanPlayer ITanPlayer) {

        List<String> description = new ArrayList<>();

        if(ITanPlayer.hasRegion()){
            RegionData regionData = ITanPlayer.getRegion();
            description.add(Lang.GUI_REGION_ICON_DESC1_REGION.get(ITanPlayer, regionData.getColoredName()));
            description.add(Lang.GUI_REGION_ICON_DESC2_REGION.get(ITanPlayer, regionData.getRank(ITanPlayer).getColoredName()));
        }
        else {
            description.add(Lang.GUI_REGION_ICON_DESC1_NO_REGION.get(ITanPlayer));
        }
        description.add(Lang.GUI_GENERIC_CLICK_TO_OPEN.get(ITanPlayer));


        return iconManager.get(IconKey.REGION_BASE_ICON)
                .setName(Lang.GUI_REGION_ICON.get(ITanPlayer))
                .setDescription(description)
                .setAction(action -> PlayerGUI.dispatchPlayerRegion(player))
                .asGuiItem(player);
    }

    private GuiItem getTownButton(ITanPlayer ITanPlayer) {

        List<String> description = new ArrayList<>();
        if(ITanPlayer.hasTown()){
            description.add(Lang.GUI_TOWN_ICON_DESC1_HAVE_TOWN.get(ITanPlayer, ITanPlayer.getTown().getColoredName()));
            description.add(Lang.GUI_TOWN_ICON_DESC2_HAVE_TOWN.get(ITanPlayer, ITanPlayer.getTown().getRank(ITanPlayer).getColoredName()));
        }
        else {
            description.add(Lang.GUI_TOWN_ICON_DESC1_NO_TOWN.get(ITanPlayer));
        }
        description.add(Lang.GUI_GENERIC_CLICK_TO_OPEN.get(ITanPlayer));

        return iconManager.get(IconKey.TOWN_BASE_ICON)
                .setName(Lang.GUI_TOWN_ICON.get(ITanPlayer))
                .setDescription(description)
                .setAction(action -> PlayerGUI.dispatchPlayerTown(player))
                .asGuiItem(player);
    }

    private GuiItem getPlayerButton(ITanPlayer ITanPlayer) {
        return iconManager.get(IconKey.PLAYER_BASE_ICON)
                .setName(Lang.GUI_PLAYER_MENU_ICON.get(ITanPlayer, player.getName()))
                .setDescription(Lang.GUI_GENERIC_CLICK_TO_OPEN.get(ITanPlayer))
                .setAction(action -> new PlayerMenu(player))
                .asGuiItem(player);
    }


}
