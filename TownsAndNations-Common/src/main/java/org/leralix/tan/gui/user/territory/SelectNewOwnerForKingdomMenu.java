package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.KingdomData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.leralix.lib.data.SoundEnum.GOOD;

public class SelectNewOwnerForKingdomMenu extends IteratorGUI {

    private final KingdomData kingdomData;
    private final Runnable backMenu;

    public SelectNewOwnerForKingdomMenu(Player player, KingdomData kingdomData, Runnable backMenu) {
        super(player, Lang.HEADER_CHANGE_OWNERSHIP, 3);
        this.kingdomData = kingdomData;
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

        for (String playerUUID : kingdomData.getPlayerIDList()) {
            if (kingdomData.isLeader(playerUUID)) {
                continue;
            }

            OfflinePlayer kingdomPlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerUUID));

            guiItems.add(
                    iconManager.get(kingdomPlayer)
                            .setName(kingdomPlayer.getName())
                            .setDescription(
                                    Lang.GUI_KINGDOM_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC1.get(kingdomPlayer.getName()),
                                    Lang.GUI_KINGDOM_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC2.get()
                            )
                            .setAction(action -> {
                                action.setCancelled(true);

                                new ConfirmMenu(
                                        player,
                                        Lang.GUI_CONFIRM_CHANGE_KINGDOM_LEADER.get(kingdomPlayer.getName()),
                                        () -> {
                                            kingdomData.setLeaderID(kingdomPlayer.getUniqueId().toString());
                                            kingdomData.broadcastMessageWithSound(Lang.GUI_KINGDOM_SETTINGS_KINGDOM_CHANGE_LEADER_BROADCAST.get(kingdomPlayer.getName()), GOOD);
                                            TanChatUtils.message(player, Lang.GUI_KINGDOM_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_SUCCESS.get(tanPlayer, kingdomPlayer.getName()));

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
