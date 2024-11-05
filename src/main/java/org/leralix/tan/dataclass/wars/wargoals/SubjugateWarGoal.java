package org.leralix.tan.dataclass.wars.wargoals;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.dataclass.wars.CreateAttackData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.TerritoryUtil;

import java.util.function.Consumer;

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
    public void addExtraOptions(Gui gui, Player player, CreateAttackData createAttackData, Consumer<Player> exit) {
        //Subjugate war goal does not have any extra options
    }

    @Override
    public void applyWarGoal() {
        ITerritoryData territoryData = TerritoryUtil.getTerritory(territoryToSubjugate);
        ITerritoryData newOverlord = TerritoryUtil.getTerritory(newOverlordID);
        if(territoryData == null || newOverlord == null)
            return;

        if(territoryData.haveOverlord()){
            territoryData.getOverlord().removeVassal(territoryData);
            territoryData.removeOverlord();
        }
        territoryData.setOverlord(newOverlord);
        newOverlord.addVassal(territoryData);

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

        ITerritoryData loosingTerritory = TerritoryUtil.getTerritory(territoryToSubjugate);
        ITerritoryData winningTerritory = TerritoryUtil.getTerritory(newOverlordID);
        if(loosingTerritory == null || winningTerritory == null)
            return;
        player.sendMessage(Lang.WARGOAL_SUBJUGATE_SUCCESS.get(loosingTerritory.getColoredName(), winningTerritory.getColoredName()));
    }

    @Override
    public void sendAttackSuccessToDefenders(Player player) {
        sendAttackSuccessToAttackers(player);
    }

}
