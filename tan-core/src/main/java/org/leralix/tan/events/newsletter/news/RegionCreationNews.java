package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanRegion;

public class RegionCreationNews extends Newsletter {

  private final String playerID;
  private final String regionID;

  public RegionCreationNews(TanRegion regionData, TanPlayer player) {
    super();
    playerID = player.getUUID().toString();
    regionID = regionData.getID();
  }

  public RegionCreationNews(UUID id, long date, String playerID, String regionID) {
    super(id, date);
    this.playerID = playerID;
    this.regionID = regionID;
  }

  @Override
  public NewsletterType getType() {
    return NewsletterType.REGION_CREATED;
  }

  public String getPlayerID() {
    return playerID;
  }

  public String getRegionID() {
    return regionID;
  }

  @Override
  public void broadcast(Player player) {
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(playerID);
    if (tanPlayer == null) return;
    RegionData regionData = RegionDataStorage.getInstance().getSync(regionID);
    if (regionData == null) return;
    TanChatUtils.message(
        player,
        Lang.REGION_CREATED_NEWSLETTER.get(
            player, tanPlayer.getNameStored(), regionData.getBaseColoredName()));
  }

  @Override
  public void broadcastConcerned(Player player) {
    broadcast(player);
  }

  @Override
  public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(playerID);
    if (tanPlayer == null) return null;
    RegionData regionData = RegionDataStorage.getInstance().getSync(regionID);
    if (regionData == null) return null;

    ItemStack itemStack =
        HeadUtils.makeSkullB64(
            Lang.REGION_CREATED_NEWSLETTER_TITLE.get(lang),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljMTgzMmU0ZWY1YzRhZDljNTE5ZDE5NGIxOTg1MDMwZDI1NzkxNDMzNGFhZjI3NDVjOWRmZDYxMWQ2ZDYxZCJ9fX0=",
            Lang.NEWSLETTER_DATE.get(
                lang, TimeZoneManager.getInstance().getRelativeTimeDescription(lang, getDate())),
            Lang.REGION_CREATED_NEWSLETTER.get(
                lang, tanPlayer.getNameStored(), regionData.getBaseColoredName()),
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
