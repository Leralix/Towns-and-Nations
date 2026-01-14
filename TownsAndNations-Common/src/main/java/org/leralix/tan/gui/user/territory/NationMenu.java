package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.NationData;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.GuiUtil;

public class NationMenu extends TerritoryMenu {

    private final NationData nationData;

    public NationMenu(Player player, NationData nationData) {
        super(player, Lang.HEADER_NATION_MENU.get(nationData.getName()), nationData);
        this.nationData = nationData;
        open();
    }

    @Override
    public void open() {
        gui.setItem(1, 5, getTerritoryInfo());
        gui.getFiller().fillTop(GuiUtil.getUnnamedItem(Material.ORANGE_STAINED_GLASS_PANE));

        gui.setItem(2, 2, getTownTreasuryButton());
        gui.setItem(2, 3, getMemberButton());
        gui.setItem(2, 4, getLandButton());
        gui.setItem(2, 5, getBrowseButton());
        gui.setItem(2, 6, getDiplomacyButton());
        gui.setItem(2, 7, getLevelButton());
        gui.setItem(2, 8, getSettingsButton());

        gui.setItem(3, 2, getBuildingButton());
        gui.setItem(3, 3, getAttackButton());
        gui.setItem(3, 4, getHierarchyButton());

        gui.setItem(4, 1, GuiUtil.createBackArrow(player, MainMenu::new));

        gui.open(player);
    }

    private GuiItem getSettingsButton() {
        return createSettingsButton(Lang.GUI_NATION_SETTINGS_ICON_DESC1.get(), p -> new NationSettingsMenu(player, nationData));
    }
}
