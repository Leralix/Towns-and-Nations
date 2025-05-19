package org.leralix.tan.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.newsletter.NewsletterType;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.TerritoryUtil;

import java.util.function.Consumer;

import static org.leralix.tan.utils.TanChatUtils.getTANString;

public class AttackWonByDefenderNewsletter extends Newsletter {

    private final String attackingTerritoryID;
    private final String defendingTerritoryID;

    public AttackWonByDefenderNewsletter(String defendingTerritoryID, String attackingTerritoryID) {
        super();
        this.defendingTerritoryID = defendingTerritoryID;
        this.attackingTerritoryID = attackingTerritoryID;
    }

    @Override
    public GuiItem createGuiItem(Player player, Consumer<Player> onClick) {
        TerritoryData attackingTerritory = TerritoryUtil.getTerritory(attackingTerritoryID);
        TerritoryData defendingTerritory = TerritoryUtil.getTerritory(defendingTerritoryID);
        if(attackingTerritory == null || defendingTerritory == null)
            return null;

        ItemStack icon = HeadUtils.createCustomItemStack(Material.IRON_SWORD,
                Lang.ATTACK_WON_BY_DEFENDER_TITLE.get(),
                Lang.ATTACK_WON_BY_DEFENDER.get(attackingTerritory.getBaseColoredName(), defendingTerritory.getBaseColoredName(), defendingTerritory.getBaseColoredName()),
                Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get());

        return ItemBuilder.from(icon).asGuiItem(event -> {
            event.setCancelled(true);
            if(event.isRightClick()){
                markAsRead(player);
                onClick.accept(player);
            }
        });
    }

    @Override
    public GuiItem createConcernedGuiItem(Player player, Consumer<Player> onClick) {
        return createGuiItem(player, onClick);
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
        return NewsletterType.ATTACK_WON_BY_DEFENDER;
    }

    @Override
    public void broadcast(Player player) {
        TerritoryData attackingTerritory = TerritoryUtil.getTerritory(attackingTerritoryID);
        TerritoryData defendingTerritory = TerritoryUtil.getTerritory(defendingTerritoryID);
        if(attackingTerritory == null || defendingTerritory == null)
            return;

        player.sendMessage(getTANString() + Lang.ATTACK_WON_BY_ATTACKER.get(attackingTerritory.getBaseColoredName(), defendingTerritory.getBaseColoredName(), defendingTerritory.getBaseColoredName()));
        SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
    }
}
