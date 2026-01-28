package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class PlayerApplicationMenu extends IteratorGUI {

    TownData townData;

    public PlayerApplicationMenu(Player player, TownData townData) {
        super(player, Lang.HEADER_TOWN_APPLICATIONS, 3);
        this.townData = townData;
    }

    @Override
    public void open() {

        GuiUtil.createIterator(gui, getApplicationList(), page, player,
                p -> new TerritoryMemberMenu(player, townData).open(),
                p -> nextPage(),
                p -> previousPage(),
                Material.LIME_STAINED_GLASS_PANE
        );
        gui.open(player);
    }

    private List<GuiItem> getApplicationList() {
        List<GuiItem> guiItems = new ArrayList<>();
        for (String playerUUID : new ArrayList<>(townData.getPlayerJoinRequestSet())) {

            UUID parsedUuid;
            try {
                parsedUuid = UUID.fromString(playerUUID);
            } catch (IllegalArgumentException e) {
                townData.removePlayerJoinRequest(playerUUID);
                continue;
            }

            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(parsedUuid);
            ITanPlayer playerIterateData = PlayerDataStorage.getInstance().getOrNull(playerUUID);
            guiItems.add(iconManager.get(playerIterate)
                    .setClickToAcceptMessage(
                            Lang.GUI_PLAYER_ASK_JOIN_PROFILE_DESC2,
                            Lang.GUI_PLAYER_ASK_JOIN_PROFILE_DESC3
                    )
                    .setAction(action -> {
                        if (action.isLeftClick()) {
                            if (!townData.doesPlayerHavePermission(tanPlayer, RolePermission.INVITE_PLAYER)) {
                                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
                                return;
                            }
                            if (townData.isFull()) {
                                TanChatUtils.message(player, Lang.INVITATION_TOWN_FULL.get(tanPlayer), NOT_ALLOWED);
                                return;
                            }
                            if (playerIterateData == null) {
                                townData.removePlayerJoinRequest(playerUUID);
                                open();
                                return;
                            }
                            townData.addPlayer(playerIterateData);
                        } else if (action.isRightClick()) {
                            if (!townData.doesPlayerHavePermission(tanPlayer, RolePermission.KICK_PLAYER)) {
                                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                                return;
                            }
                            if (playerIterateData == null) {
                                townData.removePlayerJoinRequest(playerUUID);
                            } else {
                                townData.removePlayerJoinRequest(playerIterateData.getID());
                            }
                        }
                        open();
                    })
                    .asGuiItem(player, langType));

        }
        return guiItems;
    }

}
