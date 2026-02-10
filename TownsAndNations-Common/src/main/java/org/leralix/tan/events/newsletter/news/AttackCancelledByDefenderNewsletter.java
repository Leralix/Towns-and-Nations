package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.territory.TanTerritory;

import java.util.UUID;
import java.util.function.Consumer;


public class AttackCancelledByDefenderNewsletter extends Newsletter {

    private final String attackingTerritoryID;
    private final String defendingTerritoryID;

    public AttackCancelledByDefenderNewsletter(TanTerritory defendingTerritory, TanTerritory attackingTerritory) {
        super();
        this.defendingTerritoryID = defendingTerritory.getID();
        this.attackingTerritoryID = attackingTerritory.getID();
    }

    public AttackCancelledByDefenderNewsletter(UUID id, long date, String attackingTerritoryID, String defendingTerritoryID) {
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

        return createBasicNewsletter(
                Material.IRON_SWORD,
                Lang.ATTACK_CANCELLED_TITLE.get(),
                Lang.ATTACK_CANCELLED.get(attackingTerritory.getColoredName(), defendingTerritory.getColoredName()),
                lang,
                onClick,
                player
        );
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
        return NewsletterType.ATTACK_CANCELLED;
    }

    @Override
    public void broadcast(Player player, ITanPlayer tanPlayer) {
        TerritoryData attackingTerritory = TerritoryUtil.getTerritory(attackingTerritoryID);
        TerritoryData defendingTerritory = TerritoryUtil.getTerritory(defendingTerritoryID);
        if (attackingTerritory == null || defendingTerritory == null)
            return;
        TanChatUtils.message(player, Lang.DEFENSIVE_SIDE_HAS_SURRENDER.get(
                defendingTerritory.getColoredName(),
                attackingTerritory.getColoredName()
        ), SoundEnum.WAR);
    }
}
