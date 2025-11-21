package org.leralix.tan.war.info;

import org.leralix.tan.lang.FilledLang;

import java.util.List;

/**
 * Represents the completed result of an attack, including statistics such as deaths and captures.
 * "Attacker" refers to the attacking territory during the assault (it can be the defender counter-attacking).
 */
public class AttackResultCompleted extends AttackResult {

    /**
     * Number of deaths on the attacker side
     */
    int nbDeathsAttacker;
    /**
     * Number of deaths on the defender side
     */
    int nbDeathsDefender;
    /**
     * Number of forts captured by the attacker at the end of the attack.
     * If a fort was recaptured by the defender later, it is not counted here.
     */
    int nbFortsCaptured;
    /**
     * Number of chunks captured by the attacker at the end of the attack.
     */
    int nbChunkCaptured;

    public AttackResultCompleted(
            int nbAttackersKilled,
            int nbDefendersKilled,
            int nbFortsCaptured,
            int nbClaimsCaptured
    ) {
        this.nbDeathsAttacker = nbAttackersKilled;
        this.nbDeathsDefender = nbDefendersKilled;
        this.nbFortsCaptured = nbFortsCaptured;
        this.nbChunkCaptured = nbClaimsCaptured;
    }

    @Override
    public List<FilledLang> getResultLines() {
        return List.of();
    }
}
