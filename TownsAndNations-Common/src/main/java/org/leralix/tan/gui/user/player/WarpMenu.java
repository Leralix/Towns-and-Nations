package org.leralix.tan.gui.user.player;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.TeleportationRegister;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class WarpMenu extends IteratorGUI {

    private final Consumer<Player> guiCallback;

    public WarpMenu(Player player, Consumer<Player> returnMenu) {
        super(player, Lang.HEADER_WARP_LIST, 3);
        this.guiCallback = returnMenu;
        open();
    }

    @Override
    public void open() {
        iterator(getTerritoriesAuthorizingTeleportationButton(), guiCallback);

        gui.open(player);
    }

    private List<GuiItem> getTerritoriesAuthorizingTeleportationButton() {

        List<GuiItem> res = new ArrayList<>();

        Set<TerritoryData> territories = TerritoryUtil.getTerritoriesAuthorizingTeleportation(tanPlayer);

        for (TerritoryData territoryData : territories) {
            res.add(iconManager.get(territoryData.getIcon())
                    .setName(territoryData.getColoredName())
                    .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                    .setAction(action -> {
                        TeleportationRegister.teleportToTownSpawn(tanPlayer, territoryData);
                        player.closeInventory();
                    })
                    .asGuiItem(player, langType));
        }

        return res;
    }
}
