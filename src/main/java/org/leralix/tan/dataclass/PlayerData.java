package org.leralix.tan.dataclass;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.invitation.TownInviteDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.wars.CurrentAttack;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.dataclass.territory.RegionData;

import java.util.*;


public class PlayerData {

    private final String UUID;
    private String storedName;
    private Double Balance;
    private String TownId;
    private Integer townRankID;
    private Integer regionRankID;
    private List<String> propertiesListID;
    private List<String> attackInvolvedIn;
    private LangType lang;

    public PlayerData(Player player) {
        this.UUID = player.getUniqueId().toString();
        this.storedName = player.getName();
        this.Balance = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getDouble("StartingMoney");
        this.TownId = null;
        this.townRankID = null;
        this.regionRankID = null;
        this.propertiesListID = new ArrayList<>();
        this.attackInvolvedIn = new ArrayList<>();
    }

    public String getID() {
        return UUID;
    }

    public String getNameStored() {
        if (storedName == null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(java.util.UUID.fromString(UUID));
            storedName = offlinePlayer.getName();
            if (storedName == null) {
                storedName = "Unknown name";
            }
        }
        return storedName;
    }

    public void setNameStored(String name) {
        this.storedName = name;
    }

    public void clearName() {
        this.storedName = null;
    }

    public double getBalance() {
        return this.Balance;
    }

    public void setBalance(double balance) {
        this.Balance = balance;
    }

    public String getTownId() {
        return this.TownId;
    }

    public TownData getTown() {
        return TownDataStorage.getInstance().get(this.TownId);
    }

    public boolean hasTown() {
        return this.TownId != null;
    }

    public boolean isTownOverlord() {
        if (!hasTown())
            return false;
        return getTown().isLeader(this.UUID);
    }

    public RankData getTownRank() {
        if (!hasTown())
            return null;
        return getTown().getRank(getTownRankID());
    }

    public RankData getRegionRank() {
        if (!hasRegion())
            return null;
        return getRegion().getRank(getRegionRankID());
    }

    public void addToBalance(double amount) {
        this.Balance = this.Balance + amount;
    }

    public void removeFromBalance(double amount) {
        this.Balance = this.Balance - amount;
    }

    public boolean hasRegion() {
        if (!this.hasTown()) {
            return false;
        }
        return getTown().haveOverlord();
    }

    public RegionData getRegion() {
        if (!hasRegion())
            return null;
        return getTown().getRegion();
    }

    public UUID getUUID() {
        return java.util.UUID.fromString(UUID);
    }

    public void joinTown(TownData townData) {
        this.TownId = townData.getID();
        setTownRankID(townData.getDefaultRankID());
    }

    public void leaveTown() {
        this.TownId = null;
        this.townRankID = null;
    }

    public void setTownRankID(int townRankID) {
        this.townRankID = townRankID;
    }

    public Integer getTownRankID() {
        return this.townRankID;
    }

    public List<String> getPropertiesListID() {
        if (this.propertiesListID == null)
            this.propertiesListID = new ArrayList<>();
        return propertiesListID;
    }

    public void addProperty(PropertyData propertyData) {
        getPropertiesListID().add(propertyData.getTotalID());
    }

    public List<PropertyData> getProperties() {
        List<PropertyData> propertyDataList = new ArrayList<>();

        for (String propertyID : getPropertiesListID()) {
            String[] parts = propertyID.split("_");
            String tID = parts[0];
            String pID = parts[1];

            PropertyData nextProperty = TownDataStorage.getInstance().get(tID).getProperty(pID);

            propertyDataList.add(nextProperty);
        }

        return propertyDataList;
    }

    public void removeProperty(PropertyData propertyData) {
        this.propertiesListID.remove(propertyData.getTotalID());
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getUUID());
    }

    public List<String> getAttackInvolvedIn() {
        if (attackInvolvedIn == null)
            attackInvolvedIn = new ArrayList<>();
        return attackInvolvedIn;
    }

    public void notifyDeath(Player killer) {
        Iterator<String> iterator = getAttackInvolvedIn().iterator();
        while (iterator.hasNext()) {
            String attackID = iterator.next();
            CurrentAttack currentAttacks = CurrentAttacksStorage.get(attackID);
            if (currentAttacks != null) {
                currentAttacks.playerKilled(this, killer);
            } else {
                iterator.remove();
            }
        }
    }

    public void addWar(CurrentAttack currentAttacks) {
        if (getAttackInvolvedIn().contains(currentAttacks.getId())) {
            return;
        }
        getAttackInvolvedIn().add(currentAttacks.getId());
    }

    public void updateCurrentAttack() {
        Iterator<String> iterator = getAttackInvolvedIn().iterator();
        while (iterator.hasNext()) {
            String attackID = iterator.next();
            CurrentAttack currentAttack = CurrentAttacksStorage.get(attackID);
            if (currentAttack == null || !currentAttack.containsPlayer(this)) {
                iterator.remove();
            } else {
                currentAttack.addPlayer(this);
            }
        }
    }

    public boolean isAtWarWith(TerritoryData territoryData) {
        if (territoryData == null) {
            return false;
        }
        for (String attackID : getAttackInvolvedIn()) {
            CurrentAttack currentAttack = CurrentAttacksStorage.get(attackID);
            if (currentAttack == null) {
                getAttackInvolvedIn().remove(attackID);
                continue;
            }
            if (currentAttack.getDefenders().contains(territoryData)) {
                return true;
            }
        }
        return false;
    }

    public void removeWar(@NotNull CurrentAttack currentAttacks) {
        getAttackInvolvedIn().remove(currentAttacks.getId());
    }

    public TownRelation getRelationWithPlayer(Player playerToAdd) {
        PlayerData otherPlayer = PlayerDataStorage.getInstance().get(playerToAdd);
        if (!hasTown() || !otherPlayer.hasTown())
            return null;

        TownData playerTown = TownDataStorage.getInstance().get(this);
        TownData otherPlayerTown = TownDataStorage.getInstance().get(playerToAdd);

        TownRelation currentRelation = playerTown.getRelationWith(otherPlayerTown);

        //If no relation, check if maybe they are from the same region TODO implement this inside TownData#getRelationWith()
        if (currentRelation == null && playerTown.haveOverlord() && otherPlayerTown.haveOverlord()) {
            currentRelation = playerTown.getOverlord().getRelationWith(otherPlayerTown.getOverlord());
        }

        return currentRelation;
    }

    public Integer getRegionRankID() {
        if(!hasRegion()){
            return null;
        }
        if (regionRankID == null)
            regionRankID = getRegion().getDefaultRankID();
        return regionRankID;
    }

    public void setRegionRankID(Integer rankID) {
        this.regionRankID = rankID;
    }

    public Integer getRankID(TerritoryData territoryData) {
        if (territoryData instanceof TownData) {
            return getTownRankID();
        } else if (territoryData instanceof RegionData) {
            return getRegionRankID();
        }
        return null;
    }

    public List<TerritoryData> getAllTerritoriesPlayerIsIn() {
        List<TerritoryData> territories = new ArrayList<>();
        if (hasTown()) {
            territories.add(getTown());
        }
        if (hasRegion()) {
            territories.add(getRegion());
        }
        return territories;
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getServer().getOfflinePlayer(getUUID());
    }

    public LangType getLang() {
        if (lang == null)
            return Lang.getChosenLang();
        return lang;
    }

    public void setLang(LangType lang) {
        this.lang = lang;
    }

    public void clearAllTownApplications() {
        TownInviteDataStorage.removeInvitation(UUID); //Remove town invitation
        for (TownData allTown : TownDataStorage.getInstance().getTownMap().values()) {
            allTown.removePlayerJoinRequest(UUID); //Remove applications
        }
    }

    public void setRankID(TerritoryData territoryData, Integer defaultRankID) {
        if(territoryData instanceof TownData){
            setTownRankID(defaultRankID);
        }
        if(territoryData instanceof RegionData){
            setRegionRankID(defaultRankID);
        }

    }
}
