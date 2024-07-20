package org.tan.TownsAndNations.DataClass;

import org.tan.TownsAndNations.utils.ConfigUtil;

public class CreateAttackData {

    long deltaDateTime;

    public CreateAttackData(){
        deltaDateTime = ConfigUtil.getCustomConfig("config.yml").getLong("MinimumTimeBeforeAttack") * 60000;
    }

    public void addDeltaDateTime(long deltaDateTime){
        this.deltaDateTime += deltaDateTime;
    }

    public long getDeltaDateTime(){
        return deltaDateTime;
    }
    public String getStringDeltaDateTime(){
        int nbHours = (int) (deltaDateTime / 3600000);
        int nbMinutes = (int) ((deltaDateTime % 3600000) / 60000);
        return nbHours + "h" + nbMinutes + "m";
    }


}
