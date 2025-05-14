package org.leralix.tan.listeners.chat.events;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.newsletter.NewsletterStorage;
import org.leralix.tan.newsletter.news.TownCreatedNews;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.utils.FileUtil;
import org.leralix.tan.utils.TeamUtils;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.lang.Lang;

public class CreateTown extends ChatListenerEvent {
    int cost;
    public CreateTown(int cost) {
        super();
        this.cost = cost;
    }

    @Override
    public void execute(Player player, String message) {
        double playerBalance = EconomyUtil.getBalance(player);

        if(playerBalance < cost){
            player.sendMessage(Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(cost - playerBalance));
            return;
        }

        FileConfiguration config =  ConfigUtil.getCustomConfig(ConfigTag.MAIN);
        int maxSize = config.getInt("TownNameSize");

        if(message.length() > maxSize){
            player.sendMessage(TanChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        if(TownDataStorage.getInstance().isNameUsed(message)){
            player.sendMessage(TanChatUtils.getTANString() + Lang.NAME_ALREADY_USED.get());
            return;
        }
        PlayerChatListenerStorage.removePlayer(player);
        createTown(player, message);

    }

    public void createTown(Player player, String message) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        TownData newTown = TownDataStorage.getInstance().newTown(message, playerData);
        EconomyUtil.removeFromBalance(player,cost);

        NewsletterStorage.register(new TownCreatedNews(newTown, player));
        FileUtil.addLineToHistory(Lang.TOWN_CREATED_NEWSLETTER.get(player.getName(), newTown.getName()));

        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> TeamUtils.setIndividualScoreBoard(player));

        openGui(p -> PlayerGUI.dispatchPlayerTown(player), player);
    }
}
