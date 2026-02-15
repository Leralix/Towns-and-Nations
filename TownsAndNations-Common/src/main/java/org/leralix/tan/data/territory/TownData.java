package org.leralix.tan.data.territory;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.data.chunk.ClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunk;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.economy.*;
import org.leralix.tan.data.territory.rank.RankData;
import org.leralix.tan.data.territory.teleportation.TeleportationPosition;
import org.leralix.tan.data.upgrade.rewards.numeric.TownPlayerCap;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.PlayerJoinTownAcceptedInternalEvent;
import org.leralix.tan.events.events.PlayerJoinTownRequestInternalEvent;
import org.leralix.tan.gui.common.PlayerGUI;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.database.transactions.TransactionManager;
import org.leralix.tan.storage.database.transactions.instance.PlayerTaxTransaction;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.Range;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.graphic.PrefixUtil;
import org.leralix.tan.utils.graphic.TeamUtils;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.buildings.TanLandmark;
import org.tan.api.interfaces.buildings.TanProperty;
import org.tan.api.interfaces.territory.TanTown;

import java.util.*;

public class TownData extends TerritoryData implements TanTown {

    //This is all that should be kept after the transition to the parent class
    private UUID UuidLeader;
    private String townTag;
    private boolean isRecruiting;
    private final Set<UUID> PlayerJoinRequestSet;
    private Map<String, PropertyData> propertyDataMap;
    private TeleportationPosition teleportationPosition;
    private final Set<UUID> townPlayerListId;
    private Vector2D capitalLocation;


    public TownData(String townId, String townName) {
        this(townId, townName, null); // Appelle le constructeur principal
    }

    public TownData(String townId, String townName, ITanPlayer leader) {
        super(townId, townName, leader);
        this.PlayerJoinRequestSet = new HashSet<>();
        this.townPlayerListId = new HashSet<>();
        this.isRecruiting = false;

        if (leader != null) {
            this.UuidLeader = leader.getID();
            addPlayer(leader);
        }

        Range prefixSizeRange = Constants.getPrefixSize();

        this.townTag = prefixSizeRange.isValueIn(townName.length()) ?
                townName.toUpperCase() :
                townName.substring(0, prefixSizeRange.getMaxVal()).toUpperCase();
    }

    @Override
    public Collection<TanProperty> getProperties() {
        return List.copyOf(getPropertiesInternal());
    }

    public Collection<PropertyData> getPropertiesInternal() {
        return getPropertyDataMap().values();
    }

    @Override
    public Collection<TanLandmark> getLandmarksOwned() {
        return List.copyOf(LandmarkStorage.getInstance().getLandmarkOf(this));
    }

    @Override
    public RankData getRank(ITanPlayer tanPlayer) {
        return getRank(tanPlayer.getTownRankID());
    }


    public void addPlayer(ITanPlayer tanNewPlayer) {
        townPlayerListId.add(tanNewPlayer.getID());
        getTownDefaultRank().addPlayer(tanNewPlayer);
        tanNewPlayer.joinTown(this);

        TanChatUtils.message(tanNewPlayer.getPlayer(), Lang.TOWN_INVITATION_ACCEPTED_MEMBER_SIDE.get(tanNewPlayer.getLang(), this.getColoredName()));

        tanNewPlayer.clearAllTownApplications();

        for (TerritoryData overlords : getOverlords()) {
            overlords.registerPlayer(tanNewPlayer);
        }

        EventManager.getInstance().callEvent(new PlayerJoinTownAcceptedInternalEvent(tanNewPlayer, this));
        TeamUtils.updateAllScoreboardColor();
        PrefixUtil.updatePrefix(tanNewPlayer.getPlayer());
        TownDataStorage.getInstance().save();
    }

    public void removePlayer(ITanPlayer tanPlayer) {
        for (TerritoryData overlords : getOverlords()) {
            overlords.unregisterPlayer(tanPlayer);
        }

        getRank(tanPlayer).removePlayer(tanPlayer);
        townPlayerListId.remove(tanPlayer.getID());
        tanPlayer.leaveTown();
        TownDataStorage.getInstance().save();
        PrefixUtil.updatePrefix(tanPlayer.getPlayer());
    }

    @Override
    public Collection<UUID> getPlayerIDList() {
        return townPlayerListId;
    }

    @Override
    public Collection<ITanPlayer> getITanPlayerList() {
        ArrayList<ITanPlayer> res = new ArrayList<>();
        for (UUID playerID : getPlayerIDList()) {
            res.add(PlayerDataStorage.getInstance().get(playerID));
        }
        return res;
    }

    @Override
    public IconBuilder getIconWithInformations(LangType langType) {

        return IconManager.getInstance().get(getIcon())
                .setName(ChatColor.GREEN + getName())
                .setDescription(
                        Lang.GUI_TOWN_INFO_DESC0.get(getDescription()),
                        Lang.GUI_TOWN_INFO_DESC1.get(getLeaderName()),
                        Lang.GUI_TOWN_INFO_DESC2.get(Integer.toString(getPlayerIDList().size())),
                        Lang.GUI_TOWN_INFO_DESC3.get(Integer.toString(getNumberOfClaimedChunk())),
                        getOverlordInternal().map(overlord -> Lang.GUI_TOWN_INFO_DESC5_REGION.get(overlord.getName()))
                                .orElseGet(Lang.GUI_TOWN_INFO_DESC5_NO_REGION::get)
                );
    }

    @Override
    public int getHierarchyRank() {
        return 0;
    }

    @Override
    public String getBaseColoredName() {
        return "§9" + getName();
    }

    @Override
    public UUID getLeaderID() {
        if (this.UuidLeader == null)
            return townPlayerListId.iterator().next(); //If the leader is null, the first player in the list is the leader
        return this.UuidLeader;
    }

    @Override
    public ITanPlayer getLeaderData() {
        return PlayerDataStorage.getInstance().get(this.UuidLeader);
    }

    @Override
    public void setLeaderID(UUID leaderID) {
        this.UuidLeader = leaderID;
    }


    @Override
    public boolean isLeader(UUID leaderID) {
        return getLeaderID().equals(leaderID);
    }

    @Override
    protected Collection<TerritoryData> getOverlords() {
        List<TerritoryData> overlords = new ArrayList<>();

        var optRegion = getRegion();

        if (optRegion.isPresent()) {
            RegionData regionData = optRegion.get();
            overlords.add(regionData);
            regionData.getOverlordInternal().ifPresent(overlords::add);
        }

        return overlords;
    }


    @Override
    public void broadCastMessage(FilledLang message) {
        for (UUID playerId : townPlayerListId) {
            Player player = Bukkit.getServer().getPlayer(playerId);
            if (player != null && player.isOnline()) {
                TanChatUtils.message(player, message.get(player));
            }
        }
    }

    @Override
    public void broadcastMessageWithSound(FilledLang message, SoundEnum soundEnum, boolean addPrefix) {
        for (UUID playerId : townPlayerListId) {
            Player player = Bukkit.getPlayer(playerId);
            TanChatUtils.message(player, message, soundEnum);
        }
    }

    @Override
    public void broadcastMessageWithSound(FilledLang message, SoundEnum soundEnum) {
        broadcastMessageWithSound(message, soundEnum, true);
    }

    public RankData getTownDefaultRank() {
        return getRank(getDefaultRankID());
    }


    public boolean isFull() {
        return !getNewLevel().getStat(TownPlayerCap.class).canDoAction(this.townPlayerListId.size());
    }

    public void addPlayerJoinRequest(Player player) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        EventManager.getInstance().callEvent(new PlayerJoinTownRequestInternalEvent(tanPlayer, this));
        addPlayerJoinRequest(tanPlayer.getID());
    }

    public void addPlayerJoinRequest(UUID playerID) {
        this.PlayerJoinRequestSet.add(playerID);
    }

    public void removePlayerJoinRequest(UUID playerID) {
        PlayerJoinRequestSet.remove(playerID);
    }

    public boolean isPlayerAlreadyRequested(String playerUUID) {
        return PlayerJoinRequestSet.contains(playerUUID);
    }

    public boolean isPlayerAlreadyRequested(Player player) {
        return isPlayerAlreadyRequested(player.getUniqueId().toString());
    }

    public Set<UUID> getPlayerJoinRequestSet() {
        return this.PlayerJoinRequestSet;
    }

    public boolean isRecruiting() {
        return isRecruiting;
    }

    public void swapRecruiting() {
        this.isRecruiting = !this.isRecruiting;
    }

    @Override
    protected void collectTaxes() {

        for (ITanPlayer tanPlayer : getITanPlayerList()) {
            OfflinePlayer offlinePlayer = tanPlayer.getOfflinePlayer();

            if (!getRank(tanPlayer).isPayingTaxes()) continue;

            double tax = getTax();
            double playerBalance = EconomyUtil.getBalance(offlinePlayer);

            //If player does not have enough money, take what they can give
            if (playerBalance < tax) {
                EconomyUtil.removeFromBalance(offlinePlayer, playerBalance);
                addToBalance(playerBalance);
                TransactionManager.getInstance().register(new PlayerTaxTransaction(this, tanPlayer.getID().toString(), playerBalance, false));
            } else {
                EconomyUtil.removeFromBalance(offlinePlayer, tax);
                addToBalance(tax);
                TransactionManager.getInstance().register(new PlayerTaxTransaction(this, tanPlayer.getID().toString(), tax, true));
            }
        }
    }

    public void setSpawn(Location location) {
        this.teleportationPosition = new TeleportationPosition(location);
    }

    public boolean isSpawnSet() {
        return this.teleportationPosition != null;
    }

    public TeleportationPosition getSpawn() {
        return this.teleportationPosition;
    }

    @Override
    public void abstractClaimChunk(Player player, Chunk chunk, boolean ignoreAdjacent) {

        removeFromBalance(getClaimCost());
        NewClaimedChunkStorage.getInstance().unclaimChunkAndUpdate(NewClaimedChunkStorage.getInstance().get(chunk));

        ClaimedChunk chunkClaimed = NewClaimedChunkStorage.getInstance().claimTownChunk(chunk, getID());
        //If this was the first claimed chunk, set the capital.
        if (getNumberOfClaimedChunk() == 1) {
            setCapitalLocation(chunkClaimed.getVector2D());
        }
    }


    public void setCapitalLocation(Vector2D vector2D) {
        capitalLocation = vector2D;
    }

    public Optional<Vector2D> getCapitalLocation() {
        return Optional.ofNullable(capitalLocation);
    }

    public Optional<RegionData> getRegion() {
        var optRegion = getOverlordInternal();
        if (optRegion.isPresent() && optRegion.get() instanceof RegionData regionData) {
            return Optional.of(regionData);
        }
        return Optional.empty();
    }

    public @Nullable String getRegionID(){
        return overlordID;
    }


    @Override
    public Collection<TerritoryData> getPotentialVassals() {
        return Collections.emptyList();
    }

    public void removeOverlordPrivate() {
        for (ITanPlayer tanPlayer : getITanPlayerList()) {
            tanPlayer.setRegionRankID(null);
        }
    }

    @Override
    protected void addVassalPrivate(TerritoryData vassal) {
        //town have no vassals
    }

    @Override
    protected void removeVassal(TerritoryData vassal) {
        //Town have no vassals
    }

    @Override
    public TerritoryData getCapital() {
        return null;
    }

    @Override
    protected void specificSetPlayerRank(ITanPlayer tanPlayer, int rankID) {
        tanPlayer.setTownRankID(rankID);
    }

    @Override
    protected void addSpecificTaxes(Budget budget) {
        budget.addProfitLine(new PlayerTaxLine(this));
        getOverlordInternal().ifPresent(overlord -> budget.addProfitLine(new OverlordTaxLine(this, overlord)));
        budget.addProfitLine(new PropertyRentTaxLine(this));
        budget.addProfitLine(new PropertySellTaxLine(this));
        budget.addProfitLine(new PropertyCreationTaxLine(this));
    }

    public Map<String, PropertyData> getPropertyDataMap() {
        if (this.propertyDataMap == null) this.propertyDataMap = new HashMap<>();
        return this.propertyDataMap;
    }

    public String nextPropertyID() {
        if (getPropertyDataMap().isEmpty()) return "P0";
        int size = getPropertyDataMap().size();
        int lastID = Integer.parseInt(getPropertyDataMap().values().stream().toList().get(size - 1).getTotalID().split("P")[1]);
        return "P" + (lastID + 1);
    }

    public PropertyData registerNewProperty(Vector3D p1, Vector3D p2) {
        String propertyID = nextPropertyID();
        String id = this.getID() + "_" + propertyID;
        PropertyData newProperty = new PropertyData(id, p1, p2, this);
        this.propertyDataMap.put(propertyID, newProperty);
        return newProperty;
    }

    public PropertyData registerNewProperty(Vector3D p1, Vector3D p2, ITanPlayer owner) {
        String propertyID = nextPropertyID();
        String id = this.getID() + "_" + propertyID;
        PropertyData newProperty = new PropertyData(id, p1, p2, owner);
        this.propertyDataMap.put(propertyID, newProperty);
        owner.addProperty(newProperty);
        return newProperty;
    }

    public PropertyData getProperty(String id) {
        return getPropertyDataMap().get(id);
    }

    public PropertyData getProperty(Location location) {
        for (PropertyData propertyData : getPropertyDataMap().values()) {
            if (propertyData.containsLocation(location)) {
                return propertyData;
            }
        }
        return null;
    }

    public void removeProperty(PropertyData propertyData) {
        this.propertyDataMap.remove(propertyData.getPropertyID());
    }

    public String getTownTag() {
        if (this.townTag == null) setTownTag(name.substring(0, 3).toUpperCase());
        return this.townTag;
    }

    public void setTownTag(String townTag) {
        this.townTag = townTag;
        applyToAllOnlinePlayer(PrefixUtil::updatePrefix);
    }

    public String getFormatedTag() {
        String tag = Constants.getTownTagFormat();
        tag = tag.replace("{townColor}", getChunkColor().toString());
        tag = tag.replace("{townTag}", getTownTag());
        return ChatColor.translateAlternateColorCodes('&', tag);
    }


    public void kickPlayer(OfflinePlayer kickedPlayer) {
        ITanPlayer kickedITanPlayer = PlayerDataStorage.getInstance().get(kickedPlayer);

        removePlayer(kickedITanPlayer);

        broadcastMessageWithSound(Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS.get(kickedPlayer.getName()), SoundEnum.BAD);
        Player player = kickedPlayer.getPlayer();
        TanChatUtils.message(player, Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS_PLAYER.get(kickedITanPlayer.getLang()), SoundEnum.BAD);
    }

    public boolean haveNoLeader() {
        return this.UuidLeader == null;
    }


    public void removeAllLandmark() {
        for (Landmark landmark : LandmarkStorage.getInstance().getLandmarkOf(this)) {
            landmark.removeOwnership();
        }
    }

    @Override
    public synchronized void delete() {
        super.delete();

        getRegion().ifPresent(regionData -> regionData.removeVassal(this));

        removeAllLandmark(); //Remove all Landmark from the deleted town
        removeAllProperty(); //Remove all Property from the deleted town

        for (ITanPlayer playerData : new ArrayList<>(getITanPlayerList())) {
            removePlayer(playerData);
        }

        TeamUtils.updateAllScoreboardColor();
        TownDataStorage.getInstance().deleteTown(this);


    }

    private void removeAllProperty() {
        for(TanProperty property : new ArrayList<>(getProperties())){
            property.delete();
        }
    }

    @Override
    public void openMainMenu(Player player, ITanPlayer playerData) {
        PlayerGUI.dispatchPlayerTown(player, playerData);
    }

    @Override
    public boolean canHaveVassals() {
        return false;
    }

    @Override
    public boolean canHaveOverlord() {
        return true;
    }

    @Override
    public Set<String> getVassalsID() {
        return Collections.emptySet();
    }

    public boolean isTownCapitalOccupied() {
        var optCapital = getCapitalLocation();
        if (optCapital.isPresent()) {
            var claimedChunk = NewClaimedChunkStorage.getInstance().get(optCapital.get().getChunk());
            return claimedChunk instanceof TerritoryChunk territoryChunk && territoryChunk.isOccupied();
        }
        return false;
    }
}

