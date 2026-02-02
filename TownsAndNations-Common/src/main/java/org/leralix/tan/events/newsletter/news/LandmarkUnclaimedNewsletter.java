package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.DateUtil;
import org.tan.api.interfaces.TanLandmark;
import org.tan.api.interfaces.TanTerritory;

import java.util.UUID;
import java.util.function.Consumer;

public class LandmarkUnclaimedNewsletter extends Newsletter {

    private final String landmarkID;
    private final String oldOwnerID;


    public LandmarkUnclaimedNewsletter(UUID id, long date, String landmarkID, String oldOwnerID) {
        super(id, date);
        this.landmarkID = landmarkID;
        this.oldOwnerID = oldOwnerID;
    }


    public LandmarkUnclaimedNewsletter(TanLandmark landmark, TanTerritory oldOwner) {
        super();
        this.landmarkID = landmark.getID();
        this.oldOwnerID = oldOwner.getID();
    }

    public String getLandmarkID() {
        return landmarkID;
    }

    public String getOldOwnerID() {
        return oldOwnerID;
    }

    @Override
    public GuiItem createGuiItem(Player player, LangType langType, Consumer<Player> onClick) {
        TerritoryData oldOwner = TerritoryUtil.getTerritory(oldOwnerID);
        Landmark landmark = LandmarkStorage.getInstance().get(landmarkID);

        if (landmark == null || oldOwner == null) {
            return null;
        }

        return IconManager.getInstance().get(Material.CHEST)
                .setName(Lang.LANDMARK_UNCLAIMED_NEWSLETTER_TITLE.get(langType))
                .setDescription(
                        Lang.NEWSLETTER_DATE.get(DateUtil.getRelativeTimeDescription(langType, getDate())),
                        Lang.LANDMARK_UNCLAIMED_NEWSLETTER.get(oldOwner.getColoredName(), landmark.getName())
                )
                .setAction(event -> {
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
        TerritoryData territoryData = TerritoryUtil.getTerritory(oldOwnerID);
        if (territoryData == null) return false;
        return territoryData.isPlayerIn(player);
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.LANDMARK_UNCLAIMED;
    }

    @Override
    public void broadcast(Player player, ITanPlayer tanPlayer) {
        TerritoryData oldOwner = TerritoryUtil.getTerritory(oldOwnerID);
        Landmark landmark = LandmarkStorage.getInstance().get(landmarkID);
        Lang.LANDMARK_UNCLAIMED_NEWSLETTER.get(tanPlayer, oldOwner.getColoredName(), landmark.getName());
    }
}
