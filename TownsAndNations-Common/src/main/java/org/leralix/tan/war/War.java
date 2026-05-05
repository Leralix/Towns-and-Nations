package org.leralix.tan.war;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.war.info.WarRole;
import org.leralix.tan.war.wargoals.WarGoal;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface War {
    String getID();

    String getName();

    void setName(String name);

    String getMainDefenderID();

    Territory getMainDefender();

    String getMainAttackerID();

    Territory getMainAttacker();

    boolean isMainAttacker(Territory territory);

    boolean isMainDefender(Territory territory);

    IconBuilder getIcon();

    default void territorySurrender(Territory looserTerritory) {
        territorySurrender(getTerritoryRole(looserTerritory));
    }

    void territorySurrender(WarRole looserTerritory);

    void endWar();

    Collection<PlannedAttack> getPlannedAttacks();

    Map<String, PlannedAttack> getPlannedAttacksMap();

    List<WarGoal> getGoals(WarRole warRole);

    void removeGoal(WarRole warRole, WarGoal goal);

    void addGoal(WarRole warRole, WarGoal conquerWarGoal);

    Territory getTerritory(WarRole warRole);

    Collection<FilledLang> generateWarGoalsDesciption(WarRole warRole, LangType langType);

    void createPlannedAttack(WarRole roleOfAttacker, int startTime, int durationTime);

    Collection<String> getDefendersID();

    Collection<String> getAttackersID();

    WarRole getTerritoryRole(Territory territory);

    WarRole getPlayerRole(ITanPlayer player);

    void removeBelligerent(Territory territory);

    void addAttacker(Territory territory);

    void addDefender(Territory territory);

    Collection<Territory> getDefendingTerritories();

    Collection<Territory> getAttackingTerritories();
}
