package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.text.DateUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.territory.TanTown;

import java.util.UUID;
import java.util.function.Consumer;


public class PlayerJoinTownNews extends Newsletter {

    String playerID;
    String townID;

    public PlayerJoinTownNews(TanPlayer tanPlayer, TanTown townData) {
        super();
        playerID = tanPlayer.getID().toString();
        townID = townData.getID();
    }

    public PlayerJoinTownNews(UUID id, long date, String playerID, String townID) {
        super(id, date);
        this.playerID = playerID;
        this.townID = townID;
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.PLAYER_JOIN_TOWN;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getTownID() {
        return townID;
    }

    @Override
    public void broadcast(Player player, ITanPlayer tanPlayer) {
        OfflinePlayer playerJoiningTown = Bukkit.getOfflinePlayer(UUID.fromString(playerID));
        TownData townData = TownDataStorage.getInstance().get(townID);
        if (townData == null)
            return;
        TanChatUtils.message(player, Lang.PLAYER_JOINED_TOWN_NEWSLETTER.get(tanPlayer, playerJoiningTown.getName(), townData.getColoredName()), SoundEnum.MINOR_GOOD);
    }

    @Override
    public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
        OfflinePlayer playerJoiningTown = Bukkit.getOfflinePlayer(UUID.fromString(playerID));

        TownData townData = TownDataStorage.getInstance().get(townID);
        if (townData == null)
            return null;


        return IconManager.getInstance().get(IconKey.NEWSLETTER_PLAYER_JOIN_TOWN_ICON)
                .setName(Lang.PLAYER_JOINED_TOWN_NEWSLETTER_TITLE.get(lang))
                .setDescription(
                        Lang.NEWSLETTER_DATE.get(DateUtil.getRelativeTimeDescription(lang, getDate())),
                        Lang.PLAYER_JOINED_TOWN_NEWSLETTER.get(playerJoiningTown.getName(), townData.getCustomColoredName().toLegacyText())
                )
                .setClickToAcceptMessage(
                        Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ
                )
                .setAction(action -> {
                            action.setCancelled(true);
                            if (action.isRightClick()) {
                                markAsRead(player);
                                onClick.accept(player);
                            }
                        }
                ).asGuiItem(player, lang);
    }

    @Override
    public GuiItem createConcernedGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
        return createGuiItem(player, lang, onClick);
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        TownData townData = TownDataStorage.getInstance().get(townID);
        if (townData == null)
            return false;
        return townData.doesPlayerHavePermission(player, RolePermission.INVITE_PLAYER);
    }
}
