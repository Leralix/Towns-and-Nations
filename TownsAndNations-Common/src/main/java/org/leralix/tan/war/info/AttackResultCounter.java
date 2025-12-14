package org.leralix.tan.war.info;

public class AttackResultCounter {

    private int nbAttackersKilled;
    private int nbDefendersKilled;
    private int nbFortsCaptured;
    private int nbClaimsCaptured;

    public AttackResultCounter() {
        this.nbAttackersKilled = 0;
        this.nbDefendersKilled = 0;
        this.nbFortsCaptured = 0;
        this.nbClaimsCaptured = 0;
    }

    public AttackResultCompleted buildResult() {
        return new AttackResultCompleted(nbAttackersKilled, nbDefendersKilled, nbFortsCaptured, nbClaimsCaptured);
    }

    public void incrementAttackersKilled() {
        nbAttackersKilled++;
    }

    public void incrementDefendersKilled() {
        nbDefendersKilled++;
    }

    public void incrementFortsCaptured() {
        nbFortsCaptured++;
    }

    public void decrementFortsCaptured() {
        nbFortsCaptured--;
    }

    public void incrementClaimsCaptured() {
        nbClaimsCaptured++;
    }

    public void decrementClaimsCaptured() {
        nbClaimsCaptured--;
    }
}
