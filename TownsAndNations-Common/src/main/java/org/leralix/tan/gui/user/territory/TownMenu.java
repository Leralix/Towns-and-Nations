package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;

public class TownMenu extends TerritoryMenu {

    private final TownData townData;

    public TownMenu(Player player, ITanPlayer tanPlayer, TownData townData) {
        super(player, Lang.HEADER_TOWN_MENU.get(tanPlayer.getTown().getName()), townData);
        this.townData = townData;
        open();
    }

    @Override
    public void open() {
        setupCommonLayout(Material.BLUE_STAINED_GLASS_PANE);
        gui.setItem(2, 4, getLandButton());
        gui.setItem(2, 8, getSettingsButton());
        gui.setItem(3, 8, getLandmarksButton());
        gui.open(player);
    }

    private GuiItem getSettingsButton() {
        return createSettingsButton(Lang.GUI_TOWN_SETTINGS_ICON_DESC1.get(), p -> new TownSettingsMenu(player, townData));
    }

    private GuiItem getLandmarksButton() {
        return IconManager.getInstance().get(IconKey.TOWN_LANDMARKS_ICON)
                .setName(Lang.ADMIN_GUI_LANDMARK_ICON.get(tanPlayer.getLang()))
                .setDescription(Lang.ADMIN_GUI_LANDMARK_DESC1.get())
                .setAction(event -> new PlayerOwnedLandmarksMenu(player, townData))
                .asGuiItem(player, langType);
    }
}
