package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

import java.util.ArrayList;
import java.util.List;

public class AdminSelectNewOverlord extends IteratorGUI {
    private final Territory territory;
    private final BasicGui returnGui;

    public AdminSelectNewOverlord(Player player, Territory townData, BasicGui returnGui) {
        super(player, Lang.HEADER_ADMIN_CHANGE_OVERLORD.get(townData.getName()), 6);
        this.territory = townData;
        this.returnGui = returnGui;
        open();
    }

    @Override
    public void open() {
        iterator(getPotentialOverlords(), p -> returnGui.open());

        gui.open(player);
    }

    private List<GuiItem> getPotentialOverlords() {
        List<GuiItem> guiItems = new ArrayList<>();

        for(Territory potentialOverlord : TerritoryUtil.getAllPotentialOverlords(territory)){

            IconBuilder potentialOverlordIcon = potentialOverlord.getIconWithInformations(tanPlayer.getLang());
            potentialOverlordIcon.addDescription(Lang.GUI_GENERIC_CLICK_TO_SELECT.get());

            potentialOverlordIcon.setAction(action -> {
                territory.setOverlord(potentialOverlord);
                returnGui.open();
            });
            guiItems.add(potentialOverlordIcon.asGuiItem(player, langType));
        }
        return guiItems;
    }
}
