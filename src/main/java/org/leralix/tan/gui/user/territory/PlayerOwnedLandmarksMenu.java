package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.LandmarkStorage;

import java.util.ArrayList;
import java.util.List;

public class PlayerOwnedLandmarksMenu extends IteratorGUI {

    private final TownData townData;

    public PlayerOwnedLandmarksMenu(Player player, TownData townData){
        super(player, Lang.HEADER_TOWN_OWNED_LANDMARK, 6);
        this.townData = townData;
        open();
    }

    @Override
    public void open() {

        iterator(getLandmarks(), p -> townData.openMainMenu(player));

        gui.open(player);
    }

    private List<GuiItem> getLandmarks() {
        ArrayList<GuiItem> res = new ArrayList<>();
        for (Landmark landmark : LandmarkStorage.getInstance().getLandmarkOf(townData)) {
            GuiItem landmarkButton = ItemBuilder.from(landmark.getIcon(tanPlayer.getLang())).asGuiItem(event -> event.setCancelled(true));
            res.add(landmarkButton);
        }
        return res;
    }
}
