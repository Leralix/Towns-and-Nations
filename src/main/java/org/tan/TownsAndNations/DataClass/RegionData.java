package org.tan.TownsAndNations.DataClass;

import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;

import java.util.ArrayList;
import java.util.List;

public class RegionData {

    private String id;
    private String name;
    private String ownerID;
    private String nationID;
    private Integer taxRate;
    private List<String> townsInRegion = new ArrayList<>();


    public RegionData(String id, String name, String ownerID) {
        PlayerData owner = PlayerDataStorage.get(ownerID);
        TownData ownerTown = TownDataStorage.get(owner);

        this.id = id;
        this.name = name;
        this.ownerID = ownerID;
        this.nationID = null;
        this.taxRate = 1;
        this.townsInRegion.add(ownerTown.getID());
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public PlayerData getOwnerData() {
        return PlayerDataStorage.get(ownerID);
    }
    public boolean isOwner(String playerID) {
        return ownerID.equals(playerID);
    }

    public boolean hasNation() {
        return nationID != null;
    }
    public String getNationID() {
        return nationID;
    }

    public void setNationID(String nationID) {
        this.nationID = nationID;
    }

    public Integer getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Integer taxRate) {
        this.taxRate = taxRate;
    }

    public List<String> getTownsIDInRegion() {
        return townsInRegion;
    }

    public List<TownData> getTownsInRegion() {
        List<TownData> towns = new ArrayList<>();
        for (String townID : townsInRegion) {
            towns.add(TownDataStorage.get(townID));
        }
        return towns;
    }

    public void addTown(String townID) {
        townsInRegion.add(townID);
    }






}
