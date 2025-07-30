package org.leralix.tan.war.legacy;

import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.war.legacy.wargoals.ConquerWarGoal;
import org.leralix.tan.war.legacy.wargoals.WarGoal;

public class CreateAttackData {

    long minTime;
    long maxTime;
    long deltaDateTime;
    WarGoal warGoal;
    TerritoryData mainAttacker;
    TerritoryData mainDefender;

    public CreateAttackData(TerritoryData mainAttacker, TerritoryData mainDefender){

        this.minTime = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("MinimumTimeBeforeAttack",120);
        this.maxTime = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("MaximumTimeBeforeAttack",4320);
        this.minTime = minTime * 60 * 20;
        this.maxTime = maxTime * 60 * 20;

        this.deltaDateTime = (minTime + maxTime) / 2;
        this.warGoal = new ConquerWarGoal(1);
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
