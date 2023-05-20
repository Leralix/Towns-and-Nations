package org.tan.towns_and_nations.DataClass;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class TownDataClass {

    private String UuidLeader;
    private String TownName;
    private ItemStack townIcon = null;

    private HashMap<String, ArrayList<String>> townPlayerList = new HashMap<>();

    public TownDataClass( String uuidLeader, String townName){
        this.UuidLeader = uuidLeader;
        this.TownName = townName;
        this.townIcon = null;

        ArrayList<String> list = new ArrayList<>();
        list.add(uuidLeader);
        this.townPlayerList.put("Leader", list);

    }



}
