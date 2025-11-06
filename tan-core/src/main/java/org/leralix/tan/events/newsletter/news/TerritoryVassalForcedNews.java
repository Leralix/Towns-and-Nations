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

public class TerritoryVassalForcedNews extends Newsletter {
  private final String proposingTerritoryID;
  private final String forcedTerritoryID;

  public TerritoryVassalForcedNews(
      TanTerritory proposingTerritory, TanTerritory receivingTerritory) {
    this(proposingTerritory.getID(), receivingTerritory.getID());
  }

  public TerritoryVassalForcedNews(String proposingTerritoryID, String forcedTerritoryID) {
    super();
    this.proposingTerritoryID = proposingTerritoryID;
    this.forcedTerritoryID = forcedTerritoryID;
  }

  public TerritoryVassalForcedNews(
      UUID id, long date, String proposingTerritoryID, String forcedTerritoryID) {
    super(id, date);
    this.proposingTerritoryID = proposingTerritoryID;
    this.forcedTerritoryID = forcedTerritoryID;
  }

  @Override
  public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
    TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
    TerritoryData forcedTerritory = TerritoryUtil.getTerritory(forcedTerritoryID);
    if (proposingTerritory == null || forcedTerritory == null) return null;

    ItemStack icon =
        HeadUtils.createCustomItemStack(
            Material.GOLDEN_HELMET,
            Lang.FORCED_VASSALAGE_TITLE.get(lang),
            Lang.NEWSLETTER_DATE.get(
                lang, TimeZoneManager.getInstance().getRelativeTimeDescription(lang, getDate())),
            Lang.FORCED_VASSALAGE.get(
                lang,
                proposingTerritory.getBaseColoredName(),
                forcedTerritory.getBaseColoredName()),
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
    return createGuiItem(player, lang, onClick);
  }

  @Override
  public boolean shouldShowToPlayer(Player player) {
    TerritoryData territoryData = TerritoryUtil.getTerritory(forcedTerritoryID);
    if (territoryData == null) return false;
    TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
    if (proposingTerritory == null) return false;
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    if (!territoryData.isPlayerIn(tanPlayer)) return false;
    return territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.TOWN_ADMINISTRATOR)
        || proposingTerritory.doesPlayerHavePermission(
            tanPlayer, RolePermission.TOWN_ADMINISTRATOR);
  }

  @Override
  public NewsletterType getType() {
    return NewsletterType.TERRITORY_VASSAL_FORCED;
  }

  @Override
  public void broadcast(Player player) {
    TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
    if (proposingTerritory == null) return;
    TerritoryData receivingTerritory = TerritoryUtil.getTerritory(forcedTerritoryID);
    if (receivingTerritory == null) return;
    TanChatUtils.message(
        player,
        Lang.FORCED_VASSALAGE.get(
            player,
            receivingTerritory.getBaseColoredName(),
            proposingTerritory.getBaseColoredName()),
        SoundEnum.MINOR_BAD);
  }

  public String getProposingTerritoryID() {
    return proposingTerritoryID;
  }

  public String getForcedTerritoryID() {
    return forcedTerritoryID;
  }

  @Override
  public void broadcastConcerned(Player player) {
    broadcast(player);
  }
}
