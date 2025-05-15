package org.leralix.tan.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.newsletter.NewsletterType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.HeadUtils;

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
        this.townID = townID;
        this.playerID = playerID;
    }

    @Override
    public GuiItem createGuiItem(Player player, Consumer<Player> onClick) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(playerID);
        if (playerData == null)
            return null;

        TownData townData = TownDataStorage.getInstance().get(townID);
        if (townData == null)
            return null;

        ItemStack itemStack = HeadUtils.makeSkullB64(Lang.TOWN_CREATED_NEWSLETTER_TITLE.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=",
                Lang.TOWN_CREATED_NEWSLETTER.get(playerData.getNameStored(), townData.getBaseColoredName()));


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
