package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.AdminSetPlayerBalance;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.StringUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class AdminManagePlayer extends BasicGui {


    private final ITanPlayer targetPlayer;

    public AdminManagePlayer(Player player, ITanPlayer targetPlayer) {
        super(player, Lang.HEADER_ADMIN_PLAYER_MENU.get(targetPlayer.getNameStored()), 3);
        this.targetPlayer = targetPlayer;
        open();
    }

    @Override
    public void open() {
        gui.setItem(1, 5, getPlayerInfo());

        gui.setItem(2, 4, getBalanceButton());
        gui.setItem(2, 6, getTownButton());

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new AdminBrowsePlayers(player)));

        gui.open(player);
    }

    private @NotNull GuiItem getBalanceButton() {
        return iconManager.get(IconKey.PLAYER_BALANCE_ICON)
                .setName(Lang.GUI_YOUR_BALANCE.get(langType))
                .setDescription(
                        Lang.GUI_YOUR_BALANCE_DESC1.get(StringUtil.formatMoney(EconomyUtil.getBalance(targetPlayer.getOfflinePlayer())))
                )
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_MODIFY)
                .setAction(action -> {
                    player.closeInventory();
                    PlayerChatListenerStorage.register(player, new AdminSetPlayerBalance(targetPlayer));
                })
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getTownButton() {


        IconBuilder iconBuilder = iconManager.get(IconKey.TOWN_BASE_ICON);


        if(targetPlayer.hasTown()){
            TownData townData = targetPlayer.getTown();
            iconBuilder
                    .setName(Lang.ADMIN_GUI_TOWN_PLAYER_TOWN.get(langType, townData.getName()))
                    .setClickToAcceptMessage(
                            townData.isLeader(targetPlayer) ?
                                    Lang.ADMIN_GUI_TOWN_PLAYER_TOWN_DESC2 :
                                    Lang.ADMIN_GUI_TOWN_PLAYER_TOWN_DESC1
                    )
                    .setAction(action -> {
                            if (townData.isLeader(targetPlayer)) {
                                TanChatUtils.message(player, Lang.GUI_TOWN_MEMBER_CANT_KICK_LEADER.get(langType));
                                return;
                            }
                            townData.removePlayer(targetPlayer);

                            TanChatUtils.message(player, Lang.ADMIN_GUI_TOWN_PLAYER_LEAVE_TOWN_SUCCESS.get(langType, targetPlayer.getNameStored(), townData.getName()));
                            open();
                    });
        }
        else {
            iconBuilder
                    .setName(Lang.ADMIN_GUI_PLAYER_NO_TOWN.get(langType))
                    .setClickToAcceptMessage(Lang.GUI_GENERIC_ADD_BUTTON)
                    .setAction(action -> new AdminSetPlayerTown(player, targetPlayer));
        }
        return iconBuilder.asGuiItem(player, langType);
    }

    private @NotNull GuiItem getPlayerInfo() {

        OfflinePlayer offlinePlayer = targetPlayer.getOfflinePlayer();

        return iconManager.get(offlinePlayer)
                .setName(offlinePlayer.getName())
                .setDescription(Lang.GUI_YOUR_BALANCE_DESC1.get(StringUtil.formatMoney(EconomyUtil.getBalance(offlinePlayer))))
                .asGuiItem(player, langType);
    }
}
