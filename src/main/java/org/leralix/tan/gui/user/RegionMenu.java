package org.leralix.tan.gui.user;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.GuiUtil;

public class RegionMenu extends TerritoryMenu {


    public RegionMenu(Player player){
        super(player, Lang.HEADER_REGION_MENU.get(player, PlayerDataStorage.getInstance().get(player).getTown().getName()));
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

        gui.setItem(3, 2, getAttackButton());
        gui.setItem(3, 3, getHierarchyButton());

        gui.setItem(4, 1, GuiUtil.createBackArrow(player, p -> new MainMenu(p).open()));

        gui.open(player);
    }

    private GuiItem getSettingsButton() {
        return IconManager.getInstance().get(IconKey.TERRITORY_SETTINGS_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_ICON.get(playerData.getLang()))
                .setDescription(
                        Lang.GUI_TOWN_SETTINGS_ICON_DESC1.get(playerData.getLang()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData)
                )
                .setAction(event -> PlayerGUI.openRegionSettings(player))
                .asGuiItem(player);
    }


}
