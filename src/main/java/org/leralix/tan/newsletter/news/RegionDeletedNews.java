package org.leralix.tan.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.newsletter.NewsletterType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.utils.HeadUtils;

import java.util.function.Consumer;

import static org.leralix.tan.utils.TanChatUtils.getTANString;

public class RegionDeletedNews extends Newsletter {

    private final String playerID;
    private final String regionName;

    public RegionDeletedNews(String playerID, RegionData regionData) {
        super();
        this.playerID = playerID;
        this.regionName = regionData.getCustomColoredName().toLegacyText();
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
        PlayerData playerData = PlayerDataStorage.getInstance().get(playerID);
        if(playerData == null)
            return null;
        ItemStack itemStack = HeadUtils.makeSkullB64(Lang.REGION_DELETED_NEWSLETTER_TITLE.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljMTgzMmU0ZWY1YzRhZDljNTE5ZDE5NGIxOTg1MDMwZDI1NzkxNDMzNGFhZjI3NDVjOWRmZDYxMWQ2ZDYxZCJ9fX0=",
                Lang.REGION_DELETED_NEWSLETTER.get(playerData.getNameStored(), regionName));

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
