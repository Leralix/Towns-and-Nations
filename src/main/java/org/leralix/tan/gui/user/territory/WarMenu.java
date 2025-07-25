package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlannedAttackStorage;
import org.leralix.tan.utils.GuiUtil;

import java.util.ArrayList;
import java.util.List;

public class WarMenu extends IteratorGUI {

    private final TerritoryData territoryData;

    public WarMenu(Player player, TerritoryData territoryData){
        super(player, Lang.HEADER_WARS_MENU, 6);
        this.territoryData = territoryData;
        open();
    }

    @Override
    public void open() {

        GuiUtil.createIterator(gui, getWars(tanPlayer), page, player,
                p -> territoryData.openMainMenu(player),
                p -> nextPage(),
                p -> previousPage());

        gui.open(player);

    }


    private List<GuiItem> getWars(ITanPlayer tanPlayer) {
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for(PlannedAttack plannedAttack : PlannedAttackStorage.getWars()){
            ItemStack attackIcon = plannedAttack.getIcon(tanPlayer, territoryData);
            GuiItem attackButton = ItemBuilder.from(attackIcon).asGuiItem(event -> {
                event.setCancelled(true);
                if(event.isLeftClick()){
                    PlayerGUI.openSpecificPlannedAttackMenu(player, territoryData, plannedAttack);
                }
            });
            guiItems.add(attackButton);
        }
        return guiItems;
    }
}
