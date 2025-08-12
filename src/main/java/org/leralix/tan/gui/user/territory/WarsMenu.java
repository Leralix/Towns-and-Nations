package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.user.war.WarMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.war.War;
import org.leralix.tan.war.WarStorage;

import java.util.ArrayList;
import java.util.List;

public class WarsMenu extends IteratorGUI {

    private final TerritoryData territoryData;

    public WarsMenu(Player player, TerritoryData territoryData) {
        super(player, "War Menu", 4);
        this.territoryData = territoryData;
        open();
    }

    @Override
    public void open() {
        iterator(getWars(territoryData), p -> territoryData.openMainMenu(player));
        gui.open(player);
    }

    public List<GuiItem> getWars(TerritoryData territoryData) {

        List<War> wars = WarStorage.getInstance().getWarsOfTerritory(territoryData);


        List<GuiItem> guiItems = new ArrayList<>();
        for(War war : wars) {
            guiItems.add(iconManager.get(war.getIcon())
                    .setName(war.getName())
                     .setDescription(
                             Lang.ATTACK_ICON_DESC_1.get(langType, war.getMainAttacker().getColoredName()),
                             Lang.ATTACK_ICON_DESC_2.get(langType, war.getMainDefender().getColoredName()),
                             Lang.GUI_GENERIC_CLICK_TO_OPEN.get(langType)
                     )
                    .setAction(event -> new WarMenu(player, territoryData, war))
                    .asGuiItem(player));
        }

        gui.setItem(4, 4, getAttackButton(territoryData));
        return guiItems;
    }

    private @NotNull GuiItem getAttackButton(TerritoryData territoryData) {
        return iconManager.get(new ItemStack(Material.BOW))
                .setName("Open Attacks")
                .setAction(p -> new AttackMenu(player, territoryData))
                .asGuiItem(player);
    }

}

