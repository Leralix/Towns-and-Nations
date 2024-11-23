package org.leralix.tan.dataclass.wars;

import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.wars.wargoals.NoWarGoal;
import org.leralix.tan.dataclass.wars.wargoals.WarGoal;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

public class CreateAttackData {

    long minTime;
    long maxTime;
    long deltaDateTime;
    WarGoal warGoal;
    TerritoryData mainAttacker;
    TerritoryData mainDefender;

    public CreateAttackData(TerritoryData mainAttacker, TerritoryData mainDefender){

        minTime = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("MinimumTimeBeforeAttack",120);
        maxTime = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("MaximumTimeBeforeAttack",4320);
        minTime = minTime * 60 * 20;
        maxTime = maxTime * 60 * 20;

        deltaDateTime = (minTime + maxTime) / 2;
        warGoal = new NoWarGoal();
        this.mainAttacker = mainAttacker;
        this.mainDefender = mainDefender;
    }

    public TerritoryData getMainAttacker() {
        return mainAttacker;
    }

    public TerritoryData getMainDefender() {
        return mainDefender;
    }

    public boolean canBeSubjugated(){
        return mainAttacker.getHierarchyRank() > mainDefender.getHierarchyRank();
    }

    public void addDeltaDateTime(long deltaDateTime){
        this.deltaDateTime += deltaDateTime;
        if(this.deltaDateTime < minTime){
            this.deltaDateTime = minTime;
        }
        if(this.deltaDateTime > maxTime){
            this.deltaDateTime = maxTime;
        }



    }

    public long getDeltaDateTime(){
        return deltaDateTime;
    }

    public WarGoal getWargoal(){
        return warGoal;
    }


    public void setWarGoal(WarGoal subjugateWarGoal) {
        this.warGoal = subjugateWarGoal;
    }

    public void setDeltaDateTime(int value) {
        deltaDateTime = value;
    }
}
