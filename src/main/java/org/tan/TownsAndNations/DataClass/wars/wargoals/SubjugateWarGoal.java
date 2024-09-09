package org.tan.TownsAndNations.DataClass.wars.wargoals;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.DataClass.wars.CreateAttackData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.utils.TerritoryUtil;

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

    }

    @Override
    public void applyWarGoal() {
        ITerritoryData territoryData = TerritoryUtil.getTerritory(territoryToSubjugate);
        ITerritoryData newOverlord = TerritoryUtil.getTerritory(newOverlordID);
        if(territoryData == null || newOverlord == null)
            return;

        if(territoryData.haveOverlord()){
            territoryData.getOverlord().removeSubject(territoryData);
            territoryData.removeOverlord();
        }
        territoryData.setOverlord(newOverlord);
        newOverlord.addSubject(territoryData);

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
    public void sendWinMessageForWinner(Player player) {
        super.sendWinMessageForWinner(player);

        ITerritoryData loosingTerritory = TerritoryUtil.getTerritory(territoryToSubjugate);
        ITerritoryData winningTerritory = TerritoryUtil.getTerritory(newOverlordID);
        if(loosingTerritory == null || winningTerritory == null)
            return;
        player.sendMessage(Lang.WARGOAL_SUBJUGATE_SUCCESS.get(loosingTerritory.getColoredName(), winningTerritory.getColoredName()));
    }

    @Override
    public void sendWinMessageForLooser(Player player) {
        sendWinMessageForWinner(player);
    }

}
