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
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.TerritoryUtil;
import org.tan.api.interfaces.TanTerritory;

import java.util.UUID;
import java.util.function.Consumer;

import static org.leralix.tan.utils.TanChatUtils.getTANString;

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
        if(attackingTerritory == null || defendingTerritory == null)
            return null;

        LangType langType = PlayerDataStorage.getInstance().get(player).getLang();

        ItemStack icon = HeadUtils.createCustomItemStack(Material.IRON_SWORD,
                Lang.ATTACK_CANCELLED_TITLE.get(langType),
                Lang.NEWSLETTER_DATE.get(langType, TimeZoneManager.getInstance().getRelativeTimeDescription(langType, getDate())),
                Lang.ATTACK_CANCELLED.get(langType, attackingTerritory.getBaseColoredName(), defendingTerritory.getBaseColoredName()),
                Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get(langType));

        return ItemBuilder.from(icon).asGuiItem(event -> {
            event.setCancelled(true);
            if(event.isRightClick()){
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
    public void broadcast(Player player) {
        TerritoryData attackingTerritory = TerritoryUtil.getTerritory(attackingTerritoryID);
        TerritoryData defendingTerritory = TerritoryUtil.getTerritory(defendingTerritoryID);
        if(attackingTerritory == null || defendingTerritory == null)
            return;

        player.sendMessage(getTANString() + Lang.ATTACK_CANCELLED.get(attackingTerritory.getBaseColoredName(), defendingTerritory.getBaseColoredName(), defendingTerritory.getBaseColoredName()));
        SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
    }

    @Override
    public void broadcastConcerned(Player player) {
        broadcast(player);
    }
}
