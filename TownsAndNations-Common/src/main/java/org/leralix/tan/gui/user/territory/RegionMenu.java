package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.lang.Lang;

public class RegionMenu extends TerritoryMenu {

    private final Region regionData;

    public RegionMenu(Player player, Region regionData){
        super(player, Lang.HEADER_REGION_MENU.get(regionData.getName()), regionData);
        this.regionData = regionData;
        open();
    }

    @Override
    public void open(){
        setupCommonLayout(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        gui.open(player);
    }

    @Override
    protected GuiItem getSettingsButton() {
        return createSettingsButton(Lang.GUI_REGION_SETTINGS_ICON_DESC1.get(), p -> new RegionSettingsMenu(player, regionData));
    }

}
