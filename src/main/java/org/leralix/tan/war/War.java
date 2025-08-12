package org.leralix.tan.war;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.CurrentWarStorage;
import org.leralix.tan.utils.TerritoryUtil;
import org.leralix.tan.war.legacy.WarRole;
import org.leralix.tan.war.legacy.wargoals.WarGoal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class War {

    private final String ID;
    private String name;
    private final String mainDefenderID;
    private final String mainAttackerID;

    private final List<WarGoal> attackGoals;
    private final List<WarGoal> defenseGoals;

    public War(String ID, TerritoryData mainDefender, TerritoryData mainAttacker) {
        this.ID = ID;
        this.name = "War " + ID;
        this.mainDefenderID = mainDefender.getID();
        this.mainAttackerID = mainAttacker.getID();
        this.attackGoals = new ArrayList<>();
        this.defenseGoals = new ArrayList<>();
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainDefenderID() {
        return mainDefenderID;
    }

    public TerritoryData getMainDefender() {
        return TerritoryUtil.getTerritory(mainDefenderID);
    }

    public String getMainAttackerID() {
        return mainAttackerID;
    }

    public TerritoryData getMainAttacker() {
        return TerritoryUtil.getTerritory(mainAttackerID);
    }

    public boolean isMainAttacker(TerritoryData territoryData) {
        return territoryData.getID().equals(mainAttackerID);
    }

    public boolean isMainDefender(TerritoryData territoryData) {
        return territoryData.getID().equals(mainDefenderID);
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.IRON_SWORD);
    }

    public void territorySurrender(WarRole winningSide) {

        TerritoryData winner = getTerritory(winningSide);
        TerritoryData looser = getEnemyTerritory(winningSide);

        for(WarGoal goal : getGoals(winningSide.opposite())) {
            goal.applyWarGoal(winner, looser);
        }

        endWar();
    }

    public void endWar() {
        getMainAttacker().setRelation(getMainDefender(), TownRelation.NEUTRAL);
        for(PlannedAttack plannedAttack : CurrentWarStorage.getWars()){
            if(plannedAttack.getWar().getID().equals(getID())){
                plannedAttack.end();
            }
        }
        WarStorage.getInstance().remove(this);
    }

    public List<WarGoal> getGoals(WarRole warRole) {
        if (warRole == WarRole.MAIN_ATTACKER) {
            return attackGoals;
        } else if (warRole == WarRole.MAIN_DEFENDER) {
            return defenseGoals;
        }
        return Collections.emptyList();
    }

    public void removeGoal(WarRole warRole, WarGoal goal) {
        if (warRole == WarRole.MAIN_ATTACKER) {
            attackGoals.remove(goal);
        } else if (warRole == WarRole.MAIN_DEFENDER) {
            defenseGoals.remove(goal);
        }
    }

    public void addGoal(WarRole warRole, WarGoal conquerWarGoal) {
        if (warRole == WarRole.MAIN_ATTACKER) {
            attackGoals.add(conquerWarGoal);
        } else if (warRole == WarRole.MAIN_DEFENDER) {
            defenseGoals.add(conquerWarGoal);
        }
    }

    public TerritoryData getTerritory(WarRole warRole) {
        if(warRole == WarRole.MAIN_ATTACKER){
            return getMainAttacker();
        }
        if(warRole == WarRole.MAIN_DEFENDER){
            return getMainDefender();
        }
        throw new IllegalArgumentException(warRole + " is not authorized");
    }

    public TerritoryData getEnemyTerritory(WarRole warRole) {
        if(warRole == WarRole.MAIN_ATTACKER){
            return getMainDefender();
        }
        if(warRole == WarRole.MAIN_DEFENDER){
            return getMainAttacker();
        }
        throw new IllegalArgumentException(warRole + " is not authorized");
    }

    public Collection<String> generateWarGoalsDesciption(WarRole warRole, LangType langType) {
        List<WarGoal> goals = getGoals(warRole.opposite());
        List<String> goalsToString = new ArrayList<>();
        for(WarGoal goal : goals) {
            goalsToString.add(Lang.WAR_GOAL_LIST_BUTTON_LIST.get(langType, goal.getCurrentDesc()));
        }

        // If no goals are set, add a message
        if(goalsToString.isEmpty()) {
            goalsToString.add(Lang.WAR_GOAL_LIST_BUTTON_LIST_NO_WAR_GOAL_SET.get(langType));
        }
        return goalsToString;

    }
}