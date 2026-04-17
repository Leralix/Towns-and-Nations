package org.leralix.tan.data.territory;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.data.chunk.ChunkData;
import org.leralix.tan.data.chunk.IClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunk;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.economy.*;
import org.leralix.tan.data.territory.rank.RankData;
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
import org.leralix.tan.utils.Range;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.graphic.PrefixUtil;
import org.leralix.tan.utils.graphic.TeamUtils;
import org.leralix.tan.utils.territory.ChunkUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.tan.api.interfaces.buildings.TanLandmark;
import org.tan.api.interfaces.buildings.TanProperty;

import java.util.*;

public class TownData extends TerritoryData implements Town {

    //This is all that should be kept after the transition to the parent class
    private UUID UuidLeader;
    private String townTag;
    private boolean isRecruiting;
    private final Set<UUID> PlayerJoinRequestSet;
    private Map<String, PropertyData> propertyDataMap;
    private final Set<UUID> townPlayerListId;
    private Vector2D capitalLocation;


    public TownData(String townId, String townName) {
        this(townId, townName, null);
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
    public Collection<PropertyData> getPropertiesInternal() {
        return getPropertyDataMap().values();
    }

    @Override
    public Collection<TanLandmark> getLandmarksOwned() {
        return List.copyOf(TownsAndNations.getPlugin().getLandmarkStorage().getLandmarkOf(this));
    }

    @Override
    public RankData getRank(ITanPlayer tanPlayer) {
        return getRank(tanPlayer.getTownRankID());
    }

    @Override
    public void addPlayer(ITanPlayer tanNewPlayer) {
        townPlayerListId.add(tanNewPlayer.getID());
        getTownDefaultRank().addPlayer(tanNewPlayer);
        tanNewPlayer.joinTown(this);

        TanChatUtils.message(tanNewPlayer.getPlayer(), Lang.TOWN_INVITATION_ACCEPTED_MEMBER_SIDE.get(tanNewPlayer.getLang(), this.getColoredName()));

        tanNewPlayer.clearAllTownApplications();

        for (Territory overlords : getOverlords()) {
            overlords.registerPlayer(tanNewPlayer);
        }

        EventManager.getInstance().callEvent(new PlayerJoinTownAcceptedInternalEvent(tanNewPlayer, this));
        TeamUtils.updateAllScoreboardColor();
        PrefixUtil.updatePrefix(tanNewPlayer.getPlayer());
    }

    @Override
    public void removePlayer(ITanPlayer tanPlayer) {
        for (Territory overlords : getOverlords()) {
            overlords.unregisterPlayer(tanPlayer);
        }

        getRank(tanPlayer).removePlayer(tanPlayer);
        townPlayerListId.remove(tanPlayer.getID());
        tanPlayer.leaveTown();
        Player player = tanPlayer.getPlayer();
        TeamUtils.setIndividualScoreBoard(player);
        PrefixUtil.updatePrefix(player);
    }

    @Override
    public Collection<UUID> getPlayerIDList() {
        return townPlayerListId;
    }

    @Override
    public Collection<ITanPlayer> getITanPlayerList() {
        ArrayList<ITanPlayer> res = new ArrayList<>();
        for (UUID playerID : getPlayerIDList()) {
            res.add(TownsAndNations.getPlugin().getPlayerDataStorage().get(playerID));
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
        return TownsAndNations.getPlugin().getPlayerDataStorage().get(this.UuidLeader);
    }

    @Override
    public void setLeaderID(UUID leaderID) {
        this.UuidLeader = leaderID;
    }


    @Override
    public boolean isLeader(UUID leaderID) {
        return getLeaderID().equals(leaderID);
    }

    protected Collection<Territory> getOverlords() {
        List<Territory> overlords = new ArrayList<>();

        var optRegion = getRegion();

        if (optRegion.isPresent()) {
            Region regionData = optRegion.get();
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
                TanChatUtils.message(player, message.get(TownsAndNations.getPlugin().getPlayerDataStorage().get(player)));
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

    @Override
    public RankData getTownDefaultRank() {
        return getRank(getDefaultRankID());
    }

    @Override
    public boolean isFull() {
        return !getNewLevel().getStat(TownPlayerCap.class).canDoAction(this.townPlayerListId.size());
    }

    @Override
    public void addPlayerJoinRequest(Player player) {
        ITanPlayer tanPlayer = TownsAndNations.getPlugin().getPlayerDataStorage().get(player);
        EventManager.getInstance().callEvent(new PlayerJoinTownRequestInternalEvent(tanPlayer, this));
        this.PlayerJoinRequestSet.add(player.getUniqueId());
    }

    @Override
    public void removePlayerJoinRequest(UUID playerID) {
        PlayerJoinRequestSet.remove(playerID);
    }

    @Override
    public boolean isPlayerAlreadyRequested(UUID playerUUID) {
        return PlayerJoinRequestSet.contains(playerUUID);
    }

    @Override
    public Set<UUID> getPlayerJoinRequestSet() {
        return this.PlayerJoinRequestSet;
    }

    @Override
    public boolean isRecruiting() {
        return isRecruiting;
    }

    @Override
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

    @Override
    public void abstractClaimChunk(Chunk chunk, boolean ignoreAdjacent) {

        removeFromBalance(getClaimCost());
        TownsAndNations.getPlugin().getClaimStorage().unclaimChunkAndUpdate(TownsAndNations.getPlugin().getClaimStorage().get(chunk));

        ChunkData chunkClaimed = TownsAndNations.getPlugin().getClaimStorage().claimTownChunk(chunk, getID());
        //If this was the first claimed chunk, set the capital.
        if (getNumberOfClaimedChunk() == 1) {
            setCapitalLocation(chunkClaimed.getVector2D());
        }
    }

    @Override
    protected boolean isPositionClaimable(Player player, Chunk chunk, IClaimedChunk chunkData, LangType langType) {

        // Rules are different for the first claimed chunk.
        if (getNumberOfClaimedChunk() == 0) {
            int bufferZone = Constants.territoryClaimBufferZone();
            // If the chunk is in the buffer zone of another territory, it cannot be claimed.
            if (ChunkUtil.isInBufferZone(chunkData, this, bufferZone)) {
                TanChatUtils.message(player, Lang.CHUNK_IN_BUFFER_ZONE.get(langType, Integer.toString(bufferZone)));
                return false;
            }
            return true;
        }

        // Else, the chunk must be adjacent to at least one chunk of the territory.
        if (!TownsAndNations.getPlugin().getClaimStorage().isOneAdjacentChunkClaimedBySameTerritory(chunk, getID())) {
            TanChatUtils.message(player, Lang.CHUNK_NOT_ADJACENT.get(langType));
            return false;
        }
        return true;
    }

    @Override
    public void setCapitalLocation(Vector2D vector2D) {
        capitalLocation = vector2D;
    }

    @Override
    public Optional<Vector2D> getCapitalLocation() {
        return Optional.ofNullable(capitalLocation);
    }

    @Override
    public Optional<Region> getRegion() {
        var optRegion = getOverlordInternal();
        if (optRegion.isPresent() && optRegion.get() instanceof Region regionData) {
            return Optional.of(regionData);
        }
        return Optional.empty();
    }

    @Override
    public @Nullable String getRegionID(){
        return overlordID;
    }


    @Override
    public Collection<Territory> getPotentialVassals() {
        return Collections.emptyList();
    }

    @Override
    public List<Territory> getSubjects() {
        return Collections.emptyList();
    }

    @Override
    public void removeOverlordPrivate() {
        for (ITanPlayer tanPlayer : getITanPlayerList()) {
            tanPlayer.setRegionRankID(null);
        }
    }

    @Override
    protected void addVassalPrivate(Territory vassal) {
        //town have no vassals
    }

    @Override
    public void removeVassal(Territory vassal) {
        //Town have no vassals
    }

    @Override
    public int getTotalPlayerCount() {
        return getPlayerIDList().size();
    }

    @Override
    public Territory getCapital() {
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

    @Override
    public Map<String, PropertyData> getPropertyDataMap() {
        if (this.propertyDataMap == null) this.propertyDataMap = new HashMap<>();
        return this.propertyDataMap;
    }

    @Override
    public String nextPropertyID() {
        if (getPropertyDataMap().isEmpty()) return "P0";
        int size = getPropertyDataMap().size();
        int lastID = Integer.parseInt(getPropertyDataMap().values().stream().toList().get(size - 1).getTotalID().split("P")[1]);
        return "P" + (lastID + 1);
    }

    @Override
    public PropertyData registerNewProperty(Vector3D p1, Vector3D p2) {
        String propertyID = nextPropertyID();
        String id = this.getID() + "_" + propertyID;
        PropertyData newProperty = new PropertyData(id, p1, p2, this);
        this.propertyDataMap.put(propertyID, newProperty);
        return newProperty;
    }

    @Override
    public PropertyData registerNewProperty(Vector3D p1, Vector3D p2, ITanPlayer owner) {
        String propertyID = nextPropertyID();
        String id = this.getID() + "_" + propertyID;
        PropertyData newProperty = new PropertyData(id, p1, p2, owner);
        this.propertyDataMap.put(propertyID, newProperty);
        owner.addProperty(newProperty);
        return newProperty;
    }

    @Override
    public PropertyData getProperty(String id) {
        return getPropertyDataMap().get(id);
    }

    @Override
    public PropertyData getProperty(Location location) {
        for (PropertyData propertyData : getPropertyDataMap().values()) {
            if (propertyData.containsLocation(location)) {
                return propertyData;
            }
        }
        return null;
    }

    @Override
    public void removeProperty(PropertyData propertyData) {
        this.propertyDataMap.remove(propertyData.getPropertyID());
    }

    @Override
    public String getTownTag() {
        if (this.townTag == null) setTownTag(name.substring(0, 3).toUpperCase());
        return this.townTag;
    }

    @Override
    public void setTownTag(String townTag) {
        this.townTag = townTag;
        applyToAllOnlinePlayer(PrefixUtil::updatePrefix);
    }

    @Override
    public String getFormatedTag() {
        String tag = Constants.getTownTagFormat();
        tag = tag.replace("{townColor}", getChunkColor().toString());
        tag = tag.replace("{townTag}", getTownTag());
        return ChatColor.translateAlternateColorCodes('&', tag);
    }

    @Override
    public void kickPlayer(OfflinePlayer kickedPlayer) {
        ITanPlayer kickedITanPlayer = TownsAndNations.getPlugin().getPlayerDataStorage().get(kickedPlayer);

        removePlayer(kickedITanPlayer);

        broadcastMessageWithSound(Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS.get(kickedPlayer.getName()), SoundEnum.BAD);
        Player player = kickedPlayer.getPlayer();
        TanChatUtils.message(player, Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS_PLAYER.get(kickedITanPlayer.getLang()), SoundEnum.BAD);
    }

    @Override
    public boolean haveNoLeader() {
        return this.UuidLeader == null;
    }

    @Override
    public void removeAllLandmark() {
        for (Landmark landmark : TownsAndNations.getPlugin().getLandmarkStorage().getLandmarkOf(this)) {
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
        TownsAndNations.getPlugin().getTownStorage().deleteTown(this);


    }

    @Override
    public void removeAllProperty() {
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

    @Override
    public boolean isTownCapitalOccupied() {
        var optCapital = getCapitalLocation();
        if (optCapital.isPresent()) {
            var claimedChunk = TownsAndNations.getPlugin().getClaimStorage().get(optCapital.get().getChunk());
            return claimedChunk instanceof TerritoryChunk territoryChunk && territoryChunk.isOccupied();
        }
        return false;
    }
}

