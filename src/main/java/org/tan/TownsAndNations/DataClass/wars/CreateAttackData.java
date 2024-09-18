package org.tan.TownsAndNations.DataClass.wars;

import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.DataClass.territoryData.RegionData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.DataClass.wars.wargoals.NoWarGoal;
import org.tan.TownsAndNations.DataClass.wars.wargoals.SubjugateWarGoal;
import org.tan.TownsAndNations.DataClass.wars.wargoals.WarGoal;
import org.tan.TownsAndNations.utils.ConfigUtil;

public class CreateAttackData {

    long deltaDateTime;
    WarGoal warGoal;
    ITerritoryData mainAttacker;
    ITerritoryData mainDefender;

    public CreateAttackData(ITerritoryData mainAttacker, ITerritoryData mainDefender){
        deltaDateTime = ConfigUtil.getCustomConfig("config.yml").getLong("MinimumTimeBeforeAttack") * 60 * 20;
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

    public void setDeltaDateTime(int value) {
        deltaDateTime = value;
    }
}
