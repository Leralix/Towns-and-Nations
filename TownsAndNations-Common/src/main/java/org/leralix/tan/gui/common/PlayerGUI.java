package org.leralix.tan.gui.common;

import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Nation;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.gui.landmark.LandmarkNoOwnerMenu;
import org.leralix.tan.gui.landmark.LandmarkOwnedMenu;
import org.leralix.tan.gui.user.territory.*;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.text.TanChatUtils;

import static org.leralix.lib.data.SoundEnum.MINOR_BAD;

public class PlayerGUI {

    private PlayerGUI() {
        throw new IllegalStateException("Utility class");
    }

    public static void dispatchPlayerNation(Player player, ITanPlayer playerData) {
        Nation nationData = playerData.getNation();
        if (nationData != null) {
            new NationMenu(player, nationData);
        } else {
            new NoNationMenu(player);
        }
    }

    public static void dispatchPlayerRegion(Player player, ITanPlayer playerData) {
        Region regionData = TownsAndNations.getPlugin().getRegionStorage().get(playerData);
        if (regionData != null) {
            new RegionMenu(player, regionData);
        } else {
            new NoRegionMenu(player);
        }
    }

    public static void dispatchPlayerTown(Player player, ITanPlayer playerData) {
        Town townData = playerData.getTown();
        if (townData != null) {
            new TownMenu(player, playerData, townData);
        } else {
            new NoTownMenu(player);
        }
    }

    public static void dispatchLandmarkGui(Player player, ITanPlayer playerData, Landmark landmark) {

        Town townData = playerData.getTown();
        if (!landmark.isOwned()) {
            new LandmarkNoOwnerMenu(player, landmark);
            return;
        }
        if (landmark.isOwnedBy(townData)) {
            new LandmarkOwnedMenu(player, townData, landmark);
            return;
        }
        Town owner = TownsAndNations.getPlugin().getTownStorage().get(landmark.getOwnerID());
        TanChatUtils.message(player, Lang.LANDMARK_ALREADY_CLAIMED.get(playerData, owner.getName()), MINOR_BAD);
    }

}
