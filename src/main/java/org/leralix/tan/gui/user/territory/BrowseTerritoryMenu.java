package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.BrowseScope;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
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
    private final  Consumer<Player> exitMenu;

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
        return iconManager.get(IconKey.CHANGE_SCOPE_ICON)
                .setName(Lang.BROWSE_SCOPE.get(tanPlayer, scope.getName(tanPlayer)))
                .setDescription(Lang.GUI_GENERIC_CLICK_TO_SWITCH.get(tanPlayer))
                .setAction(p -> setScope(scope.getNextScope()))
                .asGuiItem(player);
    }

    private List<GuiItem> getTerritory() {
        List<TerritoryData> territoryList = new ArrayList<>();

        if(scope == BrowseScope.ALL || scope == BrowseScope.TOWNS)
            territoryList.addAll(TownDataStorage.getInstance().getAll().values());
        if(scope == BrowseScope.ALL || scope == BrowseScope.REGIONS)
            territoryList.addAll(RegionDataStorage.getInstance().getAll().values());

        ArrayList<GuiItem> townGuiItems = new ArrayList<>();

        for(TerritoryData specificTerritoryData : territoryList){
            ItemStack territoryIcon = specificTerritoryData.getIconWithInformationAndRelation(territoryData, tanPlayer.getLang());
            GuiItem territoryGUI = ItemBuilder.from(territoryIcon).asGuiItem();

            townGuiItems.add(territoryGUI);
        }
        return townGuiItems;
    }
}
