package org.tan.towns_and_nations.DataClass;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TownDataClass {

    private String TownId;
    private String TownName;
    private String UuidLeader;
    private String Description;
    public boolean isOpen;
    public String DateCreated;
    private String Overlord;
    private ItemStack townIcon;
    private HashMap<String, ArrayList<String>> townPlayerList = new HashMap<>();

    public TownDataClass( String townId, String townName, String uuidLeader){
        this.TownId = townId;
        this.UuidLeader = uuidLeader;
        this.TownName = townName;
        this.Description = "Description";
        this.isOpen = false;
        this.DateCreated = new Date().toString();
        this.Overlord = null;
        this.townIcon = null;

        ArrayList<String> list = new ArrayList<>();
        list.add(uuidLeader);
        this.townPlayerList.put("Leader", list);

    }

    public String getTownName(){
        return this.TownName;
    }



}
