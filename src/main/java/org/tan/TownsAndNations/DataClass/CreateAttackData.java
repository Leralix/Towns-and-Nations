package org.tan.TownsAndNations.DataClass;

import org.tan.TownsAndNations.utils.ConfigUtil;

public class CreateAttackData {

    long deltaDateTime;

    public CreateAttackData(){
        deltaDateTime = ConfigUtil.getCustomConfig("config.yml").getLong("MinimumTimeBeforeAttack") * 60 * 20;
    }

    public void addDeltaDateTime(long deltaDateTime){
        this.deltaDateTime += deltaDateTime;
    }

    public long getDeltaDateTime(){
        return deltaDateTime;
    }



}
