package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public class PlayerOwnedLandmarksMenu extends IteratorGUI {

    private final Town townData;

    public PlayerOwnedLandmarksMenu(Player player, Town townData){
        super(player, Lang.HEADER_TOWN_OWNED_LANDMARK, 6);
        this.townData = townData;
        open();
    }

    @Override
    public void open() {

        iterator(getLandmarks(), p -> townData.openMainMenu(player, tanPlayer));

        gui.open(player);
    }

    private List<GuiItem> getLandmarks() {
        ArrayList<GuiItem> res = new ArrayList<>();
        for (Landmark landmark : TownsAndNations.getPlugin().getLandmarkStorage().getLandmarkOf(townData)) {
            GuiItem landmarkButton = landmark.getIcon(tanPlayer.getLang()).asGuiItem(player, langType);
            res.add(landmarkButton);
        }
        return res;
    }
}
