package org.tan.TownsAndNations.DataClass.wars;

import org.tan.TownsAndNations.DataClass.wars.wargoals.NoWarGoal;
import org.tan.TownsAndNations.DataClass.wars.wargoals.SubjugateWarGoal;
import org.tan.TownsAndNations.DataClass.wars.wargoals.WarGoal;
import org.tan.TownsAndNations.utils.ConfigUtil;

public class CreateAttackData {

    long deltaDateTime;
    WarGoal warGoal;

    public CreateAttackData(){
        deltaDateTime = ConfigUtil.getCustomConfig("config.yml").getLong("MinimumTimeBeforeAttack") * 60 * 20;
        warGoal = new NoWarGoal();
    }

    public void addDeltaDateTime(long deltaDateTime){
        this.deltaDateTime += deltaDateTime;
    }

    public long getDeltaDateTime(){
        return deltaDateTime;
    }

    public WarGoal getWargoal(){
        return warGoal;
    }


    public void setWargoal(WarGoal subjugateWarGoal) {
        this.warGoal = subjugateWarGoal;
    }
}
