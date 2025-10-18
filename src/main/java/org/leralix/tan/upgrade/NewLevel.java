package org.leralix.tan.upgrade;

import java.util.HashMap;
import java.util.Map;

public class NewLevel {

    private int mainLevel;
    private Map<String, Integer> level;

    public NewLevel(){
        this.mainLevel = 1;
        this.level = new HashMap<>();
    }

    public int getLevel(Upgrade upgrade){
        if(level == null){
            level = new HashMap<>();
        }
        if(!level.containsKey(upgrade.getID())){
            return 0;
        }
        return level.get(upgrade.getID());
    }

    public void levelUp(Upgrade townUpgrade) {
        String key = townUpgrade.getID();
        if(!level.containsKey(key)){
            level.put(key, 1);
            return;
        }
        level.put(key, level.get(key) + 1);
    }

    public int getMainLevel() {
        return mainLevel;
    }

    public void levelUpMain(){
        mainLevel++;
    }
}
