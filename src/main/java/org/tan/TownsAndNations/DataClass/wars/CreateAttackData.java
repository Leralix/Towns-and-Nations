package org.tan.TownsAndNations.DataClass.wars;

import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.DataClass.wars.wargoals.NoWarGoal;
import org.tan.TownsAndNations.DataClass.wars.wargoals.WarGoal;
import org.tan.TownsAndNations.utils.ConfigUtil;

public class CreateAttackData {

    long minTime;
    long maxTime;
    long deltaDateTime;
    WarGoal warGoal;
    ITerritoryData mainAttacker;
    ITerritoryData mainDefender;

    public CreateAttackData(ITerritoryData mainAttacker, ITerritoryData mainDefender){

        minTime = ConfigUtil.getCustomConfig("config.yml").getInt("MinimumTimeBeforeAttack",120);
        maxTime = ConfigUtil.getCustomConfig("config.yml").getInt("MaximumTimeBeforeAttack",4320);
        minTime = minTime * 60 * 20;
        maxTime = maxTime * 60 * 20;

        deltaDateTime = (minTime + maxTime) / 2;
        warGoal = new NoWarGoal();
        this.mainAttacker = mainAttacker;
        this.mainDefender = mainDefender;
    }

    public ITerritoryData getMainAttacker() {
        return mainAttacker;
    }

    public ITerritoryData getMainDefender() {
        return mainDefender;
    }

    public boolean canBeSubjugated(){
        return mainAttacker.getRank() > mainDefender.getRank();
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
