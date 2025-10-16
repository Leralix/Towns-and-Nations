package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;

public class RegionMenu extends TerritoryMenu {

    private final RegionData regionData;

    public RegionMenu(Player player, RegionData regionData){
        super(player, Lang.HEADER_REGION_MENU.get(player, PlayerDataStorage.getInstance().get(player).getRegion().getName()), regionData);
        this.regionData = regionData;
        open();
    }

    @Override
    public void open(){
        gui.setItem(1, 5, getTerritoryInfo());
        gui.getFiller().fillTop(GuiUtil.getUnnamedItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE));

        gui.setItem(2, 2, getTownTreasuryButton());
        gui.setItem(2, 3, getMemberButton());
        gui.setItem(2, 4, getLandButton());
        gui.setItem(2, 5, getBrowseButton());
        gui.setItem(2, 6, getDiplomacyButton());

        gui.setItem(2, 8, getSettingsButton());

        gui.setItem(3, 2, getBuildingButton());
        gui.setItem(3, 3, getAttackButton());
        gui.setItem(3, 4, getHierarchyButton());

        gui.setItem(4, 1, GuiUtil.createBackArrow(player, MainMenu::new));

        gui.open(player);
    }

    private GuiItem getSettingsButton() {
        return IconManager.getInstance().get(IconKey.TERRITORY_SETTINGS_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_ICON.get(tanPlayer.getLang()))
                .setDescription(Lang.GUI_TOWN_SETTINGS_ICON_DESC1.get(tanPlayer.getLang()))
                .setAction(event -> new RegionSettingsMenu(player, regionData))
                .asGuiItem(player);
    }


}
