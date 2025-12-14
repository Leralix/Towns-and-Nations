package org.tan.api.interfaces.war;

public interface TanAttackResults extends TanAttackStatus {


    /**
     * Number of deaths on the attacker side
     */
    int getNbDeathsAttacker();
    /**
     * Number of deaths on the defender side
     */
     int getNbDeathsDefender();
    /**
     * Number of forts captured by the attacker at the end of the attack.
     * If a fort was recaptured by the defender later, it is not counted here.
     */
    int getNbFortsCaptured();
    /**
     * Number of chunks captured by the attacker at the end of the attack.
     */
    int getNbChunkCaptured();

}
