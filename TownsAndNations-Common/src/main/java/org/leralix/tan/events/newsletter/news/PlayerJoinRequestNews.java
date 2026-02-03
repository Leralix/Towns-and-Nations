package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.user.territory.PlayerApplicationMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.text.DateUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.territory.TanTown;

import java.util.UUID;
import java.util.function.Consumer;


public class PlayerJoinRequestNews extends Newsletter {

    private final String playerID;
    private final String townID;

    public PlayerJoinRequestNews(UUID id, long date, String playerID, String townID) {
        super(id, date);
        this.playerID = playerID;
        this.townID = townID;
    }

    public PlayerJoinRequestNews(TanPlayer player, TanTown townData) {
        super();
        this.playerID = player.getID().toString();
        this.townID = townData.getID();
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.PLAYER_APPLICATION;
    }

    private TownData getTownData() {
        return TownDataStorage.getInstance().get(townID);
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getTownID() {
        return townID;
    }

    @Override
    public void broadcast(Player player, ITanPlayer tanPlayer) {
        OfflinePlayer playerJoinRequest = Bukkit.getOfflinePlayer(UUID.fromString(playerID));

        TownData townData = TownDataStorage.getInstance().get(townID);
        if (townData == null)
            return;
        TanChatUtils.message(player,
                Lang.PLAYER_APPLICATION_NEWSLETTER.get(
                        tanPlayer,
                        playerJoinRequest.getName(),
                        townData.getColoredName()),
                SoundEnum.MINOR_GOOD);
    }

    @Override
    public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {

        TownData townData = getTownData();
        if (townData == null) {
            return null;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(playerID);
        } catch (IllegalArgumentException e) {
            return null;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        return IconManager.getInstance().get(offlinePlayer)
                .setName(Lang.NEWSLETTER_PLAYER_APPLICATION.get(lang, offlinePlayer.getName()))
                .setDescription(
                        Lang.NEWSLETTER_DATE.get(DateUtil.getRelativeTimeDescription(lang, getDate())),
                        Lang.NEWSLETTER_PLAYER_APPLICATION_DESC1.get(offlinePlayer.getName(), getTownData().getColoredName()),
                        Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get()
                )
                .setAction(action -> {
                    action.setCancelled(true);
                    if (action.isRightClick()) {
                        markAsRead(player);
                        onClick.accept(player);
                    }
                })
                .asGuiItem(player, lang);
    }

    @Override
    public GuiItem createConcernedGuiItem(Player player, LangType lang, Consumer<Player> onClick) {

        TownData townData = getTownData();
        if (townData == null) {
            return null;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(playerID);
        } catch (IllegalArgumentException e) {
            return null;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        return IconManager.getInstance().get(offlinePlayer)
                .setName(Lang.NEWSLETTER_PLAYER_APPLICATION.get(lang, offlinePlayer.getName()))
                .setDescription(
                        Lang.NEWSLETTER_PLAYER_APPLICATION_DESC1.get(offlinePlayer.getName(), townData.getColoredName()),
                        Lang.NEWSLETTER_PLAYER_APPLICATION_DESC2.get(),
                        Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get()
                )
                .setAction(action -> {
                    action.setCancelled(true);
                    if (action.isLeftClick()) {
                        new PlayerApplicationMenu(player, townData).open();
                    }
                    if (action.isRightClick()) {
                        markAsRead(player);
                        onClick.accept(player);
                    }
                })
                .asGuiItem(player, lang);
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        TownData townData = getTownData();
        if (townData == null) {
            return false;
        }
        return townData.isPlayerIn(player);
    }
}
