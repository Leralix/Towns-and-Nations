package org.leralix.tan.gui.user;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.CreateTown;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.GuiUtil;
import org.leralix.tan.utils.TanChatUtils;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class NoTownMenu extends BasicGui {

    public NoTownMenu(Player player) {
        super(player, Lang.HEADER_NO_TOWN_MENU, 3);
        open();
    }

    @Override
    public void open() {

        gui.setItem(2, 3, getCreateTownButton());
        gui.setItem(2, 7, getBrowseTownsButton());
        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new MainMenu(player).open()));

        gui.open(player);
    }

    private GuiItem getCreateTownButton() {

        int townPrice = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("townCost", 1000);

        return IconManager.getInstance().get(IconKey.CREATE_TOWN_ICON)
                .setName(Lang.GUI_NO_TOWN_CREATE_NEW_TOWN.get(playerData))
                .setDescription(
                        Lang.GUI_NO_TOWN_CREATE_NEW_TOWN_DESC1.get(playerData, townPrice),
                        Lang.GUI_GENERIC_CLICK_TO_PROCEED.get(playerData))
                .setAction( action -> {
                    if(!player.hasPermission("tan.base.town.create")){
                        player.sendMessage(Lang.PLAYER_NO_PERMISSION.get(playerData));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }

                    double playerMoney = EconomyUtil.getBalance(player);
                    if (playerMoney < townPrice) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(playerData, townPrice - playerMoney));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                    }
                    else {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_WRITE_TOWN_NAME_IN_CHAT.get(playerData));
                        PlayerChatListenerStorage.register(player, new CreateTown(townPrice));
                    }
                })
                .asGuiItem(player);
    }

    private GuiItem getBrowseTownsButton() {

        return IconManager.getInstance().get(IconKey.BROWSE_TOWN_ICON)
                .setName(Lang.GUI_NO_TOWN_JOIN_A_TOWN.get(playerData))
                .setDescription(
                        Lang.GUI_NO_TOWN_JOIN_A_TOWN_DESC1.get(playerData, TownDataStorage.getInstance().getNumberOfTown()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData))
                .setAction(event -> {
                    new ApplyToTownMenu(player);
                })
                .asGuiItem(player);
    }




}
