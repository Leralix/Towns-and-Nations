package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.scope.BrowseScope;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BrowseTerritoryMenu extends IteratorGUI {

    private final TerritoryData territoryData;
    private BrowseScope scope;
    private final Consumer<Player> exitMenu;

    public BrowseTerritoryMenu(Player player, TerritoryData territoryData, BrowseScope scope, Consumer<Player> exitMenu){
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

        GuiUtil.createIterator(gui, getTerritory(), page, player,
                exitMenu,
                p -> nextPage(),
                p -> previousPage());

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
        List<TerritoryData> territoryList = new ArrayList<>();

        if(scope == BrowseScope.ALL || scope == BrowseScope.TOWNS)
            territoryList.addAll(TownDataStorage.getInstance().getAll().values());
        if(scope == BrowseScope.ALL || scope == BrowseScope.REGIONS)
            territoryList.addAll(RegionDataStorage.getInstance().getAll().values());
        if(scope == BrowseScope.ALL || scope == BrowseScope.NATIONS && org.leralix.tan.utils.constants.Constants.enableNation())
            territoryList.addAll(org.leralix.tan.storage.stored.NationDataStorage.getInstance().getAll().values());

        ArrayList<GuiItem> townGuiItems = new ArrayList<>();

        for(TerritoryData specificTerritoryData : territoryList){

            townGuiItems.add(specificTerritoryData
                    .getIconWithInformationAndRelation(territoryData, tanPlayer.getLang())
                    .asGuiItem(player, langType)
            );
        }
        return townGuiItems;
    }
}
