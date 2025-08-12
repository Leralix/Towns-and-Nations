package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.user.war.PlannedAttackMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlannedAttackStorage;
import org.leralix.tan.war.PlannedAttack;

import java.util.ArrayList;
import java.util.List;

public class AttackMenu extends IteratorGUI {

    private final TerritoryData territoryData;

    public AttackMenu(Player player, TerritoryData territoryData){
        super(player, Lang.HEADER_WARS_MENU, 6);
        this.territoryData = territoryData;
        open();
    }

    @Override
    public void open() {

        iterator(getWars(tanPlayer), territoryData::openMainMenu);

        gui.open(player);
    }


    private List<GuiItem> getWars(ITanPlayer tanPlayer) {
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for(PlannedAttack plannedAttack : PlannedAttackStorage.getInstance().getAll().values()){
            ItemStack attackIcon = plannedAttack.getIcon(tanPlayer, territoryData);
            GuiItem attackButton = ItemBuilder.from(attackIcon).asGuiItem(event -> {
                event.setCancelled(true);
                new PlannedAttackMenu(player, territoryData, plannedAttack);
            });
            guiItems.add(attackButton);
        }
        return guiItems;
    }
}
