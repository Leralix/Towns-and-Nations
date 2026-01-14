package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.NationData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.gui.service.requirements.RankPermissionRequirement;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TerritoryMemberMenu extends IteratorGUI {

    private final TerritoryData territoryData;

    public TerritoryMemberMenu(Player player, TerritoryData territoryData) {
        super(player, getTitleFor(territoryData), 6);
        this.territoryData = territoryData;
    }

    private static Lang getTitleFor(TerritoryData territoryData) {
        if (territoryData instanceof TownData) {
            return Lang.HEADER_TOWN_MEMBERS;
        }
        if (territoryData instanceof RegionData) {
            return Lang.HEADER_REGION_MEMBERS;
        }
        if (territoryData instanceof NationData) {
            return Lang.HEADER_NATION_MEMBERS;
        }
        return Lang.HEADER_TOWN_MEMBERS;
    }

    @Override
    public void open() {

        iterator(getMemberList(), p -> territoryData.openMainMenu(player));

        gui.setItem(6, 4, getManageRankButton());
        if (territoryData instanceof TownData townData) {
            gui.setItem(6, 5, getManageApplicationsButton(townData));
        }
        gui.open(player);
    }

    private List<GuiItem> getMemberList() {

        List<GuiItem> players = new ArrayList<>();
        PlayerDataStorage playerDataStorage = PlayerDataStorage.getInstance();

        for (String playerUUID : territoryData.getOrderedPlayerIDList()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));

            ITanPlayer playerToIterate = playerDataStorage.get(offlinePlayer);

            IconBuilder iconBuilder = iconManager.get(offlinePlayer)
                    .setName(offlinePlayer.getName())
                    .setDescription(
                            Lang.GUI_TOWN_MEMBER_DESC1.get(playerToIterate.getRank(territoryData).getColoredName()),
                            Lang.GUI_TOWN_MEMBER_DESC2.get(Double.toString(EconomyUtil.getBalance(playerToIterate)))
                    );

            if (territoryData instanceof TownData townData) {
                addKickPlayerOption(townData, iconBuilder, playerToIterate, offlinePlayer);
            }
            players.add(iconBuilder.asGuiItem(player, langType));
        }
        return players;
    }

    private void addKickPlayerOption(TownData townData, IconBuilder iconBuilder, ITanPlayer playerToIterate, OfflinePlayer offlinePlayer) {
        iconBuilder
                .setClickToAcceptMessage(
                        Lang.GUI_TOWN_MEMBER_DESC3
                )
                .setAction(event -> {
                            if (event.getClick() != ClickType.RIGHT) {
                                return;
                            }

                            if (!territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.KICK_PLAYER)) {
                                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(langType));
                                return;
                            }
                            if (territoryData.getRank(playerToIterate).isSuperiorTo(townData.getRank(tanPlayer))) {
                                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get(langType));
                                return;
                            }
                            if (territoryData.isLeader(playerToIterate)) {
                                TanChatUtils.message(player, Lang.GUI_TOWN_MEMBER_CANT_KICK_LEADER.get(langType));
                                return;
                            }
                            if (playerToIterate.getID().equals(tanPlayer.getID())) {
                                TanChatUtils.message(player, Lang.GUI_TOWN_MEMBER_CANT_KICK_YOURSELF.get(langType));
                                return;
                            }

                            new ConfirmMenu(
                                    player,
                                    Lang.CONFIRM_PLAYER_KICKED.get(offlinePlayer.getName()),
                                    () -> {
                                        townData.kickPlayer(offlinePlayer);
                                        open();
                                    },
                                    this::open
                            );
                        }
                );
    }

    private GuiItem getManageRankButton() {
        return IconManager.getInstance().get(IconKey.MANAGE_RANKS_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_MANAGE_ROLES.get(langType))
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.MANAGE_RANKS))
                .setAction(p -> new TerritoryRanksMenu(player, territoryData).open())
                .asGuiItem(player, langType);
    }

    private GuiItem getManageApplicationsButton(TownData townData) {
        return IconManager.getInstance().get(IconKey.MANAGE_APPLICATIONS_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_MANAGE_APPLICATION.get(langType))
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.INVITE_PLAYER))
                .setDescription(Lang.GUI_TOWN_MEMBERS_MANAGE_APPLICATION_DESC1.get(Integer.toString(townData.getPlayerJoinRequestSet().size())))
                .setAction(p -> new PlayerApplicationMenu(player, townData).open())
                .asGuiItem(player, langType);
    }
}
