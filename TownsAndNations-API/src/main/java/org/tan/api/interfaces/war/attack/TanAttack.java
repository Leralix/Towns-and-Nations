package org.tan.api.interfaces.war.attack;

import java.time.Instant;
import java.util.Optional;

public interface TanAttack {

    /**
     * @return the time when the attack start
     */
    Instant getStartTime();

    /**
     * @return the time when the attack will end
     */
    Instant getEndTime();

    /**
     * @return the results of the attack, if it has been resolved
     */
    Optional<TanAttackResults> getResults();

}
