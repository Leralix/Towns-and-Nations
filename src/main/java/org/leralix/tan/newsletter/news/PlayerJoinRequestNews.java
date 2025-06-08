package org.leralix.tan.newsletter.news;

import dev.triumphteam.gui
.builder.item.ItemBuilder;
import dev.triumphteam.gui
.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.user.territory.PlayerApplicationMenu;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.newsletter.NewsletterType;

import java.util.UUID;
import java.util.function.Consumer;

import static org.leralix.tan.utils.TanChatUtils.getTANString;

public class PlayerJoinRequestNews extends Newsletter {

    String playerID;
    String townID;

    public PlayerJoinRequestNews(UUID id, long date, String playerID, String townID) {
        super(id, date);
        this.playerID = playerID;
        this.townID = townID;
    }

    public PlayerJoinRequestNews(Player player, TownData townData) {
        super();
        this.playerID = player.getUniqueId().toString();
        this.townID = townData.getID();
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.PLAYER_APPLICATION;
    }

    private TownData getTownData(){
        return TownDataStorage.getInstance().get(townID);
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getTownID() {
        return townID;
    }

    @Override
    public void broadcast(Player player) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(playerID);
        if(playerData == null)
            return;
        TownData townData = TownDataStorage.getInstance().get(townID);
        if(townData == null)
            return;
        player.sendMessage(getTANString() + Lang.PLAYER_APPLICATION_NEWSLETTER.get(playerData.getNameStored(), townData.getBaseColoredName()));
        SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
    }

    @Override
    public GuiItem createGuiItem(Player player, Consumer<Player> onClick) {

        TownData townData = getTownData();
        if(townData == null){
            return null;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));

        ItemStack itemStack = HeadUtils.getPlayerHead(Lang.NEWSLETTER_PLAYER_APPLICATION.get(offlinePlayer.getName()), offlinePlayer,
                Lang.NEWSLETTER_PLAYER_APPLICATION_DESC1.get(offlinePlayer.getName(), getTownData().getBaseColoredName()),
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

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));

        ItemStack itemStack = HeadUtils.getPlayerHead(Lang.NEWSLETTER_PLAYER_APPLICATION.get(offlinePlayer.getName()), offlinePlayer,
                Lang.NEWSLETTER_PLAYER_APPLICATION_DESC1.get(offlinePlayer.getName(), TownDataStorage.getInstance().get(townID).getBaseColoredName()),
                Lang.NEWSLETTER_PLAYER_APPLICATION_DESC2.get(),
                Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get());

        return ItemBuilder.from(itemStack).asGuiItem(event -> {
            event.setCancelled(true);
            if(event.isLeftClick()){
                new PlayerApplicationMenu(player, getTownData()).open();
            }
            if(event.isRightClick()){
                markAsRead(player);
                onClick.accept(player);
            }
        });
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        TownData townData = getTownData();
        if(townData == null){
            return false;
        }
        return townData.isPlayerIn(player);
    }

    @Override
    public void broadcastConcerned(Player player) {
        broadcast(player);
    }
}
