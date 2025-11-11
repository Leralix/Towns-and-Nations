package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.gui.user.territory.relation.OpenDiplomacyProposalsMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.enums.EDiplomacyState;
import org.tan.api.interfaces.TanTerritory;

public class DiplomacyProposalNews extends Newsletter {
  private final String proposingTerritoryID;
  private final String receivingTerritoryID;
  private final TownRelation wantedRelation;

  public DiplomacyProposalNews(
      UUID id,
      long date,
      String proposingTerritoryID,
      String receivingTerritoryID,
      TownRelation wantedRelation) {
    super(id, date);
    this.proposingTerritoryID = proposingTerritoryID;
    this.receivingTerritoryID = receivingTerritoryID;
    this.wantedRelation = wantedRelation;
  }

  public DiplomacyProposalNews(
      TanTerritory proposingTerritory,
      TanTerritory receivingTerritory,
      EDiplomacyState wantedRelation) {
    super();
    this.proposingTerritoryID = proposingTerritory.getID();
    this.receivingTerritoryID = receivingTerritory.getID();
    this.wantedRelation = TownRelation.fromAPI(wantedRelation);
  }

  @Override
  public NewsletterType getType() {
    return NewsletterType.DIPLOMACY_PROPOSAL;
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

  @Override
  public void broadcast(Player player) {
    TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
    if (proposingTerritory == null) return;
    TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
    if (receivingTerritory == null) return;
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    // BUGFIX: Convert Adventure Component to legacy text properly
    TanChatUtils.message(
        player,
        Lang.DIPLOMACY_PROPOSAL_NEWSLETTER.get(
            player,
            LegacyComponentSerializer.legacySection()
                .serialize(proposingTerritory.getCustomColoredName()),
            LegacyComponentSerializer.legacySection()
                .serialize(receivingTerritory.getCustomColoredName()),
            wantedRelation.getColoredName(tanPlayer.getLang())),
        SoundEnum.MINOR_GOOD);
  }

  @Override
  public void broadcastConcerned(Player player) {
    broadcast(player);
  }

  @Override
  public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
    TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
    TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
    if (proposingTerritory == null || receivingTerritory == null) return null;

    ItemStack icon =
        HeadUtils.createCustomItemStack(
            Material.PAPER,
            Lang.NEWSLETTER_DIPLOMACY_PROPOSAL.get(lang, proposingTerritory.getBaseColoredName()),
            Lang.NEWSLETTER_DATE.get(
                lang, TimeZoneManager.getInstance().getRelativeTimeDescription(lang, getDate())),
            Lang.NEWSLETTER_DIPLOMACY_PROPOSAL_DESC1.get(
                lang, receivingTerritory.getBaseColoredName(), wantedRelation.getColoredName(lang)),
            Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get(lang));

    return ItemBuilder.from(icon)
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
    TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
    TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
    if (proposingTerritory == null || receivingTerritory == null) return null;

    ItemStack icon =
        HeadUtils.createCustomItemStack(
            Material.PAPER,
            Lang.NEWSLETTER_DIPLOMACY_PROPOSAL.get(lang, proposingTerritory.getBaseColoredName()),
            Lang.NEWSLETTER_DIPLOMACY_PROPOSAL_DESC1.get(
                lang, receivingTerritory.getBaseColoredName(), wantedRelation.getColoredName(lang)),
            Lang.NEWSLETTER_DIPLOMACY_PROPOSAL_DESC2.get(lang),
            Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get(lang));
    return ItemBuilder.from(icon)
        .asGuiItem(
            event -> {
              event.setCancelled(true);
              if (event.isLeftClick()) {
                if (receivingTerritory.doesPlayerHavePermission(
                    player, RolePermission.MANAGE_TOWN_RELATION)) {
                  TanChatUtils.message(
                      player, Lang.PLAYER_NO_PERMISSION.get(lang), SoundEnum.NOT_ALLOWED);
                  return;
                }
                OpenDiplomacyProposalsMenu.open(player, receivingTerritory);
              }
              if (event.isRightClick()) {
                markAsRead(player);
                onClick.accept(player);
              }
            });
  }

  @Override
  public boolean shouldShowToPlayer(Player player) {
    TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
    if (receivingTerritory == null) return false;
    TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
    if (proposingTerritory == null) return false;

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    return proposingTerritory.isPlayerIn(tanPlayer) || receivingTerritory.isPlayerIn(tanPlayer);
  }
}
