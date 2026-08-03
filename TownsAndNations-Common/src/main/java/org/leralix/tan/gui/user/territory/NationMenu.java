package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.Nation;
import org.leralix.tan.lang.Lang;

public class NationMenu extends TerritoryMenu {

    private final Nation nationData;

    public NationMenu(Player player, Nation nationData) {
        super(player, Lang.HEADER_NATION_MENU.get(nationData.getName()), nationData);
        this.nationData = nationData;
        open();
    }

    @Override
    public void open() {
        setupCommonLayout(Material.ORANGE_STAINED_GLASS_PANE);
        gui.open(player);
    }

    @Override
    protected GuiItem getSettingsButton() {
        return createSettingsButton(Lang.GUI_NATION_SETTINGS_ICON_DESC1.get(), p -> new NationSettingsMenu(player, nationData, this));
    }
}
