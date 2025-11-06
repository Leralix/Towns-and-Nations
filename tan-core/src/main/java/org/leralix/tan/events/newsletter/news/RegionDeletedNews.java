package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanRegion;

public class RegionDeletedNews extends Newsletter {

  private final String playerID;
  private final String regionName;

  public RegionDeletedNews(TanRegion regionData, TanPlayer player) {
    super();
    this.playerID = player.getUUID().toString();
    this.regionName = regionData.getName();
  }

  public RegionDeletedNews(UUID id, long date, String playerID, String oldRegionName) {
    super(id, date);
    this.playerID = playerID;
    this.regionName = oldRegionName;
  }

  public String getPlayerID() {
    return playerID;
  }

  public String getRegionName() {
    return regionName;
  }

  @Override
  public NewsletterType getType() {
    return NewsletterType.REGION_DELETED;
  }

  @Override
  public void broadcast(Player player) {
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(playerID);
    if (tanPlayer == null) return;
    TanChatUtils.message(
        player,
        Lang.REGION_DELETED_NEWSLETTER.get(player, tanPlayer.getNameStored(), regionName),
        SoundEnum.GOOD);
  }

  @Override
  public void broadcastConcerned(Player player) {
    broadcast(player);
  }

  @Override
  public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(playerID);
    if (tanPlayer == null) return null;
    ItemStack itemStack =
        HeadUtils.makeSkullB64(
            Lang.REGION_DELETED_NEWSLETTER_TITLE.get(lang),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljMTgzMmU0ZWY1YzRhZDljNTE5ZDE5NGIxOTg1MDMwZDI1NzkxNDMzNGFhZjI3NDVjOWRmZDYxMWQ2ZDYxZCJ9fX0=",
            Lang.NEWSLETTER_DATE.get(
                lang, TimeZoneManager.getInstance().getRelativeTimeDescription(lang, getDate())),
            Lang.REGION_DELETED_NEWSLETTER.get(lang, tanPlayer.getNameStored(), regionName),
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
}
