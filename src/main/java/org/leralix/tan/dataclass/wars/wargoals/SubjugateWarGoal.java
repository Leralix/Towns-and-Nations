package org.leralix.tan.dataclass.wars.wargoals;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.wars.CreateAttackData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.newsletter.news.TerritoryVassalForcedNews;
import org.leralix.tan.newsletter.storage.NewsletterStorage;
import org.leralix.tan.utils.TerritoryUtil;

public class SubjugateWarGoal extends WarGoal {


    final String territoryToSubjugate;
    final String newOverlordID;

    public SubjugateWarGoal(CreateAttackData createAttackData){
        super();
        this.territoryToSubjugate = createAttackData.getMainDefender().getID();
        this.newOverlordID = createAttackData.getMainAttacker().getID();
    }

    @Override
    public ItemStack getIcon() {
        return buildIcon(Material.CHAIN, Lang.SUBJUGATE_WAR_GOAL_DESC.get());
    }

    @Override
    public String getDisplayName() {
        return Lang.SUBJUGATE_WAR_GOAL.get();
    }

    @Override
    public void addExtraOptions(Gui gui, Player player, CreateAttackData createAttackData) {
        //Subjugate war goal does not have any extra options
    }

    @Override
    public void applyWarGoal() {
        TerritoryData territoryData = TerritoryUtil.getTerritory(territoryToSubjugate);
        TerritoryData newOverlord = TerritoryUtil.getTerritory(newOverlordID);
        if(territoryData == null || newOverlord == null)
            return;

        if(territoryData.haveOverlord()){
            territoryData.removeOverlord();
        }
        territoryData.setOverlord(newOverlord);

        NewsletterStorage.register(new TerritoryVassalForcedNews(
                territoryData,
                newOverlord
        ));

    }

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public String getCurrentDesc() {
        return Lang.GUI_WARGOAL_SUBJUGATE_WAR_GOAL_RESULT.get();
    }

    @Override
    public void sendAttackSuccessToAttackers(Player player) {
        super.sendAttackSuccessToAttackers(player);

        TerritoryData loosingTerritory = TerritoryUtil.getTerritory(territoryToSubjugate);
        TerritoryData winningTerritory = TerritoryUtil.getTerritory(newOverlordID);
        if(loosingTerritory == null || winningTerritory == null)
            return;
        player.sendMessage(Lang.WARGOAL_SUBJUGATE_SUCCESS.get(loosingTerritory.getBaseColoredName(), winningTerritory.getBaseColoredName()));
    }

    @Override
    public void sendAttackSuccessToDefenders(Player player) {
        sendAttackSuccessToAttackers(player);
    }

}
