package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.NationData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.leralix.lib.data.SoundEnum.GOOD;

public class SelectNewOwnerForNationMenu extends IteratorGUI {

    private final NationData nationData;
    private final Runnable backMenu;

    public SelectNewOwnerForNationMenu(Player player, NationData nationData, Runnable backMenu) {
        super(player, Lang.HEADER_CHANGE_OWNERSHIP, 3);
        this.nationData = nationData;
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

        for (String playerUUID : nationData.getPlayerIDList()) {
            if (nationData.isLeader(playerUUID)) {
                continue;
            }

            OfflinePlayer nationPlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerUUID));

            guiItems.add(
                    iconManager.get(nationPlayer)
                            .setName(nationPlayer.getName())
                            .setDescription(
                                    Lang.GUI_NATION_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC1.get(nationPlayer.getName()),
                                    Lang.GUI_NATION_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC2.get()
                            )
                            .setAction(action -> {
                                action.setCancelled(true);

                                new ConfirmMenu(
                                        player,
                                        Lang.GUI_CONFIRM_CHANGE_NATION_LEADER.get(nationPlayer.getName()),
                                        () -> {
                                            nationData.setLeaderID(nationPlayer.getUniqueId().toString());
                                            nationData.broadcastMessageWithSound(Lang.GUI_NATION_SETTINGS_NATION_CHANGE_LEADER_BROADCAST.get(nationPlayer.getName()), GOOD);
                                            TanChatUtils.message(player, Lang.GUI_NATION_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_SUCCESS.get(tanPlayer, nationPlayer.getName()));

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
