package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class TownMenu extends TerritoryMenu {

    private final TownData townData;

    public TownMenu(Player player, TownData townData) {
        super(player, Lang.HEADER_TOWN_MENU.get(PlayerDataStorage.getInstance().get(player).getTown().getName()), townData);
        this.townData = townData;
        open();
    }

    @Override
    public void open() {
        setupCommonLayout(Material.BLUE_STAINED_GLASS_PANE);
        setRow2Column4(getLandButton());
        setRow3Column8(getLandmarksButton());
        gui.setItem(2, 8, getSettingsButton());
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
