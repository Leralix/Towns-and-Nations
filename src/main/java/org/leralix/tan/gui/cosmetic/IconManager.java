package org.leralix.tan.gui.cosmetic;

import org.leralix.tan.gui.cosmetic.type.IconBuilder;

import java.util.EnumMap;
import java.util.Map;

public class IconManager {

    private static IconManager instance;

    Map<IconKey, IconBuilder> iconMap;


    private IconManager(){
        this.iconMap = new EnumMap<>(IconKey.class);



    }


    public static IconManager getInstance(){
        if(instance == null){
            instance = new IconManager();
        }
        return instance;
    }

}
