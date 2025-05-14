package org.leralix.tan.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.newsletter.NewsletterType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

import java.util.function.Consumer;

import static org.leralix.tan.utils.TanChatUtils.getTANString;

public class TownCreatedNews extends Newsletter {

    private final String playerID;
    private final String townID;

    public TownCreatedNews(TownData townData, Player player) {
        this(townData.getID(), player.getUniqueId().toString());
    }

    public TownCreatedNews(String townID, String playerID) {
        super();
        this.playerID = townID;
        this.townID = playerID;
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
        return NewsletterType.TOWN_CREATION;
    }

    @Override
    public void broadcast(Player player) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(playerID);
        if (playerData == null)
            return;

        TownData townData = TownDataStorage.getInstance().get(townID);
        if (townData == null)
            return;
        player.sendMessage(getTANString() + Lang.TOWN_CREATED_NEWSLETTER.get(playerData.getNameStored(), townData.getBaseColoredName()));
        SoundUtil.playSound(player, SoundEnum.GOOD);
    }
}
