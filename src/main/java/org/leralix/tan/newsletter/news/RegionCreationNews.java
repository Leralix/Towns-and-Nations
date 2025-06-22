package org.leralix.tan.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.newsletter.NewsletterType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.utils.HeadUtils;

import java.util.UUID;
import java.util.function.Consumer;

import static org.leralix.tan.utils.TanChatUtils.getTANString;

public class RegionCreationNews extends Newsletter {

    private final String playerID;
    private final String regionID;

    public RegionCreationNews(Player player, RegionData regionData) {
        super();
        playerID = player.getUniqueId().toString();
        regionID = regionData.getID();
    }

    public RegionCreationNews(UUID id, long date, String playerID, String regionID) {
        super(id, date);
        this.playerID = playerID;
        this.regionID = regionID;
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.REGION_CREATED;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getRegionID() {
        return regionID;
    }

    @Override
    public void broadcast(Player player) {
        ITanPlayer ITanPlayer = PlayerDataStorage.getInstance().get(playerID);
        if(ITanPlayer == null)
            return;
        RegionData regionData = RegionDataStorage.getInstance().get(regionID);
        if(regionData == null)
            return;
        player.sendMessage(getTANString() + Lang.REGION_CREATED_NEWSLETTER.get(ITanPlayer.getNameStored(), regionData.getBaseColoredName()));
        SoundUtil.playSound(player, SoundEnum.GOOD);
    }

    @Override
    public void broadcastConcerned(Player player) {
        broadcast(player);
    }

    @Override
    public GuiItem createGuiItem(Player player, Consumer<Player> onClick) {

        ITanPlayer ITanPlayer = PlayerDataStorage.getInstance().get(playerID);
        if(ITanPlayer == null)
            return null;
        RegionData regionData = RegionDataStorage.getInstance().get(regionID);
        if(regionData == null)
            return null;

        ItemStack itemStack = HeadUtils.makeSkullB64(Lang.REGION_CREATED_NEWSLETTER_TITLE.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljMTgzMmU0ZWY1YzRhZDljNTE5ZDE5NGIxOTg1MDMwZDI1NzkxNDMzNGFhZjI3NDVjOWRmZDYxMWQ2ZDYxZCJ9fX0=",
                Lang.REGION_CREATED_NEWSLETTER.get(ITanPlayer.getNameStored(), regionData.getBaseColoredName()),
                Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get());

        return ItemBuilder.from(itemStack).asGuiItem(event -> {
            event.setCancelled(true);
            if(event.isRightClick()){
                markAsRead(player);
                onClick.accept(player);
            }
        });
    }

    @Override
    public GuiItem createConcernedGuiItem(Player player, Consumer<Player> onClick) {
        return createGuiItem(player, onClick);
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        return true;
    }
}
