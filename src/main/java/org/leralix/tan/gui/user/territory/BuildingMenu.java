package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.building.Building;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.interact.RightClickListener;
import org.leralix.tan.listeners.interact.events.CreatefortEvent;
import org.leralix.tan.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.tan.utils.TanChatUtils.getTANString;

public class BuildingMenu extends IteratorGUI {

    private final BasicGui previousMenu;
    private final TerritoryData territoryData;

    public BuildingMenu(Player player, TerritoryData territoryData, BasicGui previousMenu) {
        super(player, Lang.HEADER_BUILDING_MENU, 4);
        this.territoryData = territoryData;
        this.previousMenu = previousMenu;
        open();
    }


    @Override
    public void open() {
        iterator(getBuildings(), p -> previousMenu.open());

        gui.setItem(4, 6, getCreateFortButton());
        gui.open(player);
    }

    private @NotNull GuiItem getCreateFortButton() {
        return iconManager.get(IconKey.FORT_BUILDING_ICON)
                .setName(Lang.CREATE_FORT_ICON.get(langType))
                .setDescription(
                        Lang.CREATE_FORT_DESC1.get(langType, Constants.getFortCost()),
                        Lang.GUI_GENERIC_CLICK_TO_PROCEED.get(langType)
                )
                .setAction(action -> {
                    player.sendMessage(getTANString() + Lang.RIGHT_CLICK_TO_PLACE_FORT.get(langType));
                    RightClickListener.register(player, new CreatefortEvent(territoryData));
                    player.closeInventory();
                })
                .asGuiItem(player);
    }

    private List<GuiItem> getBuildings() {
        List<GuiItem> res = new ArrayList<>();
        for(Building building : territoryData.getBuildings()){
            res.add(building.getGuiItem(iconManager, player, territoryData, this));
        }
        return res;
    }
}
