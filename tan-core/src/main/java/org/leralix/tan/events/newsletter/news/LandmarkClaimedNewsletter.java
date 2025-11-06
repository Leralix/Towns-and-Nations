package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.tan.api.interfaces.TanLandmark;
import org.tan.api.interfaces.TanTerritory;

public class LandmarkClaimedNewsletter extends Newsletter {

  private final String landmarkID;
  private final String newOwnerID;

  public LandmarkClaimedNewsletter(UUID id, long date, String landmarkID, String newOwnerID) {
    super(id, date);
    this.landmarkID = landmarkID;
    this.newOwnerID = newOwnerID;
  }

  public LandmarkClaimedNewsletter(TanLandmark landmark, TanTerritory newOwner) {
    this.landmarkID = landmark.getID();
    this.newOwnerID = newOwner.getID();
  }

  public String getLandmarkID() {
    return landmarkID;
  }

  public String getNewOwnerID() {
    return newOwnerID;
  }

  @Override
  public GuiItem createGuiItem(Player player, LangType langType, Consumer<Player> onClick) {
    TerritoryData newOwner = TerritoryUtil.getTerritory(newOwnerID);
    Landmark landmark = LandmarkStorage.getInstance().getSync(landmarkID);

    if (landmark == null || newOwner == null) {
      return null;
    }

    return IconManager.getInstance()
        .get(Material.CHEST)
        .setName(Lang.LANDMARK_CLAIMED_NEWSLETTER_TITLE.get(langType))
        .setDescription(
            Lang.LANDMARK_CLAIMED_NEWSLETTER.get(newOwner.getColoredName(), landmark.getName()))
        .setAction(
            event -> {
              event.setCancelled(true);
              if (event.isRightClick()) {
                markAsRead(player);
                onClick.accept(player);
              }
            })
        .asGuiItem(player, langType);
  }

  @Override
  public GuiItem createConcernedGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
    return createGuiItem(player, lang, onClick);
  }

  @Override
  public boolean shouldShowToPlayer(Player player) {
    TerritoryData territoryData = TerritoryUtil.getTerritory(newOwnerID);
    if (territoryData == null) return false;
    return territoryData.isPlayerIn(player);
  }

  @Override
  public NewsletterType getType() {
    return NewsletterType.LANDMARK_CLAIMED;
  }

  @Override
  public void broadcast(Player player) {
    TerritoryData newOwner = TerritoryUtil.getTerritory(newOwnerID);
    Landmark landmark = LandmarkStorage.getInstance().getSync(landmarkID);
    Lang.LANDMARK_CLAIMED_NEWSLETTER.get(player, newOwner.getColoredName(), landmark.getName());
  }

  @Override
  public void broadcastConcerned(Player player) {
    broadcast(player);
  }
}
