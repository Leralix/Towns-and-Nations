package org.leralix.tan.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.newsletter.NewsletterType;
import org.leralix.tan.storage.stored.PlayerDataStorage;

import java.util.function.Consumer;

import static org.leralix.tan.utils.TanChatUtils.getTANString;

public class TownDeletedNews extends Newsletter {

    private final String oldTownName;
    private final String playerID;

    public TownDeletedNews(String townID, String playerID) {
        super();
        this.oldTownName = townID;
        this.playerID = playerID;
    }

    @Override
    public GuiItem createGuiItem(Player player, Consumer<Player> onClick) {
        return null;
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        return true;
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.TOWN_DELETION;
    }

    @Override
    public void broadcast(Player player) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(playerID);
        if (playerData == null)
            return;

        player.sendMessage(getTANString() + Lang.TOWN_DELETED_NEWSLETTER.get(playerData.getNameStored(), oldTownName));
        SoundUtil.playSound(player, SoundEnum.BAD);
    }
}
