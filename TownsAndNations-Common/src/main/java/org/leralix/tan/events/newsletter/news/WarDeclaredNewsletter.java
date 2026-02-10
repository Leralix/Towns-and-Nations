package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.DateUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.territory.TanTerritory;

import java.util.UUID;
import java.util.function.Consumer;


public class WarDeclaredNewsletter extends Newsletter {

    private final String attackingTerritoryID;
    private final String defendingTerritoryID;

    public WarDeclaredNewsletter(TanTerritory defendingTerritoryID, TanTerritory attackingTerritoryID) {
        super();
        this.defendingTerritoryID = defendingTerritoryID.getID();
        this.attackingTerritoryID = attackingTerritoryID.getID();
    }

    public WarDeclaredNewsletter(UUID id, long date, String attackingTerritoryID, String defendingTerritoryID) {
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
        if (attackingTerritory == null || defendingTerritory == null)
            return null;

        return IconManager.getInstance().get(Material.IRON_SWORD)
                .setName(Lang.WAR_DECLARED_TITLE.get(lang))
                .setDescription(
                        Lang.NEWSLETTER_DATE.get(DateUtil.getRelativeTimeDescription(lang, getDate())),
                        Lang.WAR_DECLARED.get(attackingTerritory.getColoredName(), defendingTerritory.getColoredName())
                )
                .setClickToAcceptMessage(Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ)
                .setAction(action -> {
                    action.setCancelled(true);
                    if (action.isRightClick()) {
                        markAsRead(player);
                        onClick.accept(player);
                    }
                })
                .asGuiItem(player, lang);
    }

    @Override
    public GuiItem createConcernedGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
        return createGuiItem(player, lang, onClick);
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        TerritoryData attackingTerritory = TerritoryUtil.getTerritory(attackingTerritoryID);
        if (attackingTerritory == null)
            return false;
        TerritoryData defendingTerritory = TerritoryUtil.getTerritory(defendingTerritoryID);
        if (defendingTerritory == null)
            return false;
        return attackingTerritory.isPlayerIn(player) || defendingTerritory.isPlayerIn(player);
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.WAR_DECLARED;
    }

    @Override
    public void broadcast(Player player, ITanPlayer tanPlayer) {
        TerritoryData attackingTerritory = TerritoryUtil.getTerritory(attackingTerritoryID);
        TerritoryData defendingTerritory = TerritoryUtil.getTerritory(defendingTerritoryID);
        if (attackingTerritory == null || defendingTerritory == null)
            return;

        TanChatUtils.message(player, Lang.WAR_DECLARED.get(tanPlayer, attackingTerritory.getColoredName(), defendingTerritory.getColoredName()), SoundEnum.WAR);
    }
}
