package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.TownDataStorage;

import java.util.ArrayList;
import java.util.List;

public class AdminSetPlayerTown extends IteratorGUI {

    private final ITanPlayer targetPlayer;

    public AdminSetPlayerTown(Player player, ITanPlayer tanPlayer) {
        super(player, Lang.HEADER_ADMIN_SET_PLAYER_TOWN, 6);
        this.targetPlayer = tanPlayer;
        open();
    }

    @Override
    public void open() {
        iterator(getTowns(), p -> new AdminManagePlayer(player, tanPlayer));
        gui.open(player);
    }

    private List<GuiItem> getTowns() {
        ArrayList<GuiItem> guiItems = new ArrayList<>();


        for (TownData townData : TownDataStorage.getInstance().getAll().values()) {
            IconBuilder townIcon = townData.getIconWithInformations(targetPlayer.getLang());
            townIcon.setClickToAcceptMessage(Lang.ADMIN_GUI_LEFT_CLICK_TO_MANAGE_TOWN);
            townIcon.setAction(action -> {
                townData.addPlayer(targetPlayer);
                new AdminManagePlayer(player, targetPlayer);
            });
            guiItems.add(townIcon.asGuiItem(player, langType));
        }
        return guiItems;
    }
}
