package org.leralix.tan.listeners.chat.events;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.TownCreatedInternalEvent;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.function.Consumer;

public class CreateEmptyTown extends ChatListenerEvent {

    private final Consumer<Player> guiCallback;

    public CreateEmptyTown(Consumer<Player> guiCallback) {
        this.guiCallback = guiCallback;
    }

    @Override
    public boolean execute(Player player, String townName) {
        FileConfiguration config =  ConfigUtil.getCustomConfig(ConfigTag.MAIN);
        int maxSize = config.getInt("TownNameSize");

        if(townName.length() > maxSize){
            player.sendMessage(TanChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return false;
        }

        if(TownDataStorage.getInstance().isNameUsed(townName)){
            player.sendMessage(TanChatUtils.getTANString() + Lang.NAME_ALREADY_USED.get());
            return false;
        }

        TownData newTown = TownDataStorage.getInstance().newTown(townName);

        ITanPlayer playerData = PlayerDataStorage.getInstance().get(player);
        EventManager.getInstance().callEvent(new TownCreatedInternalEvent(newTown, playerData));
        FileUtil.addLineToHistory(Lang.TOWN_CREATED_NEWSLETTER.get(player.getName(),newTown.getName()));

        openGui(guiCallback, player);
        return true;
    }
}
