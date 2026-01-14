package org.leralix.tan.dataclass;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.KingdomData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.invitation.TownInviteDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.timezone.TimeZoneEnum;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.war.War;
import org.leralix.tan.war.info.SideStatus;
import org.leralix.tan.war.legacy.CurrentAttack;
import org.leralix.tan.war.legacy.WarRole;

import java.util.*;


public class PlayerData implements ITanPlayer {

    private final String UUID;
    private String storedName;
    private Double Balance;
    private String TownId;
    private Integer townRankID;
    private Integer regionRankID;
    private String nationID;
    private Integer nationRankID;
    private List<String> propertiesListID;
    private List<String> attackInvolvedIn;
    private LangType lang;
    private TimeZoneEnum timeZone;

    public PlayerData(Player player) {
        this.UUID = player.getUniqueId().toString();
        this.storedName = player.getName();
        this.Balance = Constants.getStartingBalance();
        this.TownId = null;
        this.townRankID = null;
        this.regionRankID = null;
        this.nationID = null;
        this.nationRankID = null;
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
        TownData townData = getTown();
        if (townData == null) {
            return false;
        }
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
        var town = getTown();
        if (town == null)
            return false;
        return town.haveOverlord();
    }

    public RegionData getRegion() {
        if (!hasRegion())
            return null;
        return getTown().getRegion();
    }

    @Override
    public boolean hasKingdom() {
        RegionData region = getRegion();
        if (region == null) {
            return false;
        }
        return region.getOverlord().isPresent() && region.getOverlord().get() instanceof KingdomData;
    }

    @Override
    public KingdomData getKingdom() {
        if (!hasKingdom()) {
            return null;
        }
        return (KingdomData) getRegion().getOverlord().get();
    }

    @Override
    public RankData getKingdomRank() {
        if (!hasKingdom()) {
            return null;
        }
        return getKingdom().getRank(getKingdomRankID());
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

    public SideStatus getWarSideWith(TerritoryData territoryToCheck) {
        if (territoryToCheck == null) {
            return SideStatus.NEUTRAL;
        }

        SideStatus status = SideStatus.NEUTRAL;

        for (TerritoryData territoryOfPlayer : getAllTerritoriesPlayerIsIn()) {
            for (War war : WarStorage.getInstance().getWarsOfTerritory(territoryOfPlayer)) {

                WarRole role = war.getTerritoryRole(territoryToCheck);
                if (role == WarRole.NEUTRAL) {
                    continue;
                }

                WarRole playerRole = war.getTerritoryRole(territoryOfPlayer);
                if (role.isOpposite(playerRole)) {
                    return SideStatus.ENEMY;
                }

                status = SideStatus.ALLY;
            }
        }
        return status;
    }


    public void removeWar(@NotNull CurrentAttack currentAttacks) {
        getAttackInvolvedIn().remove(currentAttacks.getAttackData().getID());
    }

    @Override
    public TownRelation getRelationWithPlayer(Player otherPlayer) {
        return getRelationWithPlayer(PlayerDataStorage.getInstance().get(otherPlayer));
    }

    public TownRelation getRelationWithPlayer(ITanPlayer otherPlayer) {
        if (!hasTown() || !otherPlayer.hasTown())
            return TownRelation.NEUTRAL;

        TownData playerTown = getTown();
        TownData otherPlayerTown = otherPlayer.getTown();

        return playerTown.getRelationWith(otherPlayerTown);
    }

    public Integer getRegionRankID() {
        if(!hasRegion()){
            return null;
        }
        if (regionRankID == null)
            regionRankID = getRegion().getDefaultRankID();
        return regionRankID;
    }

    @Override
    public Integer getKingdomRankID() {
        if (!hasKingdom()) {
            return null;
        }
        if (kingdomRankID == null) {
            kingdomRankID = getKingdom().getDefaultRankID();
        }
        return kingdomRankID;
    }

    public void setRegionRankID(Integer rankID) {
        this.regionRankID = rankID;
    }

    @Override
    public void setKingdomRankID(Integer rankID) {
        this.kingdomRankID = rankID;
    }

    public Integer getRankID(TerritoryData territoryData) {
        if (territoryData instanceof TownData) {
            return getTownRankID();
        } else if (territoryData instanceof RegionData) {
            return getRegionRankID();
        } else if (territoryData instanceof KingdomData) {
            return getKingdomRankID();
        }
        return null;
    }

    @Override
    public RankData getRank(TerritoryData territoryData) {
        return territoryData.getRank(getRankID(territoryData));
    }

    public List<TerritoryData> getAllTerritoriesPlayerIsIn() {
        List<TerritoryData> territories = new ArrayList<>();
        if (hasTown()) {
            territories.add(getTown());
        }
        if (hasRegion()) {
            territories.add(getRegion());
        }
        if (hasNation()) {
            territories.add(getNation());
        }
        return territories;
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getServer().getOfflinePlayer(getUUID());
    }

    public LangType getLang() {
        if (lang == null)
            return Lang.getServerLang();
        return lang;
    }

    public void setLang(LangType lang) {
        this.lang = lang;
    }

    public void clearAllTownApplications() {
        TownInviteDataStorage.removeInvitation(UUID); //Remove town invitation
        for (TownData allTown : TownDataStorage.getInstance().getAll().values()) {
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
        if(territoryData instanceof org.leralix.tan.dataclass.territory.NationData){
            setNationRankID(defaultRankID);
        }

    }

    public TimeZoneEnum getTimeZone() {
        if(timeZone == null){
            return TimeZoneManager.getInstance().getTimezoneEnum();
        }
        return timeZone;
    }

    public void setTimeZone(TimeZoneEnum timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public Set<CurrentAttack> getCurrentAttacks() {
        Set<CurrentAttack> res = new HashSet<>();

        for(TerritoryData territoryData : getAllTerritoriesPlayerIsIn()){
            res.addAll(territoryData.getCurrentAttacks());
        }

        return res;
    }

    @Override
    public String getNationID() {
        return this.nationID;
    }

    @Override
    public org.leralix.tan.dataclass.territory.NationData getNation() {
        if (nationID == null || !org.leralix.tan.utils.constants.Constants.enableNation()) {
            return null;
        }
        return org.leralix.tan.storage.stored.NationDataStorage.getInstance().get(nationID);
    }

    @Override
    public boolean hasNation() {
        return nationID != null && org.leralix.tan.utils.constants.Constants.enableNation();
    }

    @Override
    public RankData getNationRank() {
        if (!hasNation())
            return null;
        return getNation().getRank(getNationRankID());
    }

    @Override
    public Integer getNationRankID() {
        return this.nationRankID;
    }

    @Override
    public void setNationRankID(Integer rankID) {
        this.nationRankID = rankID;
    }

}
