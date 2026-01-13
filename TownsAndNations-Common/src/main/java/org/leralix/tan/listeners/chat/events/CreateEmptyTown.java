package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.TownCreatedInternalEvent;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.constants.Constants;
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
        int minSize = Constants.getPrefixSize().getMinVal();
        int maxSize = Constants.getTownMaxNameSize();

        if (checkMessageLength(player, townName, minSize, maxSize)){
            return false;
        }

        if (TownDataStorage.getInstance().isNameUsed(townName)) {
            TanChatUtils.message(player, Lang.NAME_ALREADY_USED.get(player));
            return false;
        }

        TownData newTown = TownDataStorage.getInstance().newTown(townName);

        ITanPlayer playerData = PlayerDataStorage.getInstance().get(player);
        EventManager.getInstance().callEvent(new TownCreatedInternalEvent(newTown, playerData));
        FileUtil.addLineToHistory(Lang.TOWN_CREATED_NEWSLETTER.get(player.getName(), newTown.getName()));

        openGui(guiCallback, player);
        return true;
    }
}
