package org.leralix.tan.api.internal.wrappers;

import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.info.AttackResult;
import org.leralix.tan.war.info.AttackResultCompleted;
import org.tan.api.interfaces.war.attack.TanAttack;
import org.tan.api.interfaces.war.attack.TanAttackResults;

import java.time.Instant;
import java.util.Optional;

public class AttackWrapper implements TanAttack {

    private final PlannedAttack plannedAttack;

    public AttackWrapper(PlannedAttack plannedAttack){
        this.plannedAttack = plannedAttack;
    }

    @Override
    public Instant getStartTime() {
        return Instant.ofEpochMilli(plannedAttack.getStartTime());
    }

    @Override
    public Instant getEndTime() {
        return Instant.ofEpochMilli(plannedAttack.getEndTime());
    }

    @Override
    public boolean isFinished() {
        return plannedAttack.isFinished();
    }

    @Override
    public Optional<TanAttackResults> getResults() {
        AttackResult attackResult = plannedAttack.getAttackResult();
        if(attackResult instanceof AttackResultCompleted attackResultCompleted){
            return Optional.of(attackResultCompleted);
        }
        else {
            return Optional.empty();
        }
    }
}
