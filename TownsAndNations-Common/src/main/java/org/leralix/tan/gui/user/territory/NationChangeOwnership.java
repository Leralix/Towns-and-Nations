package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.Nation;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.leralix.lib.data.SoundEnum.GOOD;

public class NationChangeOwnership extends IteratorGUI {

    private final Nation nationData;
    private final BasicGui returnGUI;

    public NationChangeOwnership(Player player, Nation nationData, BasicGui returnGUI) {
        super(player, Lang.HEADER_CHANGE_OWNERSHIP, 6);
        this.nationData = nationData;
        this.returnGUI = returnGUI;
        open();
    }

    @Override
    public void open() {
        iterator(getCandidates(), p -> returnGUI.open());
        gui.open(player);
    }

    private List<GuiItem> getCandidates() {
        List<GuiItem> guiItems = new ArrayList<>();
        for (UUID playerID : nationData.getPlayerIDList()) {
            if (nationData.isLeader(playerID)) {
                continue;
            }
            OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(playerID);
            guiItems.add(
                    iconManager.get(offlinePlayer)
                            .setName(offlinePlayer.getName())
                            .setDescription(
                                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC1.get(offlinePlayer.getName()),
                                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC2.get()
                            )
                            .setAction(action -> {
                                action.setCancelled(true);

                                new ConfirmMenu(
                                        player,
                                        Lang.GUI_CONFIRM_CHANGE_NATION_LEADER.get(offlinePlayer.getName()),
                                        () -> {
                                            nationData.setLeaderID(offlinePlayer.getUniqueId());
                                            nationData.broadcastMessageWithSound(Lang.GUI_NATION_SETTINGS_NATION_CHANGE_LEADER_BROADCAST.get(offlinePlayer.getName()), GOOD);
                                            TanChatUtils.message(player, Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_SUCCESS.get(tanPlayer, offlinePlayer.getName()));
                                            returnGUI.open();
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
