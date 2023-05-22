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
    public boolean open;
    public String DateCreated;
    private String Overlord;
    private String townIconMaterialCode;
    private HashMap<String, ArrayList<String>> townPlayerList = new HashMap<>();

    public TownDataClass( String townId, String townName, String uuidLeader){
        this.TownId = townId;
        this.UuidLeader = uuidLeader;
        this.TownName = townName;
        this.Description = "Description";
        this.open = false;
        this.DateCreated = new Date().toString();
        this.Overlord = null;
        this.townIconMaterialCode = null;

        ArrayList<String> list = new ArrayList<>();
        list.add(uuidLeader);
        this.townPlayerList.put("Leader", list);

    }

    public String getTownId() {
        return this.TownId;
    }

    public void setTownId(String townId) {
        this.TownId = townId;
    }

    public void setTownName(String townName) {
        this.TownName = townName;
    }

    public String getTownName(){
        return this.TownName;
    }

    public String getUuidLeader() {
        return this.UuidLeader;
    }

    public void setUuidLeader(String uuidLeader) {
        this.UuidLeader = uuidLeader;
    }

    public String getDescription() {
        return this.Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean openValue) {
        this.open = openValue;
    }

    public String getDateCreated() {
        return this.DateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.DateCreated = dateCreated;
    }

    public String getOverlord() {
        return this.Overlord;
    }

    public void setOverlord(String overlord) {
        this.Overlord = overlord;
    }

    public ItemStack getTownIconItemStack() {
        if(this.townIconMaterialCode == null){
            return null;
        }
        else
            return new ItemStack(Material.getMaterial(this.townIconMaterialCode));

    }

    public void setTownIconMaterialCode(Material material) {
        this.townIconMaterialCode = material.name();
    }

    public HashMap<String, ArrayList<String>> getTownPlayerList() {
        return this.townPlayerList;
    }

    public void setTownPlayerList(HashMap<String, ArrayList<String>> townPlayerList) {
        this.townPlayerList = townPlayerList;
    }




}
