package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.gui.user.territory.PlayerApplicationMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanTown;

public class PlayerJoinRequestNews extends Newsletter {

  String playerID;
  String townID;

  public PlayerJoinRequestNews(UUID id, long date, String playerID, String townID) {
    super(id, date);
    this.playerID = playerID;
    this.townID = townID;
  }

  public PlayerJoinRequestNews(TanPlayer player, TanTown townData) {
    super();
    this.playerID = player.getUUID().toString();
    this.townID = townData.getID();
  }

  @Override
  public NewsletterType getType() {
    return NewsletterType.PLAYER_APPLICATION;
  }

  private TownData getTownData() {
    return TownDataStorage.getInstance().getSync(townID);
  }

  public String getPlayerID() {
    return playerID;
  }

  public String getTownID() {
    return townID;
  }

  @Override
  public void broadcast(Player player) {
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(playerID);
    if (tanPlayer == null) return;
    TownData townData = TownDataStorage.getInstance().getSync(townID);
    if (townData == null) return;
    TanChatUtils.message(
        player,
        Lang.PLAYER_APPLICATION_NEWSLETTER.get(
            player, tanPlayer.getNameStored(), townData.getBaseColoredName()),
        SoundEnum.MINOR_GOOD);
  }

  @Override
  public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {

    TownData townData = getTownData();
    if (townData == null) {
      return null;
    }

    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));

    ItemStack itemStack =
        HeadUtils.getPlayerHead(
            Lang.NEWSLETTER_PLAYER_APPLICATION.get(lang, offlinePlayer.getName()),
            offlinePlayer,
            Lang.NEWSLETTER_DATE.get(
                lang, TimeZoneManager.getInstance().getRelativeTimeDescription(lang, getDate())),
            Lang.NEWSLETTER_PLAYER_APPLICATION_DESC1.get(
                lang, offlinePlayer.getName(), getTownData().getBaseColoredName()),
            Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get(lang));

    return ItemBuilder.from(itemStack)
        .asGuiItem(
            event -> {
              event.setCancelled(true);
              if (event.isRightClick()) {
                markAsRead(player);
                onClick.accept(player);
              }
            });
  }

  @Override
  public GuiItem createConcernedGuiItem(Player player, LangType lang, Consumer<Player> onClick) {

    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));

    ItemStack itemStack =
        HeadUtils.getPlayerHead(
            Lang.NEWSLETTER_PLAYER_APPLICATION.get(lang, offlinePlayer.getName()),
            offlinePlayer,
            Lang.NEWSLETTER_PLAYER_APPLICATION_DESC1.get(
                lang,
                offlinePlayer.getName(),
                TownDataStorage.getInstance().getSync(townID).getBaseColoredName()),
            Lang.NEWSLETTER_PLAYER_APPLICATION_DESC2.get(lang),
            Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get(lang));

    return ItemBuilder.from(itemStack)
        .asGuiItem(
            event -> {
              event.setCancelled(true);
              if (event.isLeftClick()) {
                PlayerApplicationMenu.open(player, getTownData());
              }
              if (event.isRightClick()) {
                markAsRead(player);
                onClick.accept(player);
              }
            });
  }

  @Override
  public boolean shouldShowToPlayer(Player player) {
    TownData townData = getTownData();
    if (townData == null) {
      return false;
    }
    return townData.isPlayerIn(player);
  }

  @Override
  public void broadcastConcerned(Player player) {
    broadcast(player);
  }
}
