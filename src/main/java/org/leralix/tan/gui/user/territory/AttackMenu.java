package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.user.war.PlannedAttackMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.war.PlannedAttack;

import java.util.ArrayList;
import java.util.List;

public class AttackMenu extends IteratorGUI {

    private final TerritoryData territoryData;

    public AttackMenu(Player player, TerritoryData territoryData) {
        super(player, Lang.HEADER_WARS_MENU, 6);
        this.territoryData = territoryData;
        open();
    }

    @Override
    public void open() {

        iterator(getWars(tanPlayer), p -> new WarsMenu(player, territoryData));

        gui.open(player);
    }


    private List<GuiItem> getWars(ITanPlayer tanPlayer) {
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (PlannedAttack plannedAttack : WarStorage.getInstance().getAllAttacks()) {

            guiItems.add(plannedAttack.getIcon(iconManager, tanPlayer.getLang(), tanPlayer.getTimeZone())
                    .setAction(action ->
                            new PlannedAttackMenu(player, territoryData, plannedAttack))
                    .asGuiItem(player, langType)

            );
        }
        return guiItems;
    }
}
