package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SelectNewOwnerForTownMenu extends IteratorGUI {

    private final TownData townData;
    private final Runnable backMenu;


    public SelectNewOwnerForTownMenu(Player player, TownData townData, Runnable backMenu) {
        super(player, Lang.HEADER_CHANGE_OWNERSHIP, 3);
        this.townData = townData;
        this.backMenu = backMenu;
        open();
    }

    @Override
    public void open() {
        iterator(getCandidates(), p -> backMenu.run());
        gui.open(player);
    }

    private List<GuiItem> getCandidates() {
        List<GuiItem> guiItems = new ArrayList<>();
        for (UUID playerUUID : townData.getPlayerIDList()) {
            if (townData.isLeader(playerUUID)) {
                continue;
            }

            OfflinePlayer townPlayer = Bukkit.getServer().getOfflinePlayer(playerUUID);

            guiItems.add(
                    iconManager.get(townPlayer)
                            .setName(townPlayer.getName())
                            .setDescription(
                                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC1.get(player.getName()),
                                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC2.get()
                            )
                            .setAction(action -> {
                                action.setCancelled(true);

                                new ConfirmMenu(
                                        player,
                                        Lang.GUI_CONFIRM_CHANGE_TOWN_LEADER.get(townPlayer.getName()),
                                        () -> {
                                            townData.setLeaderID(townPlayer.getUniqueId());
                                            TanChatUtils.message(player, Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_SUCCESS.get(tanPlayer, townPlayer.getName()));

                                            backMenu.run();
                                        },
                                        this::open
                                );
                            })
                            .asGuiItem(player, langType)
            );
        }
        return guiItems;

    }
}
