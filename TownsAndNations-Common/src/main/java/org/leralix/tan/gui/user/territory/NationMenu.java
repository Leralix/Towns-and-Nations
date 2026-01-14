package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.NationData;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;

public class NationMenu extends TerritoryMenu {

    private final NationData nationData;

    public NationMenu(Player player, NationData nationData) {
        super(player, Lang.HEADER_NATION_MENU.get(nationData.getName()), nationData);
        this.nationData = nationData;
        open();
    }

    @Override
    public void open() {
        setupCommonLayout(Material.PURPLE_STAINED_GLASS_PANE);
        setRow2Column4(getRankButton());
        gui.setItem(2, 8, getSettingsButton());
        gui.open(player);
    }

    private GuiItem getSettingsButton() {
        return IconManager.getInstance().get(IconKey.TERRITORY_SETTINGS_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_ICON.get(tanPlayer.getLang()))
                .setDescription(Lang.GUI_TOWN_SETTINGS_ICON_DESC1.get())
                .setAction(event -> new NationSettingsMenu(player, nationData))
                .asGuiItem(player, langType);
    }

    private GuiItem getRankButton() {
        return IconManager.getInstance().get(IconKey.MANAGE_RANKS_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_ADD_NEW_ROLES.get(tanPlayer))
                .setDescription(Lang.GUI_TOWN_MEMBERS_ADD_NEW_ROLES_DESC1.get(Integer.toString(nationData.getNumberOfRank())))
                .setAction(event -> new TerritoryRanksMenu(player, nationData).open())
                .asGuiItem(player, langType);
    }
}
