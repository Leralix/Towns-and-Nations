package org.leralix.tan.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.newsletter.NewsletterScope;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.newsletter.NewsletterStorage;
import org.leralix.tan.newsletter.NewsletterType;

import java.util.UUID;
import java.util.function.Consumer;

public class PlayerJoinRequestNL extends Newsletter {

    String playerID;
    String townID;

    public PlayerJoinRequestNL(Player player, TownData townData) {
        super();
        playerID = player.getUniqueId().toString();
        townID = townData.getID();
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.PLAYER_TOWN_JOIN_REQUEST;
    }

    @Override
    public GuiItem createGuiItem(Player player, Consumer<Player> onClick) {

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));

        ItemStack itemStack = HeadUtils.getPlayerHead(Lang.NEWSLETTER_PLAYER_APPLICATION.get(offlinePlayer.getName()), offlinePlayer,
                Lang.NEWSLETTER_PLAYER_APPLICATION_DESC1.get(offlinePlayer.getName(), TownDataStorage.get(townID).getColoredName()),
                Lang.NEWSLETTER_PLAYER_APPLICATION_DESC2.get(),
                Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get());

        return ItemBuilder.from(itemStack).asGuiItem(event -> {
            event.setCancelled(true);
            if(event.isLeftClick()){
                PlayerGUI.openTownApplications(player,getTownData());
            }
            if(event.isRightClick()){
                markAsRead(player);
                onClick.accept(player);
            }
        });
    }

    @Override
    public boolean shouldShowToPlayer(Player player, NewsletterScope scope) {
        if(isRead(player) && scope == NewsletterScope.SHOW_ONLY_UNREAD)
            return false;
        TownData townData = getTownData();
        if(townData == null) {
            NewsletterStorage.removePlayerJoinRequest(this);
            return false;
        }
        return townData.doesPlayerHavePermission(player, RolePermission.INVITE_PLAYER);
    }

    protected TownData getTownData(){
        return TownDataStorage.get(townID);
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getTownID() {
        return townID;
    }
}
