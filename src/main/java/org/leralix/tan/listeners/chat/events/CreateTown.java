package org.leralix.tan.listeners.chat.events;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
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
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.graphic.TeamUtils;

public class CreateTown extends ChatListenerEvent {
    int cost;
    public CreateTown(int cost) {
        super();
        this.cost = cost;
    }

    @Override
    public boolean execute(Player player, String message) {
        double playerBalance = EconomyUtil.getBalance(player);

        if(playerBalance < cost){
            player.sendMessage(Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(player, Double.toString(cost - playerBalance)));
            return false;
        }

        FileConfiguration config =  ConfigUtil.getCustomConfig(ConfigTag.MAIN);
        int maxSize = config.getInt("TownNameSize", 45);

        if(message.length() > maxSize){
            player.sendMessage(Lang.MESSAGE_TOO_LONG.get(player, Integer.toString(maxSize)));
            return false;
        }

        if(TownDataStorage.getInstance().isNameUsed(message)){
            player.sendMessage(Lang.NAME_ALREADY_USED.get(player));
            return false;
        }
        createTown(player, message);

        return true;
    }

    public void createTown(Player player, String message) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        TownData newTown = TownDataStorage.getInstance().newTown(message, tanPlayer);
        EconomyUtil.removeFromBalance(player,cost);

        ITanPlayer playerData = PlayerDataStorage.getInstance().get(player);
        EventManager.getInstance().callEvent(new TownCreatedInternalEvent(newTown, playerData));
        FileUtil.addLineToHistory(Lang.TOWN_CREATED_NEWSLETTER.get(player.getName(), newTown.getName()));

        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> TeamUtils.setIndividualScoreBoard(player));

        openGui(p -> PlayerGUI.dispatchPlayerTown(player), player);
    }
}
