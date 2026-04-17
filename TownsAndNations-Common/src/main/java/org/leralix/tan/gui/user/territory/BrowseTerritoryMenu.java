package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.scope.BrowseScope;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BrowseTerritoryMenu extends IteratorGUI {

    private final Territory territoryData;
    private BrowseScope scope;
    private final Consumer<Player> exitMenu;

    public BrowseTerritoryMenu(Player player, Territory territoryData, BrowseScope scope, Consumer<Player> exitMenu){
        super(player, Lang.HEADER_TERRITORY_LIST, 6);
        this.territoryData = territoryData;
        this.scope = scope;
        this.exitMenu = exitMenu;
        open();
    }

    public void setScope(BrowseScope newScope){
        this.scope = newScope;
        open();
    }

    @Override
    public void open() {

        iterator(getTerritory(), exitMenu);

        gui.setItem(6, 5, getScopeButton());

        gui.open(player);
    }

    private GuiItem getScopeButton() {
        return GuiUtil.getNextScopeButton(
                iconManager,
                this,
                scope,
                newValue -> scope = newValue,
                langType,
                player
        );
    }

    private List<GuiItem> getTerritory() {
        List<Territory> territoryList = TerritoryUtil.getTerritories(scope);

        ArrayList<GuiItem> townGuiItems = new ArrayList<>();

        for(Territory specificTerritory : territoryList){

            townGuiItems.add(specificTerritory
                    .getIconWithInformationAndRelation(territoryData, tanPlayer.getLang())
                    .asGuiItem(player, langType)
            );
        }
        return townGuiItems;
    }
}
