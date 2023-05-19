package org.tan.towns_and_nations.DataClass;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TownDataClass {

    private String UuidLeader;
    private String TownName;
    private ItemStack townIcon = null;

    public TownDataClass( String uuidLeader, String townName){
        this.UuidLeader = uuidLeader;
        this.TownName = townName;
        this.townIcon = null;
    }



}
