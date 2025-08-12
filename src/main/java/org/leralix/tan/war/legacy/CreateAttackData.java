package org.leralix.tan.war.legacy;

import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.war.War;

public class CreateAttackData {

    private long minTime;
    private long maxTime;
    private long selectedTime;
    private final War war;
    private final WarRole attackingSide;

    public CreateAttackData(War war, WarRole attackingSide){

        this.minTime = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("MinimumTimeBeforeAttack",120);
        this.maxTime = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("MaximumTimeBeforeAttack",4320);
        this.minTime = minTime * 60 * 20;
        this.maxTime = maxTime * 60 * 20;

        this.selectedTime = (minTime + maxTime) / 2;
        this.war = war;
        this.attackingSide = attackingSide;
    }

    public void addDeltaDateTime(long deltaDateTime){
        this.selectedTime += deltaDateTime;
        if(this.selectedTime < minTime){
            this.selectedTime = minTime;
        }
        if(this.selectedTime > maxTime){
            this.selectedTime = maxTime;
        }
    }

    public long getSelectedTime(){
        return selectedTime;
    }

    public War getWar() {
        return war;
    }

    public WarRole getAttackingSide(){
        return attackingSide;
    }
}
