package org.leralix.tan.listeners.chat.events;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.newsletter.storage.NewsletterStorage;
import org.leralix.tan.newsletter.news.TownCreatedNews;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.utils.FileUtil;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.lang.Lang;

import java.util.function.Consumer;

public class CreateEmptyTown extends ChatListenerEvent {

    private final Consumer<Player> guiCallback;

    public CreateEmptyTown(Consumer<Player> guiCallback) {
        this.guiCallback = guiCallback;
    }

    @Override
    public void execute(Player player, String townName) {
        FileConfiguration config =  ConfigUtil.getCustomConfig(ConfigTag.MAIN);
        int maxSize = config.getInt("TownNameSize");

        if(townName.length() > maxSize){
            player.sendMessage(TanChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        if(TownDataStorage.getInstance().isNameUsed(townName)){
            player.sendMessage(TanChatUtils.getTANString() + Lang.NAME_ALREADY_USED.get());
            return;
        }

        TownData newTown = TownDataStorage.getInstance().newTown(townName);
        PlayerChatListenerStorage.removePlayer(player);

        NewsletterStorage.register(new TownCreatedNews(newTown, player));
        FileUtil.addLineToHistory(Lang.TOWN_CREATED_NEWSLETTER.get(player.getName(),newTown.getName()));

        openGui(guiCallback, player);
    }
}
