package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.HeadUtils;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanTown;

import java.util.UUID;
import java.util.function.Consumer;

import static org.leralix.tan.utils.TanChatUtils.getTANString;

public class TownCreatedNews extends Newsletter {

    private final String playerID;
    private final String townID;

    public TownCreatedNews(TanTown townData, TanPlayer player) {
        this(townData.getID(), player.getUUID().toString());
    }

    public TownCreatedNews(String townID, String playerID) {
        super();
        this.townID = townID;
        this.playerID = playerID;
    }

    public TownCreatedNews(UUID id, long date, String playerID, String townID) {
        super(id, date);
        this.playerID = playerID;
        this.townID = townID;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getTownID() {
        return townID;
    }

    @Override
    public GuiItem createGuiItem(Player player, Consumer<Player> onClick) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(playerID);
        if (tanPlayer == null)
            return null;

        TownData townData = TownDataStorage.getInstance().get(townID);
        if (townData == null)
            return null;

        ItemStack itemStack = HeadUtils.makeSkullB64(Lang.TOWN_CREATED_NEWSLETTER_TITLE.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=",
                Lang.TOWN_CREATED_NEWSLETTER.get(tanPlayer.getNameStored(), townData.getBaseColoredName()),
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

    @Override
    public NewsletterType getType() {
        return NewsletterType.TOWN_CREATED;
    }

    @Override
    public void broadcast(Player player) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(playerID);
        if (tanPlayer == null)
            return;

        TownData townData = TownDataStorage.getInstance().get(townID);
        if (townData == null)
            return;
        player.sendMessage(getTANString() + Lang.TOWN_CREATED_NEWSLETTER.get(tanPlayer.getNameStored(), townData.getBaseColoredName()));
        SoundUtil.playSound(player, SoundEnum.GOOD);
    }

    @Override
    public void broadcastConcerned(Player player) {
        broadcast(player);
    }
}
