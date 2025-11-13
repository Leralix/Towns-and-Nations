package org.leralix.tan.war.legacy;

import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.war.War;

/**
 * Class used during {@link org.leralix.tan.gui.user.war.CreateAttackMenu}
 * to specify the starting date of the new attack
 */
public class CreateAttackData {

    /**
     * The minimum time, in minutes
     */
    private final int minTime;
    /**
     * The maximum time, in minutes
     */
    private final int maxTime;
    /**
     * The selected time, in minutes
     */
    private int selectedTime;
    /**
     * The war in which the created attack will take place
     */
    private final War war;
    /**
     * The side declaring an attack.
     */
    private final WarRole attackingSide;

    public CreateAttackData(War war, WarRole attackingSide){

        this.minTime = Constants.getMinTimeBeforeAttack();
        this.maxTime = Constants.getMaxTimeBeforeAttack();

        this.selectedTime = (minTime + maxTime) / 2;
        this.war = war;
        this.attackingSide = attackingSide;
    }

    public void addDeltaDateTime(int deltaDateTime){
        this.selectedTime += deltaDateTime;
        if(this.selectedTime < minTime){
            this.selectedTime = minTime;
        }
        if(this.selectedTime > maxTime){
            this.selectedTime = maxTime;
        }
    }

    public int getSelectedTime(){
        return selectedTime;
    }

    public War getWar() {
        return war;
    }

    public WarRole getAttackingSide(){
        return attackingSide;
    }
}
