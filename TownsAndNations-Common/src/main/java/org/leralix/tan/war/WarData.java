package org.leralix.tan.war;

import org.bukkit.Material;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.WarEndInternalEvent;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.gui.cosmetic.type.ItemIconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.war.capture.CaptureManager;
import org.leralix.tan.war.info.AttackResultCancelled;
import org.leralix.tan.war.info.WarRole;
import org.leralix.tan.war.wargoals.WarGoal;

import java.util.*;

public class WarData implements War{

    private final String ID;
    private String name;
    private final String mainDefenderID;
    private final String mainAttackerID;

    private Collection<String> allDefendersID;
    private Collection<String> allAttackersID;

    private final List<WarGoal> attackGoals;
    private final List<WarGoal> defenseGoals;

    private HashMap<String, PlannedAttack> plannedAttacks;

    public WarData(String id, Territory mainAttacker, Territory mainDefender) {
        this.ID = id;
        this.name = Lang.BASIC_ATTACK_NAME.get(
                Lang.getServerLang(),
                mainAttacker.getName(),
                mainDefender.getName()
        );
        this.mainDefenderID = mainDefender.getID();
        this.mainAttackerID = mainAttacker.getID();
        this.allDefendersID = new ArrayList<>(mainDefender.getRelations().getTerritoriesIDWithRelation(TownRelation.ALLIANCE));
        this.allDefendersID.add(mainDefenderID);
        this.allAttackersID = new ArrayList<>();
        this.allAttackersID.add(mainAttackerID);
        this.attackGoals = new ArrayList<>();
        this.defenseGoals = new ArrayList<>();
        this.plannedAttacks = new HashMap<>();
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getMainDefenderID() {
        return mainDefenderID;
    }

    @Override
    public Territory getMainDefender() {
        return TerritoryUtil.getTerritory(mainDefenderID);
    }

    @Override
    public String getMainAttackerID() {
        return mainAttackerID;
    }

    @Override
    public Territory getMainAttacker() {
        return TerritoryUtil.getTerritory(mainAttackerID);
    }

    @Override
    public boolean isMainAttacker(Territory territory) {
        return territory.getID().equals(mainAttackerID);
    }

    @Override
    public boolean isMainDefender(Territory territory) {
        return territory.getID().equals(mainDefenderID);
    }

    @Override
    public IconBuilder getIcon() {
        return new IconBuilder(new ItemIconBuilder(Material.IRON_SWORD))
                .setName(getName())
                .setDescription(
                        Lang.ATTACK_ICON_DESC_1.get(getMainAttacker().getColoredName()),
                        Lang.ATTACK_ICON_DESC_2.get(getMainDefender().getColoredName())
                );
    }

    @Override
    public void territorySurrender(WarRole looserTerritory) {

        Territory looser = getTerritory(looserTerritory);
        Territory winner = getTerritory(looserTerritory.opposite());

        // All chunks captured due to the war are now released
        CaptureManager.getInstance().removeCapture(this);

        List<WarGoal> goals = getGoals(looserTerritory.opposite());

        for (WarGoal goal : goals) {
            goal.applyWarGoal(winner, looser);
        }

        EventManager.getInstance().callEvent(new WarEndInternalEvent(winner, looser, goals));

        endWar();
    }

    @Override
    public void endWar() {
        TerritoryUtil.setRelation(getMainAttacker(), getMainDefender(), Constants.getRelationAfterSurrender());
        for (PlannedAttack plannedAttack : getPlannedAttacks()) {
            plannedAttack.end(new AttackResultCancelled());
        }
        TownsAndNations.getPlugin().getWarStorage().remove(this);
    }

    @Override
    public Collection<PlannedAttack> getPlannedAttacks() {
        return getPlannedAttacksMap().values();
    }

    @Override
    public Map<String, PlannedAttack> getPlannedAttacksMap() {
        if (plannedAttacks == null) {
            //Old compatibility check
            plannedAttacks = new HashMap<>();
        }
        return plannedAttacks;
    }

    @Override
    public List<WarGoal> getGoals(WarRole warRole) {
        if (warRole == WarRole.MAIN_ATTACKER) {
            return attackGoals;
        } else if (warRole == WarRole.MAIN_DEFENDER) {
            return defenseGoals;
        }
        return Collections.emptyList();
    }

    @Override
    public void removeGoal(WarRole warRole, WarGoal goal) {
        if (warRole == WarRole.MAIN_ATTACKER) {
            attackGoals.remove(goal);
        } else if (warRole == WarRole.MAIN_DEFENDER) {
            defenseGoals.remove(goal);
        }
    }

    @Override
    public void addGoal(WarRole warRole, WarGoal conquerWarGoal) {
        if (warRole == WarRole.MAIN_ATTACKER) {
            attackGoals.add(conquerWarGoal);
        } else if (warRole == WarRole.MAIN_DEFENDER) {
            defenseGoals.add(conquerWarGoal);
        }
    }

    @Override
    public Territory getTerritory(WarRole warRole) {
        if (warRole == WarRole.MAIN_ATTACKER) {
            return getMainAttacker();
        }
        if (warRole == WarRole.MAIN_DEFENDER) {
            return getMainDefender();
        }
        throw new IllegalArgumentException(warRole + " is not authorized");
    }

    /**
     * Generate war goals against one side.
     * @param warRole   The role of the territory opening the menu. War goals used will be from the other side.
     * @param langType  The lang
     * @return          Description used to show war goals applied
     */
    @Override
    public Collection<FilledLang> generateWarGoalsDesciption(WarRole warRole, LangType langType) {
        List<WarGoal> goals = getGoals(warRole.opposite());
        List<FilledLang> goalsToString = new ArrayList<>();
        for (WarGoal goal : goals) {
            goalsToString.add(Lang.WAR_GOAL_LIST_BUTTON_LIST.get(goal.getCurrentDesc(langType)));
        }

        // If no goals are set, add a message
        if (goalsToString.isEmpty()) {
            goalsToString.add(Lang.WAR_GOAL_LIST_BUTTON_LIST_NO_WAR_GOAL_SET.get());
        }
        return goalsToString;

    }

    @Override
    public void createPlannedAttack(WarRole roleOfAttacker, int startTime, int durationTime) {
        String newID = getNextID();
        PlannedAttack newPlannedAttack = new PlannedAttack(
                newID,
                this,
                roleOfAttacker,
                startTime,
                durationTime
        );
        getPlannedAttacksMap().put(newID, newPlannedAttack);
    }

    private String getNextID() {
        int nextId = 0;
        while (getPlannedAttacksMap().containsKey(getID() + "_" + nextId)) {
            nextId++;
        }
        return getID() + "_" + nextId;
    }

    @Override
    public Collection<String> getDefendersID() {
        if (allDefendersID == null) {
            allDefendersID = new ArrayList<>();
            allDefendersID.add(mainDefenderID);
        }
        return allDefendersID;
    }

    @Override
    public Collection<String> getAttackersID() {
        if (allAttackersID == null) {
            allAttackersID = new ArrayList<>();
            allAttackersID.add(mainAttackerID);
        }
        return allAttackersID;
    }

    @Override
    public WarRole getTerritoryRole(Territory territory) {
        if (isMainAttacker(territory))
            return WarRole.MAIN_ATTACKER;
        if (isMainDefender(territory))
            return WarRole.MAIN_DEFENDER;
        if (isAttacker(territory))
            return WarRole.OTHER_ATTACKER;
        if (isDefender(territory))
            return WarRole.OTHER_DEFENDER;
        return WarRole.NEUTRAL;
    }

    /**
     * Get the player role in the war. If several of its territory have different relations in the war (not neutral),
     * the lowest will be used
     * @param player    The player to get the warRole
     * @return  The warRole of the player.
     */
    @Override
    public WarRole getPlayerRole(ITanPlayer player) {
        for(Territory Territory : player.getAllTerritoriesPlayerIsIn()){
            WarRole warRole = getTerritoryRole(Territory);
            if(warRole != WarRole.NEUTRAL){
                return warRole;
            }
        }
        return WarRole.NEUTRAL;
    }

    private boolean isAttacker(Territory territoryConcerned) {
        return getAttackersID().contains(territoryConcerned.getID());
    }

    private boolean isDefender(Territory territoryConcerned) {
        return getDefendersID().contains(territoryConcerned.getID());
    }

    /**
     * Remove a territory from the war.
     * If the territory is the main one, nothing will happen
     *
     * @param territory The territory to remove
     */
    @Override
    public void removeBelligerent(Territory territory) {
        // Do not remove the leader of one side of a war : If they leave they must surrender and stop the whole war.
        if (isMainAttacker(territory) || isMainDefender(territory)) {
            return;
        }
        String territoryID = territory.getID();
        //no need to check, it only removes if it is a part of it
        getAttackersID().remove(territoryID);
        getDefendersID().remove(territoryID);
    }

    @Override
    public void addAttacker(Territory territory) {
        getAttackersID().add(territory.getID());
    }

    @Override
    public void addDefender(Territory territory) {
        getDefendersID().add(territory.getID());
    }

    @Override
    public Collection<Territory> getDefendingTerritories() {
        Collection<Territory> defenders = new ArrayList<>();
        for (String defenderID : getDefendersID()) {
            defenders.add(TerritoryUtil.getTerritory(defenderID));
        }
        return defenders;
    }

    @Override
    public Collection<Territory> getAttackingTerritories() {
        Collection<Territory> attackers = new ArrayList<>();
        for (String attackerID : getAttackersID()) {
            attackers.add(TerritoryUtil.getTerritory(attackerID));
        }
        return attackers;
    }
}