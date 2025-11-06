package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.TanTerritory;

public class TerritoryVassalAcceptedNews extends Newsletter {

  private final String regionID;
  private final String townID;

  public TerritoryVassalAcceptedNews(TanTerritory regionID, TanTerritory townID) {
    super();
    this.regionID = regionID.getID();
    this.townID = townID.getID();
  }

  public TerritoryVassalAcceptedNews(
      UUID id, long date, String proposingTerritoryID, String receivingTerritoryID) {
    super(id, date);
    this.regionID = proposingTerritoryID;
    this.townID = receivingTerritoryID;
  }

  @Override
  public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
    TerritoryData region = TerritoryUtil.getTerritory(regionID);
    if (region == null) return null;
    TerritoryData town = TerritoryUtil.getTerritory(townID);
    if (town == null) return null;

    // BUGFIX: Convert Adventure Component to legacy text properly
    ItemStack itemStack =
        HeadUtils.makeSkullB64(
            Lang.TOWN_JOIN_REGION_ACCEPTED_NEWSLETTER_TITLE.get(lang),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkMDJjZGMwNzViYjFjYzVmNmZlM2M3NzExYWU0OTc3ZTM4YjkxMGQ1MGVkNjAyM2RmNzM5MTNlNWU3ZmNmZiJ9fX0=",
            Lang.NEWSLETTER_DATE.get(
                lang, TimeZoneManager.getInstance().getRelativeTimeDescription(lang, getDate())),
            Lang.TOWN_JOIN_REGION_ACCEPTED_NEWSLETTER.get(
                lang,
                LegacyComponentSerializer.legacySection().serialize(town.getCustomColoredName()),
                LegacyComponentSerializer.legacySection().serialize(region.getCustomColoredName())),
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
    TerritoryData proposingTerritory = TerritoryUtil.getTerritory(regionID);
    if (proposingTerritory == null) return false;
    TerritoryData receivingTerritory = TerritoryUtil.getTerritory(townID);
    if (receivingTerritory == null) return false;
    return proposingTerritory.isPlayerIn(player) || receivingTerritory.isPlayerIn(player);
  }

  @Override
  public NewsletterType getType() {
    return NewsletterType.TERRITORY_VASSAL_ACCEPTED;
  }

  @Override
  public void broadcast(Player player) {
    TerritoryData proposingTerritory = TerritoryUtil.getTerritory(regionID);
    if (proposingTerritory == null) return;
    TerritoryData receivingTerritory = TerritoryUtil.getTerritory(townID);
    if (receivingTerritory == null) return;

    // BUGFIX: Convert Adventure Component to legacy text properly
    TanChatUtils.message(
        player,
        Lang.TOWN_JOIN_REGION_ACCEPTED_NEWSLETTER.get(
            player,
            LegacyComponentSerializer.legacySection()
                .serialize(proposingTerritory.getCustomColoredName()),
            LegacyComponentSerializer.legacySection()
                .serialize(receivingTerritory.getCustomColoredName())),
        SoundEnum.MINOR_GOOD);
  }

  public String getProposingTerritoryID() {
    return regionID;
  }

  public String getReceivingTerritoryID() {
    return townID;
  }

  @Override
  public void broadcastConcerned(Player player) {
    broadcast(player);
  }
}
