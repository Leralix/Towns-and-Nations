package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.enums.EDiplomacyState;
import org.tan.api.interfaces.TanTerritory;

public class DiplomacyAcceptedNews extends Newsletter {
  private final String proposingTerritoryID;
  private final String receivingTerritoryID;
  private final TownRelation wantedRelation;
  private final boolean isRelationWorse;

  public DiplomacyAcceptedNews(
      UUID id,
      long date,
      String proposingTerritoryID,
      String receivingTerritoryID,
      TownRelation wantedRelation,
      boolean isRelationWorse) {
    super(id, date);
    this.proposingTerritoryID = proposingTerritoryID;
    this.receivingTerritoryID = receivingTerritoryID;
    this.wantedRelation = wantedRelation;
    this.isRelationWorse = isRelationWorse;
  }

  public DiplomacyAcceptedNews(
      TanTerritory proposingTerritory,
      TanTerritory receivingTerritory,
      EDiplomacyState newRelation,
      boolean isRelationBetter) {
    super();
    this.proposingTerritoryID = proposingTerritory.getID();
    this.receivingTerritoryID = receivingTerritory.getID();
    this.wantedRelation = TownRelation.fromAPI(newRelation);
    this.isRelationWorse = !isRelationBetter;
  }

  @Override
  public NewsletterType getType() {
    return NewsletterType.DIPLOMACY_ACCEPTED;
  }

  public String getProposingTerritoryID() {
    return proposingTerritoryID;
  }

  public String getReceivingTerritoryID() {
    return receivingTerritoryID;
  }

  public TownRelation getWantedRelation() {
    return wantedRelation;
  }

  public boolean isRelationWorse() {
    return isRelationWorse;
  }

  @Override
  public void broadcast(Player player) {
    TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
    if (proposingTerritory == null) return;
    TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
    if (receivingTerritory == null) return;

    LangType lang = PlayerDataStorage.getInstance().getSync(player).getLang();

    // BUGFIX: Convert Adventure Component to legacy text properly
    if (isRelationWorse) {
      TanChatUtils.message(
          player,
          Lang.BROADCAST_RELATION_WORSEN.get(
              player,
              LegacyComponentSerializer.legacySection()
                  .serialize(proposingTerritory.getCustomColoredName()),
              LegacyComponentSerializer.legacySection()
                  .serialize(receivingTerritory.getCustomColoredName()),
              wantedRelation.getColoredName(lang)),
          SoundEnum.BAD);
    } else {
      TanChatUtils.message(
          player,
          Lang.BROADCAST_RELATION_IMPROVE.get(
              player,
              LegacyComponentSerializer.legacySection()
                  .serialize(proposingTerritory.getCustomColoredName()),
              LegacyComponentSerializer.legacySection()
                  .serialize(receivingTerritory.getCustomColoredName()),
              wantedRelation.getColoredName(lang)),
          SoundEnum.GOOD);
    }
  }

  @Override
  public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {

    TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
    if (proposingTerritory == null) return null;
    TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
    if (receivingTerritory == null) return null;

    // BUGFIX: Convert Adventure Component to legacy text properly
    ItemStack itemStack =
        HeadUtils.makeSkullURL(
            Lang.DIPLOMACY_ACCEPT_NEWSLETTER_TITLE.get(lang),
            "http://textures.minecraft.net/texture/b62c08805bd9c957da3450554a09e994042f54695db855c1c2cb47ef442e1bf6",
            Lang.NEWSLETTER_DATE.get(
                lang, TimeZoneManager.getInstance().getRelativeTimeDescription(lang, getDate())),
            Lang.BROADCAST_RELATION_WORSEN.get(
                lang,
                LegacyComponentSerializer.legacySection()
                    .serialize(proposingTerritory.getCustomColoredName()),
                LegacyComponentSerializer.legacySection()
                    .serialize(receivingTerritory.getCustomColoredName()),
                wantedRelation.getColoredName(lang)),
            Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get(lang));

    return ItemBuilder.from(itemStack)
        .asGuiItem(
            e -> {
              e.setCancelled(true);
              if (e.isRightClick()) {
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
    TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
    if (proposingTerritory == null) return false;
    TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
    if (receivingTerritory == null) return false;
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    return receivingTerritory.isPlayerIn(tanPlayer) || proposingTerritory.isPlayerIn(tanPlayer);
  }

  @Override
  public void broadcastConcerned(Player player) {
    broadcast(player);
  }
}
