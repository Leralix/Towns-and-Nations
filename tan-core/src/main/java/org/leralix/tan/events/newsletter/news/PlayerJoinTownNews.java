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
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.RolePermission;
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

public class PlayerJoinTownNews extends Newsletter {

  String playerID;
  String townID;

  public PlayerJoinTownNews(TanPlayer tanPlayer, TanTown townData) {
    super();
    playerID = tanPlayer.getUUID().toString();
    townID = townData.getID();
  }

  public PlayerJoinTownNews(UUID id, long date, String playerID, String townID) {
    super(id, date);
    this.playerID = playerID;
    this.townID = townID;
  }

  @Override
  public NewsletterType getType() {
    return NewsletterType.PLAYER_JOIN_TOWN;
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
        Lang.PLAYER_JOINED_TOWN_NEWSLETTER.get(
            player, tanPlayer.getNameStored(), townData.getBaseColoredName()),
        SoundEnum.MINOR_GOOD);
  }

  @Override
  public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(playerID);
    if (tanPlayer == null) return null;
    TownData townData = TownDataStorage.getInstance().getSync(townID);
    if (townData == null) return null;

    // BUGFIX: Convert Adventure Component to legacy text properly
    ItemStack itemStack =
        HeadUtils.makeSkullURL(
            Lang.PLAYER_JOINED_TOWN_NEWSLETTER_TITLE.get(lang),
            "http://textures.minecraft.net/texture/16338322d26c6a7c08fb9fd22959a136728fa2d4dccd22b1563eb1bbaa1d5471",
            Lang.NEWSLETTER_DATE.get(
                lang, TimeZoneManager.getInstance().getRelativeTimeDescription(lang, getDate())),
            Lang.PLAYER_JOINED_TOWN_NEWSLETTER.get(
                lang,
                tanPlayer.getNameStored(),
                LegacyComponentSerializer.legacySection()
                    .serialize(townData.getCustomColoredName())),
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
    TownData townData = TownDataStorage.getInstance().getSync(townID);
    if (townData == null) return false;
    return townData.doesPlayerHavePermission(player, RolePermission.INVITE_PLAYER);
  }

  @Override
  public void broadcastConcerned(Player player) {
    broadcast(player);
  }
}
