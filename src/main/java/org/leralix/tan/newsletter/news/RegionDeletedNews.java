package org.leralix.tan.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.newsletter.NewsletterType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;

import java.util.function.Consumer;

import static org.leralix.tan.utils.TanChatUtils.getTANString;

public class RegionDeletedNews extends Newsletter {

    private final String playerID;
    private final String regionName;

    public RegionDeletedNews(String playerID, RegionData regionData) {
        super();
        this.playerID = playerID;
        regionName = regionData.getBaseColoredName();
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.REGION_CREATION;
    }

    @Override
    public void broadcast(Player player) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(playerID);
        if(playerData == null)
            return;
        player.sendMessage(getTANString() + Lang.REGION_DELETED_NEWSLETTER.get(playerData.getNameStored(), regionName));
        SoundUtil.playSound(player, SoundEnum.GOOD);
    }

    @Override
    public GuiItem createGuiItem(Player player, Consumer<Player> onClick) {
        return null;
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        return true;
    }
}
