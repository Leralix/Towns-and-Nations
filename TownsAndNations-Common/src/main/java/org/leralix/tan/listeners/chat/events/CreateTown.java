package org.leralix.tan.listeners.chat.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.TownCreatedInternalEvent;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.graphic.TeamUtils;
import org.leralix.tan.utils.text.NameFilter;
import org.leralix.tan.utils.text.TanChatUtils;

public class CreateTown extends ChatListenerEvent {
    int cost;

    public CreateTown(int cost) {
        super();
        this.cost = cost;
    }

    @Override
    public boolean execute(Player player, String message) {
        double playerBalance = EconomyUtil.getBalance(player);

        if (playerBalance < cost) {
            TanChatUtils.message(player, Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(player, Double.toString(cost - playerBalance)));
            return false;
        }

        int minSize = Constants.getPrefixSize().getMinVal();
        int maxSize = Constants.getTownMaxNameSize();

        String townName = message == null ? "" : message.trim();

        if (!NameFilter.validateOrWarn(player, townName, NameFilter.Scope.TOWN)) {
            return false;
        }

        if (checkMessageLength(player, townName, minSize, maxSize)){
            return false;
        }

        if (TownDataStorage.getInstance().isNameUsed(townName)) {
            TanChatUtils.message(player, Lang.NAME_ALREADY_USED.get(player));
            return false;
        }
        createTown(player, townName);

        return true;
    }

    public void createTown(Player player, String message) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        TownData newTown = TownDataStorage.getInstance().newTown(message, tanPlayer);
        EconomyUtil.removeFromBalance(player, cost);

        ITanPlayer playerData = PlayerDataStorage.getInstance().get(player);
        EventManager.getInstance().callEvent(new TownCreatedInternalEvent(newTown, playerData));
        FileUtil.addLineToHistory(Lang.TOWN_CREATED_NEWSLETTER.get(player.getName(), newTown.getName()));

        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> TeamUtils.setIndividualScoreBoard(player));

        openGui(p -> PlayerGUI.dispatchPlayerTown(player), player);
    }
}
