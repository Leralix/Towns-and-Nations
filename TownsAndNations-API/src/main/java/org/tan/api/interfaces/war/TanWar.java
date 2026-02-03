package org.tan.api.interfaces.war;

import org.tan.api.interfaces.territory.TanTerritory;
import org.tan.api.interfaces.war.attack.TanAttack;
import org.tan.api.interfaces.war.wargoals.TanWargoal;

import java.util.Collection;

public interface TanWar {

    /**
     * @return the name of the war
     */
    String getName();

    /**
     * A list of all war goals pressed by the attackers.
     * War goals can be :
     * <ul>
     *     <li>{@link org.tan.api.interfaces.war.wargoals.TanCaptureChunkWargoal}</li>
     *     <li>{@link org.tan.api.interfaces.war.wargoals.TanCaptureFortWargoal}</li>
     *     <li>{@link org.tan.api.interfaces.war.wargoals.TanCaptureLandmarkWargoal}</li>
     *     <li>{@link org.tan.api.interfaces.war.wargoals.TanLiberateWargoal}</li>
     *     <li>{@link org.tan.api.interfaces.war.wargoals.TanSubjugateWargoal}</li>
     * </ul>
     *
     * @return the war goals of the attackers.
     */
    Collection<TanWargoal> getAttackerWarGoals();

    /**
     * A list of all war goals pressed by the defenders.
     * War goals can be :
     * <ul>
     *     <li>{@link org.tan.api.interfaces.war.wargoals.TanCaptureChunkWargoal}</li>
     *     <li>{@link org.tan.api.interfaces.war.wargoals.TanCaptureFortWargoal}</li>
     *     <li>{@link org.tan.api.interfaces.war.wargoals.TanCaptureLandmarkWargoal}</li>
     *     <li>{@link org.tan.api.interfaces.war.wargoals.TanLiberateWargoal}</li>
     *     <li>{@link org.tan.api.interfaces.war.wargoals.TanSubjugateWargoal}</li>
     * </ul>
     *
     * @return the war goals of the defenders.
     */
    Collection<TanWargoal> getDefenderWarGoals();

    /**
     * @return a collection of all participants in the war
     */
    Collection<TanTerritory> getParticipants();

    /**
     * @return a collection of all attackers in the war
     */
    Collection<TanTerritory> getAttackers();

    /**
     * @return a collection of all defenders in the war
     */
    Collection<TanTerritory> getDefenders();

    /**
     * @return a collection of all attacks that took place during the war and
     */
    Collection<TanAttack> getAllAttack();

}
