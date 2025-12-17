package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.DateUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.TanTerritory;

import java.util.UUID;
import java.util.function.Consumer;

public class WarEndedNewsletter extends Newsletter {

    private final String winningTerritoryID;
    private final String defeatedTerritoryID;
    private final int nbAppliedWargoals;

    public WarEndedNewsletter(TanTerritory winner, TanTerritory defeated, int nbAppliedWargoals) {
        this.winningTerritoryID = winner.getID();
        this.defeatedTerritoryID = defeated.getID();
        this.nbAppliedWargoals = nbAppliedWargoals;
    }

    public WarEndedNewsletter(UUID id, long date, String winningTerritoryID, String defeatedTerritoryID, int nbAppliedWargoals) {
        super(id, date);
        this.winningTerritoryID = winningTerritoryID;
        this.defeatedTerritoryID = defeatedTerritoryID;
        this.nbAppliedWargoals = nbAppliedWargoals;
    }

    @Override
    public GuiItem createGuiItem(Player player, LangType lang, Consumer<Player> onClick) {
        TerritoryData winningTerritory = TerritoryUtil.getTerritory(winningTerritoryID);
        TerritoryData defeatedTerritory = TerritoryUtil.getTerritory(defeatedTerritoryID);
        if(winningTerritory == null || defeatedTerritory == null)
            return null;

        ItemStack icon = HeadUtils.createCustomItemStack(Material.SHIELD,
                Lang.WAR_ENDED_TITLE.get(lang),
                Lang.NEWSLETTER_DATE.get(lang, DateUtil.getRelativeTimeDescription(lang, getDate())),
                Lang.WAR_ENDED.get(
                        lang,
                        winningTerritory.getBaseColoredName(),
                        defeatedTerritory.getBaseColoredName(),
                        Integer.toString(nbAppliedWargoals)
                ),
                Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.getDefault());

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
        return true;
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.WAR_ENDED;
    }

    @Override
    public void broadcast(Player player) {
        TerritoryData winningTerritory = TerritoryUtil.getTerritory(winningTerritoryID);
        TerritoryData defeatedTerritory = TerritoryUtil.getTerritory(defeatedTerritoryID);
        if(winningTerritory == null || defeatedTerritory == null)
            return;

        TanChatUtils.message(player,
                Lang.ATTACK_ENDED.get(
                        player,
                        winningTerritory.getBaseColoredName(),
                        defeatedTerritory.getBaseColoredName(),
                        Integer.toString(nbAppliedWargoals)
                ), SoundEnum.MINOR_LEVEL_UP);
    }

    @Override
    public void broadcastConcerned(Player player) {
        broadcast(player);
    }
}
