package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanTown;

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
  public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(playerID);
    if (tanPlayer == null) return null;

    TownData townData = TownDataStorage.getInstance().getSync(townID);
    if (townData == null) return null;

    ItemStack itemStack =
        HeadUtils.makeSkullB64(
            Lang.TOWN_CREATED_NEWSLETTER_TITLE.get(lang),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=",
            Lang.NEWSLETTER_DATE.get(
                lang, TimeZoneManager.getInstance().getRelativeTimeDescription(lang, getDate())),
            Lang.TOWN_CREATED_NEWSLETTER.get(
                lang, tanPlayer.getNameStored(), townData.getBaseColoredName()),
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
    return createGuiItem(player, lang, onClick);
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
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(playerID);
    if (tanPlayer == null) return;

    TownData townData = TownDataStorage.getInstance().getSync(townID);
    if (townData == null) return;
    TanChatUtils.message(
        player,
        Lang.TOWN_CREATED_NEWSLETTER.get(
            player, tanPlayer.getNameStored(), townData.getBaseColoredName()),
        SoundEnum.GOOD);
  }

  @Override
  public void broadcastConcerned(Player player) {
    broadcast(player);
  }
}
