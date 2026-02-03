package org.leralix.tan.war.info;

import org.leralix.tan.data.timezone.TimeZoneEnum;
import org.leralix.tan.data.timezone.TimeZoneManager;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.tan.api.interfaces.war.attack.TanAttackResults;

import java.time.Instant;
import java.util.List;

/**
 * Represents the completed result of an attack, including statistics such as deaths and captures.
 * "Attacker" refers to the attacking territory during the assault (it can be the defender counter-attacking).
 */
public class AttackResultCompleted extends AttackResult implements TanAttackResults {

    /**
     * End of the attack, in epoch milliseconds
     */
    private final long endDateTime;
    /**
     * Number of deaths on the attacker side
     */
    private final int nbDeathsAttacker;
    /**
     * Number of deaths on the defender side
     */
    private final int nbDeathsDefender;
    /**
     * Number of forts captured by the attacker at the end of the attack.
     * If a fort was recaptured by the defender later, it is not counted here.
     */
    private final int nbFortsCaptured;
    /**
     * Number of chunks captured by the attacker at the end of the attack.
     */
    private final int nbChunkCaptured;

    public AttackResultCompleted(
            int nbAttackersKilled,
            int nbDefendersKilled,
            int nbFortsCaptured,
            int nbClaimsCaptured
    ) {
        this.endDateTime = System.currentTimeMillis();
        this.nbDeathsAttacker = nbAttackersKilled;
        this.nbDeathsDefender = nbDefendersKilled;
        this.nbFortsCaptured = nbFortsCaptured;
        this.nbChunkCaptured = nbClaimsCaptured;
    }

    @Override
    public List<FilledLang> getResultLines(LangType langType, TimeZoneEnum timeZone) {

        FilledLang exactTimeStart = TimeZoneManager.getInstance().formatDate(Instant.ofEpochMilli(endDateTime), timeZone, langType.getLocale());

        return List.of(
                Lang.ATTACK_ICON_FINISHED.get(),
                Lang.ATTACK_ICON_FINISHED_DATE.get(exactTimeStart.get(langType)),
                Lang.ATTACK_ICON_FINISHED_NB_DEATH_ATTACKERS.get(Integer.toString(nbDeathsAttacker)),
                Lang.ATTACK_ICON_FINISHED_NB_DEATH_DEFENDERS.get(Integer.toString(nbDeathsDefender)),
                Lang.ATTACK_ICON_FINISHED_NB_CHUNK_CAPTURED.get(Integer.toString(nbChunkCaptured)),
                Lang.ATTACK_ICON_FINISHED_NB_FORTS_CAPTURED.get(Integer.toString(nbFortsCaptured))
        );
    }

    @Override
    public int getNbDeathsAttacker() {
        return nbDeathsAttacker;
    }

    @Override
    public int getNbDeathsDefender() {
        return nbDeathsDefender;
    }

    @Override
    public int getNbFortsCaptured() {
        return nbFortsCaptured;
    }

    @Override
    public int getNbChunkCaptured() {
        return nbChunkCaptured;
    }
}
