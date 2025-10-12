package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.TanTerritory;

import java.util.UUID;
import java.util.function.Consumer;



public class TerritoryIndependentNews extends Newsletter {

    private final String formerMasterID;
    private final String independentTerritoryID;

    public TerritoryIndependentNews(TanTerritory independentTerritoryID, TanTerritory formerMasterID) {
        super();
        this.independentTerritoryID = independentTerritoryID.getID();
        this.formerMasterID = formerMasterID.getID();
    }

    public TerritoryIndependentNews(UUID id, long date, String independentTerritoryID, String formerMasterID) {
        super(id, date);
        this.independentTerritoryID = independentTerritoryID;
        this.formerMasterID = formerMasterID;
    }

    public String getFormerMasterID() {
        return formerMasterID;
    }

    public String getIndependentTerritoryID() {
        return independentTerritoryID;
    }

    @Override
    public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
        TerritoryData leavingTown = TerritoryUtil.getTerritory(formerMasterID);
        TerritoryData region = TerritoryUtil.getTerritory(independentTerritoryID);
        if (leavingTown == null || region == null)
            return null;

        ItemStack icon = HeadUtils.createCustomItemStack(Material.GOLDEN_HELMET,
                Lang.TOWN_LEAVE_REGION_NEWSLETTER_TITLE.get(lang),
                Lang.NEWSLETTER_DATE.get(lang, TimeZoneManager.getInstance().getRelativeTimeDescription(lang, getDate())),
                Lang.TOWN_LEAVE_REGION_NEWSLETTER.get(lang, leavingTown.getBaseColoredName(), region.getBaseColoredName()),
                Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get(lang));

        return ItemBuilder.from(icon).asGuiItem(event -> {
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
        TerritoryData leavingTown = TerritoryUtil.getTerritory(formerMasterID);
        if (leavingTown == null)
            return false;
        TerritoryData region = TerritoryUtil.getTerritory(independentTerritoryID);
        if (region == null)
            return false;
        return leavingTown.isPlayerIn(player) || region.isPlayerIn(player);
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.TERRITORY_VASSAL_INDEPENDENT;
    }

    @Override
    public void broadcast(Player player) {
        TerritoryData leavingTown = TerritoryUtil.getTerritory(formerMasterID);
        if (leavingTown == null)
            return;
        TerritoryData region = TerritoryUtil.getTerritory(independentTerritoryID);
        if (region == null)
            return;

        TanChatUtils.message(player, Lang.TOWN_LEAVE_REGION_NEWSLETTER.get(player, leavingTown.getBaseColoredName(), region.getBaseColoredName()), SoundEnum.MINOR_GOOD);
    }

    @Override
    public void broadcastConcerned(Player player) {
        broadcast(player);
    }
}
