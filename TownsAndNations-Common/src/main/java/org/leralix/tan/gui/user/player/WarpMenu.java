package org.leralix.tan.gui.user.player;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.scope.BrowseScope;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.TeleportationRegister;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WarpMenu extends IteratorGUI {

    private final Consumer<Player> guiCallback;

    public WarpMenu(Player player, Consumer<Player> returnMenu) {
        super(player, Lang.HEADER_WARP_LIST, 6);
        this.guiCallback = returnMenu;
        open();
    }

    @Override
    public void open() {
        iterator(getTerritoriesAuthorizingTeleportation(), guiCallback);

        gui.open(player);
    }

    private List<GuiItem> getTerritoriesAuthorizingTeleportation() {

        List<GuiItem> res = new ArrayList<>();

        List<TerritoryData> allTerritories = TerritoryUtil.getTerritories(BrowseScope.ALL);

        for (TerritoryData territoryData : tanPlayer.getAllTerritoriesPlayerIsIn()) {
            for (TerritoryData iterateTerritoryData : allTerritories) {
                if (iterateTerritoryData.authorizeTeleportation(territoryData)) {
                    res.add(iterateTerritoryData.getIconWithInformations(langType)
                            .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                            .setAction(action ->
                                    TeleportationRegister.teleportToTownSpawn(tanPlayer, territoryData))
                            .asGuiItem(player, langType));
                }
            }
        }

        return res;
    }
}
