package org.leralix.tan.war;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.WarEndInternalEvent;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.gui.cosmetic.type.ItemIconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.war.capture.CaptureManager;
import org.leralix.tan.war.info.AttackResultCancelled;
import org.leralix.tan.war.legacy.CreateAttackData;
import org.leralix.tan.war.legacy.WarRole;
import org.leralix.tan.war.legacy.wargoals.WarGoal;

import java.util.*;

public class War {

    private final String ID;
    private String name;
    private final String mainDefenderID;
    private final String mainAttackerID;

    private Collection<String> allDefendersID;
    private Collection<String> allAttackersID;

    private final List<WarGoal> attackGoals;
    private final List<WarGoal> defenseGoals;

    private HashMap<String, PlannedAttack> plannedAttacks;

    public War(String id, TerritoryData mainAttacker, TerritoryData mainDefender, List<String> otherDefendersID) {
        this.ID = id;
        this.name = Lang.BASIC_ATTACK_NAME.get(
                Lang.getServerLang(),
                mainAttacker.getName(),
                mainDefender.getName()
        );
        this.mainDefenderID = mainDefender.getID();
        this.mainAttackerID = mainAttacker.getID();
        this.allDefendersID = new ArrayList<>(otherDefendersID);
        this.allDefendersID.add(mainDefenderID);
        this.allAttackersID = new ArrayList<>();
        this.allAttackersID.add(mainAttackerID);
        this.attackGoals = new ArrayList<>();
        this.defenseGoals = new ArrayList<>();
        this.plannedAttacks = new HashMap<>();
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

    public IconBuilder getIcon() {
        return new IconBuilder(new ItemIconBuilder(Material.IRON_SWORD))
                .setName(getName())
                .setDescription(
                        Lang.ATTACK_ICON_DESC_1.get(getMainAttacker().getColoredName()),
                        Lang.ATTACK_ICON_DESC_2.get(getMainDefender().getColoredName())
                );
    }

    public void territorySurrender(TerritoryData looserTerritory) {
        territorySurrender(getTerritoryRole(looserTerritory));
    }

    public void territorySurrender(WarRole looserTerritory) {

        TerritoryData looser = getTerritory(looserTerritory);
        TerritoryData winner = getTerritory(looserTerritory.opposite());

        // All chunks captured due to the war are now released
        CaptureManager.getInstance().removeCapture(this);

        List<WarGoal> goals = getGoals(looserTerritory.opposite());

        for (WarGoal goal : goals) {
            goal.applyWarGoal(winner, looser);
        }

        EventManager.getInstance().callEvent(new WarEndInternalEvent(winner, looser, goals));

        endWar();
    }

    private void endWar() {
        getMainAttacker().setRelation(getMainDefender(), TownRelation.NEUTRAL);
        for (PlannedAttack plannedAttack : getPlannedAttacks()) {
            plannedAttack.end(new AttackResultCancelled());
        }
        WarStorage.getInstance().remove(this);
    }


    public Collection<PlannedAttack> getPlannedAttacks() {
        return getPlannedAttacksMap().values();
    }

    public Map<String, PlannedAttack> getPlannedAttacksMap() {
        if (plannedAttacks == null) {
            //Old compatibility check
            plannedAttacks = new HashMap<>();
        }
        return plannedAttacks;
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

    public PlannedAttack addAttack(CreateAttackData createAttackData) {
        String newID = getNextID();
        PlannedAttack newPlannedAttack = new PlannedAttack(newID, createAttackData);
        getPlannedAttacksMap().put(newID, newPlannedAttack);
        return newPlannedAttack;
    }

    private String getNextID() {
        int ID = 0;
        while (getPlannedAttacksMap().containsKey(getID() + "_" + ID)) {
            ID++;
        }
        return getID() + "_" + ID;
    }

    public Collection<String> getDefendersID() {
        if (allDefendersID == null) {
            allDefendersID = new ArrayList<>();
            allDefendersID.add(mainDefenderID);
        }
        return allDefendersID;
    }

    public Collection<String> getAttackersID() {
        if (allAttackersID == null) {
            allAttackersID = new ArrayList<>();
            allDefendersID.add(mainAttackerID);
        }
        return allAttackersID;
    }

    public WarRole getTerritoryRole(TerritoryData territory) {
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

    private boolean isAttacker(TerritoryData territoryConcerned) {
        return allAttackersID.contains(territoryConcerned.getID());
    }

    private boolean isDefender(TerritoryData territoryConcerned) {
        return allDefendersID.contains(territoryConcerned.getID());
    }

    /**
     * Remove a territory from the war.
     * If the territory is the main one, nothing will happen
     *
     * @param territory The territory to remove
     */
    public void removeBelligerent(TerritoryData territory) {
        // Do not remove the leader of one side of a war : If they leave they must surrender and stop the whole war.
        if (isMainAttacker(territory) || isMainDefender(territory)) {
            return;
        }
        String territoryID = territory.getID();
        //no need to check, it only removes if it is a part of it
        allAttackersID.remove(territoryID);
        allDefendersID.remove(territoryID);
    }

    public void addAttacker(TerritoryData territoryData) {
        allAttackersID.add(territoryData.getID());
    }

    public void addDefender(TerritoryData territoryData) {
        allDefendersID.add(territoryData.getID());
    }

    public Collection<TerritoryData> getDefendingTerritories() {
        Collection<TerritoryData> defenders = new ArrayList<>();
        for (String defenderID : getDefendersID()) {
            defenders.add(TerritoryUtil.getTerritory(defenderID));
        }
        return defenders;
    }

    public Collection<TerritoryData> getAttackingTerritories() {
        Collection<TerritoryData> attackers = new ArrayList<>();
        for (String attackerID : getAttackersID()) {
            attackers.add(TerritoryUtil.getTerritory(attackerID));
        }
        return attackers;
    }
}