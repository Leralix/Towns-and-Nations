package org.leralix.tan.api.internal.wrappers;

import org.leralix.tan.war.War;
import org.leralix.tan.war.info.WarRole;
import org.tan.api.interfaces.territory.TanTerritory;
import org.tan.api.interfaces.war.TanWar;
import org.tan.api.interfaces.war.attack.TanAttack;
import org.tan.api.interfaces.war.wargoals.TanWargoal;

import java.util.Collection;
import java.util.List;

public class WarWrapper implements TanWar {


    private final War war;

    public WarWrapper(War war) {
        this.war = war;
    }

    @Override
    public String getName() {
        return war.getName();
    }

    @Override
    public Collection<TanWargoal> getAttackerWarGoals() {
        return List.copyOf(war.getGoals(WarRole.MAIN_ATTACKER));
    }

    @Override
    public Collection<TanWargoal> getDefenderWarGoals() {
        return List.copyOf(war.getGoals(WarRole.MAIN_DEFENDER));
    }

    @Override
    public Collection<TanTerritory> getParticipants() {
        var participants = war.getAttackingTerritories();
        participants.addAll(war.getDefendingTerritories());
        return List.copyOf(participants);
    }

    @Override
    public Collection<TanTerritory> getAttackers() {
        return List.copyOf(war.getAttackingTerritories());
    }

    @Override
    public Collection<TanTerritory> getDefenders() {
        return List.copyOf(war.getDefendingTerritories());
    }

    @Override
    public Collection<TanAttack> getAllAttack() {
        return List.copyOf(war.getPlannedAttacks().stream().map(AttackWrapper::new).toList());
    }

}
