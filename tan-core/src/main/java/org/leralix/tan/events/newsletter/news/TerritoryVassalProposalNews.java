package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.TanTerritory;

public class TerritoryVassalProposalNews extends Newsletter {
  String proposingTerritoryID;
  String receivingTerritoryID;

  public TerritoryVassalProposalNews(
      TanTerritory proposingTerritory, TanTerritory receivingTerritory) {
    this(proposingTerritory.getID(), receivingTerritory.getID());
  }

  public TerritoryVassalProposalNews(String proposingTerritoryID, String receivingTerritoryID) {
    super();
    this.proposingTerritoryID = proposingTerritoryID;
    this.receivingTerritoryID = receivingTerritoryID;
  }

  public TerritoryVassalProposalNews(
      UUID id, long date, String proposingTerritoryID, String receivingTerritoryID) {
    super(id, date);
    this.proposingTerritoryID = proposingTerritoryID;
    this.receivingTerritoryID = receivingTerritoryID;
  }

  @Override
  public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
    TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
    TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
    if (proposingTerritory == null || receivingTerritory == null) return null;

    ItemStack icon =
        HeadUtils.createCustomItemStack(
            Material.GOLDEN_HELMET,
            Lang.NEWSLETTER_JOIN_REGION_PROPOSAL.get(lang),
            Lang.NEWSLETTER_DATE.get(
                lang, TimeZoneManager.getInstance().getRelativeTimeDescription(lang, getDate())),
            Lang.NEWSLETTER_JOIN_REGION_PROPOSAL_DESC1.get(
                lang,
                proposingTerritory.getBaseColoredName(),
                receivingTerritory.getBaseColoredName()),
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
            Material.GOLDEN_HELMET,
            Lang.NEWSLETTER_JOIN_REGION_PROPOSAL.get(lang),
            Lang.NEWSLETTER_JOIN_REGION_PROPOSAL_DESC1.get(
                lang,
                proposingTerritory.getBaseColoredName(),
                receivingTerritory.getBaseColoredName()),
            Lang.NEWSLETTER_JOIN_REGION_PROPOSAL_DESC2.get(lang),
            Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get(lang));

    return ItemBuilder.from(icon)
        .asGuiItem(
            event -> {
              event.setCancelled(true);
              if (event.isLeftClick()) {
                // TODO: Implement choose overlord menu after PlayerGUI migration
                // Original: PlayerGUI.openChooseOverlordMenu(player, receivingTerritory, 0);
                ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
              }
              if (event.isRightClick()) {
                markAsRead(player);
                onClick.accept(player);
              }
            });
  }

  @Override
  public boolean shouldShowToPlayer(Player player) {
    TerritoryData territoryData = TerritoryUtil.getTerritory(receivingTerritoryID);
    if (territoryData == null) return false;
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    if (!territoryData.isPlayerIn(tanPlayer)) return false;
    return territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.TOWN_ADMINISTRATOR);
  }

  @Override
  public NewsletterType getType() {
    return NewsletterType.TERRITORY_VASSAL_PROPOSAL;
  }

  @Override
  public void broadcast(Player player) {
    TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
    if (proposingTerritory == null) return;
    TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
    if (receivingTerritory == null) return;
    TanChatUtils.message(
        player,
        Lang.TOWN_JOIN_REGION_PROPOSAL_NEWSLETTER.get(
            player,
            proposingTerritory.getBaseColoredName(),
            receivingTerritory.getBaseColoredName()),
        SoundEnum.MINOR_BAD);
  }

  public String getProposingTerritoryID() {
    return proposingTerritoryID;
  }

  public String getReceivingTerritoryID() {
    return receivingTerritoryID;
  }

  @Override
  public void broadcastConcerned(Player player) {
    broadcast(player);
  }
}
