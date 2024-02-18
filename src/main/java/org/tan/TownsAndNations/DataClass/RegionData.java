package org.tan.TownsAndNations.DataClass;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;

import java.util.ArrayList;
import java.util.List;

public class RegionData {

    private String id;
    private String name;
    private String capitalID;
    private String nationID;
    private String regionIconType;
    private Integer taxRate;
    private Integer balance;
    private String description;
    private List<String> townsInRegion = new ArrayList<>();


    public RegionData(String id, String name, String ownerID) {
        PlayerData owner = PlayerDataStorage.get(ownerID);
        TownData ownerTown = TownDataStorage.get(owner);

        this.id = id;
        this.name = name;
        this.capitalID = ownerTown.getID();
        this.nationID = null;
        this.regionIconType = "COBBLESTONE";
        this.taxRate = 1;
        this.description = "default description";
        this.townsInRegion.add(ownerTown.getID());
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCapitalID() {
        return capitalID;
    }
    public TownData getCapital() {
        return TownDataStorage.get(capitalID);
    }

    public PlayerData getOwner() {
        return TownDataStorage.get(capitalID).getLeader();
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

    public int getNumberOfTownsIn() {
        return townsInRegion.size();
    }

    public void addTown(String townID) {
        townsInRegion.add(townID);
    }

    public ItemStack getIconItemStack() {
        return new ItemStack(Material.valueOf(this.regionIconType));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Object getTotalPlayerCount() {
        int count = 0;
        for (TownData town : getTownsInRegion()){
            count += town.getPlayerList().size();
        }
        return count;
    }
    public boolean isCapital( TownData town) {
        return isCapital(town.getID());
    }
    public boolean isCapital( String townID) {
        return capitalID.equals(townID);
    }
    public void setCapital(TownData town) {
        setCapital(town.getID());
    }
    public void setCapital(String townID) {
        this.capitalID = townID;
    }
    public void setRegionIconType(Material itemMaterial) {
        setRegionIconType(itemMaterial.name());
    }
    public void setRegionIconType(String regionIconType) {
        this.regionIconType = regionIconType;
    }

    public Integer getBalance() {
        return balance;
    }
    public Integer addBalance(Integer amount) {
        return balance += amount;
    }
    public Integer removeBalance(Integer amount) {
        return balance -= amount;
    }

    public int getIncomeTomorrow() {
        int income = 0;
        for (TownData town  : getTownsInRegion()) {
            if(town.getBalance() > taxRate) {
                income += taxRate;
            }
        }
        return income;
    }
}
