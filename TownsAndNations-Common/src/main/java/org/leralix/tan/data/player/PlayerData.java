package org.leralix.tan.data.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.api.internal.wrappers.WarWrapper;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.data.territory.*;
import org.leralix.tan.data.territory.rank.RankData;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.data.timezone.TimeZoneEnum;
import org.leralix.tan.data.timezone.TimeZoneManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.invitation.TownInviteDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.war.War;
import org.leralix.tan.war.attack.CurrentAttack;
import org.leralix.tan.war.info.SideStatus;
import org.leralix.tan.war.info.WarRole;
import org.tan.api.interfaces.buildings.TanProperty;
import org.tan.api.interfaces.war.TanWar;

import java.util.*;
import java.util.stream.Collectors;


public class PlayerData implements ITanPlayer {

    //Removing UUID "string" in favor of id "UUID"
    @Deprecated(since = "0.17.0", forRemoval = true)
    private String UUID;
    private UUID id;
    private String storedName;
    private Double Balance;
    private String TownId;
    private Integer townRankID;
    private Integer regionRankID;
    private Integer nationRankID;
    private List<String> propertiesListID;
    private List<String> attackInvolvedIn;
    private LangType lang;
    private TimeZoneEnum timeZone;

    public PlayerData(OfflinePlayer player) {
        this.id = player.getUniqueId();
        this.storedName = player.getName();
        this.Balance = Constants.getStartingBalance();
        this.TownId = null;
        this.townRankID = null;
        this.regionRankID = null;
        this.nationRankID = null;
        this.propertiesListID = new ArrayList<>();
        this.attackInvolvedIn = new ArrayList<>();
    }

    public PlayerData(
            UUID id,
            String storedName,
            Double balance,
            String townId,
            Integer townRankID,
            Integer regionRankID,
            Integer nationRankID,
            List<String> propertiesListID,
            List<String> attackInvolvedIn,
            LangType lang,
            TimeZoneEnum timeZone
    ) {
        this.id = id;
        this.storedName = storedName;
        Balance = balance;
        TownId = townId;
        this.townRankID = townRankID;
        this.regionRankID = regionRankID;
        this.nationRankID = nationRankID;
        this.propertiesListID = propertiesListID;
        this.attackInvolvedIn = attackInvolvedIn;
        this.lang = lang;
        this.timeZone = timeZone;
    }

    public UUID getID() {
        if(id == null){
            id = java.util.UUID.fromString(UUID);
        }
        return id;
    }

    public String getNameStored() {
        if (storedName == null) {
            OfflinePlayer offlinePlayer = getOfflinePlayer();
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

    public Town getTown() {
        return TownsAndNations.getPlugin().getTownStorage().get(this.TownId);
    }

    public boolean hasTown() {
        return this.TownId != null;
    }

    public boolean isTownOverlord() {
        if (!hasTown())
            return false;
        Town townData = getTown();
        if (townData == null) {
            return false;
        }
        return getTown().isLeader(getID());
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

    public Region getRegion() {
        if (!hasRegion())
            return null;
        return getTown().getRegion().orElse(null);
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

            PropertyData nextProperty = TownsAndNations.getPlugin().getTownStorage().get(tID).getProperty(pID);

            propertyDataList.add(nextProperty);
        }

        return propertyDataList;
    }

    @Override
    public Collection<TanProperty> getPropertiesOwned() {
        return List.copyOf(getProperties());
    }

    @Override
    public Collection<TanProperty> getPropertiesRented() {
        List<TanProperty> properties = new ArrayList<>();

        for(Town town : TownsAndNations.getPlugin().getTownStorage().getAll().values()){
            for(PropertyData property : town.getPropertiesInternal()){
                if(!property.isRented()){
                    continue;
                }
                if(getID().equals(property.getRenterID())){
                    properties.add(property);
                }
            }
        }
        return properties;
    }

    @Override
    public Collection<TanProperty> getPropertiesForSale() {
        return getProperties().stream()
                .filter(PropertyData::isForSale)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<TanWar> getWarsParticipatingIn() {
        List<TanWar> wars = new ArrayList<>();
        for(Territory territoryData : getAllTerritoriesPlayerIsIn()){
            wars.addAll(TownsAndNations.getPlugin().getWarStorage().getWarsOfTerritory(territoryData).stream().map(WarWrapper::new).toList());
        }
        return wars;
    }

    public void removeProperty(PropertyData propertyData) {
        this.propertiesListID.remove(propertyData.getTotalID());
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getID());
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

    public SideStatus getWarSideWith(Territory territoryToCheck) {
        if (territoryToCheck == null) {
            return SideStatus.NEUTRAL;
        }

        SideStatus status = SideStatus.NEUTRAL;

        for (Territory territoryOfPlayer : getAllTerritoriesPlayerIsIn()) {
            for (War war : TownsAndNations.getPlugin().getWarStorage().getWarsOfTerritory(territoryOfPlayer)) {

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

    public TownRelation getRelationWithPlayer(ITanPlayer otherPlayer) {
        if (!hasTown() || !otherPlayer.hasTown())
            return TownRelation.NEUTRAL;

        Town playerTown = getTown();
        Town otherPlayerTown = otherPlayer.getTown();

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
    public void setRegionRankID(Integer rankID) {
        regionRankID = rankID;
    }

    @Override
    public RankData getRank(Territory territoryData) {
        return territoryData.getRank(getRankID(territoryData));
    }

    public List<Territory> getAllTerritoriesPlayerIsIn() {
        List<Territory> territories = new ArrayList<>();
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
        return Bukkit.getServer().getOfflinePlayer(getID());
    }

    public @NotNull LangType getLang() {
        if (lang == null)
            return Lang.getServerLang();
        return lang;
    }

    public void setLang(LangType lang) {
        this.lang = lang;
    }

    public void clearAllTownApplications() {
        TownInviteDataStorage.removeInvitation(getID()); //Remove town invitation
        for (Town allTown : TownsAndNations.getPlugin().getTownStorage().getAll().values()) {
            allTown.removePlayerJoinRequest(getID()); //Remove applications
        }
    }

    public void setRankID(TerritoryData territoryData, Integer newRank) {
        if(territoryData instanceof Town){
            setTownRankID(newRank);
        }
        if(territoryData instanceof Region){
            setRegionRankID(newRank);
        }
        if(territoryData instanceof Nation){
            setNationRankID(newRank);
        }

    }

    public @NotNull TimeZoneEnum getTimeZone() {
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

        for(Territory territoryData : getAllTerritoriesPlayerIsIn()){
            res.addAll(territoryData.getCurrentAttacks());
        }

        return res;
    }

    @Override
    public Player getOnlinePlayer() {
        return Bukkit.getPlayer(getID());
    }

    @Override
    public Nation getNation() {

        var optTown = getTown();
        if(optTown == null){
            return null;
        }
        var optRegion = optTown.getRegion();
        return optRegion.flatMap(Region::getNation).orElse(null);
    }

    @Override
    public boolean hasNation() {
        return getNation() != null;
    }

    @Override
    public RankData getNationRank() {
        if (!hasNation())
            return null;
        return getNation().getRank(getNationRankID());
    }

    @Override
    public Integer getNationRankID() {
        if(!hasNation()){
            return null;
        }
        if (nationRankID == null)
            nationRankID = getNation().getDefaultRankID();
        return nationRankID;
    }

    @Override
    public void setNationRankID(Integer rankID) {
        this.nationRankID = rankID;
    }

    @Override
    public Integer getRankID(Territory territoryData) {
        if(territoryData instanceof Town){
            return getTownRankID();
        }
        if(territoryData instanceof Region){
            return getRegionRankID();
        }
        if(territoryData instanceof Nation){
            return getNationRankID();
        }
        return null;
    }

}
