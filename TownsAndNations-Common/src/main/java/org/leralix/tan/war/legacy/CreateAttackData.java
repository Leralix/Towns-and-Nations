package org.leralix.tan.war.legacy;

import org.leralix.tan.utils.constants.Constants;

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
     * The side declaring an attack.
     */
    private final WarRole attackingSide;

    public CreateAttackData(WarRole attackingSide){

        this.minTime = Constants.getMinTimeBeforeAttack();
        this.maxTime = Constants.getMaxTimeBeforeAttack();

        this.selectedTime = (minTime + maxTime) / 2;
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

    public WarRole getAttackingSide(){
        return attackingSide;
    }
}
