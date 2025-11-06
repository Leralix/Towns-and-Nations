package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Material;
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

public class AttackWonByDefenderNewsletter extends Newsletter {

  private final String attackingTerritoryID;
  private final String defendingTerritoryID;

  public AttackWonByDefenderNewsletter(
      TanTerritory defendingTerritoryID, TanTerritory attackingTerritoryID) {
    super();
    this.defendingTerritoryID = defendingTerritoryID.getID();
    this.attackingTerritoryID = attackingTerritoryID.getID();
  }

  public AttackWonByDefenderNewsletter(
      UUID id, long date, String attackingTerritoryID, String defendingTerritoryID) {
    super(id, date);
    this.attackingTerritoryID = attackingTerritoryID;
    this.defendingTerritoryID = defendingTerritoryID;
  }

  public String getAttackingTerritoryID() {
    return attackingTerritoryID;
  }

  public String getDefendingTerritoryID() {
    return defendingTerritoryID;
  }

  @Override
  public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
    TerritoryData attackingTerritory = TerritoryUtil.getTerritory(attackingTerritoryID);
    TerritoryData defendingTerritory = TerritoryUtil.getTerritory(defendingTerritoryID);
    if (attackingTerritory == null || defendingTerritory == null) return null;

    ItemStack icon =
        HeadUtils.createCustomItemStack(
            Material.IRON_SWORD,
            Lang.ATTACK_WON_BY_DEFENDER_TITLE.get(lang),
            Lang.NEWSLETTER_DATE.get(
                lang, TimeZoneManager.getInstance().getRelativeTimeDescription(lang, getDate())),
            Lang.ATTACK_WON_BY_DEFENDER.get(
                lang,
                attackingTerritory.getBaseColoredName(),
                defendingTerritory.getBaseColoredName(),
                defendingTerritory.getBaseColoredName()),
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
    TerritoryData attackingTerritory = TerritoryUtil.getTerritory(attackingTerritoryID);
    if (attackingTerritory == null) return false;
    TerritoryData defendingTerritory = TerritoryUtil.getTerritory(defendingTerritoryID);
    if (defendingTerritory == null) return false;
    return attackingTerritory.isPlayerIn(player) || defendingTerritory.isPlayerIn(player);
  }

  @Override
  public NewsletterType getType() {
    return NewsletterType.ATTACK_WON_BY_DEFENDER;
  }

  @Override
  public void broadcast(Player player) {
    TerritoryData attackingTerritory = TerritoryUtil.getTerritory(attackingTerritoryID);
    TerritoryData defendingTerritory = TerritoryUtil.getTerritory(defendingTerritoryID);
    if (attackingTerritory == null || defendingTerritory == null) return;

    TanChatUtils.message(
        player,
        Lang.ATTACK_WON_BY_ATTACKER.get(
            player,
            attackingTerritory.getBaseColoredName(),
            defendingTerritory.getBaseColoredName(),
            defendingTerritory.getBaseColoredName()),
        SoundEnum.MINOR_GOOD);
  }

  @Override
  public void broadcastConcerned(Player player) {
    broadcast(player);
  }
}
