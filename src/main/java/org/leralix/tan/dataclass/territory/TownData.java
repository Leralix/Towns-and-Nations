package org.leralix.tan.dataclass.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.dataclass.*;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.territory.economy.*;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.PlayerJoinTownAcceptedInternalEvent;
import org.leralix.tan.events.events.PlayerJoinTownRequestInternalEvent;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.gui.user.territory.TerritoryMemberMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.database.transactions.TransactionManager;
import org.leralix.tan.storage.database.transactions.instance.PlayerTaxTransaction;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.upgrade.rewards.numeric.TownPlayerCap;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.graphic.PrefixUtil;
import org.leralix.tan.utils.graphic.TeamUtils;
import org.leralix.tan.utils.text.StringUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.*;


public class TownData extends TerritoryData {

    //This is all that should be kept after the transition to the parent class
    private String UuidLeader;
    private String townTag;
    private boolean isRecruiting;
    /**
     * Keep to maintain backward compatibility until 0.17.0
     */
    @Deprecated(since = "0.16.0", forRemoval = true)
    Level townLevel;
    private HashSet<String> PlayerJoinRequestSet;
    private Map<String, PropertyData> propertyDataMap;
    private TeleportationPosition teleportationPosition;
    private final HashSet<String> townPlayerListId;
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

        int prefixSize = Constants.getPrefixSize();
        this.townTag = townName.length() >= prefixSize ? townName.substring(0, prefixSize).toUpperCase() : townName.toUpperCase();
    }

    @Override
    public RankData getRank(ITanPlayer tanPlayer) {
        return getRank(tanPlayer.getTownRankID());
    }


    public void addPlayer(ITanPlayer tanNewPlayer) {
        townPlayerListId.add(tanNewPlayer.getID());
        getTownDefaultRank().addPlayer(tanNewPlayer);
        tanNewPlayer.joinTown(this);

        TanChatUtils.message(tanNewPlayer.getPlayer(), Lang.TOWN_INVITATION_ACCEPTED_MEMBER_SIDE.get(tanNewPlayer.getLang(), getBaseColoredName()));

        tanNewPlayer.clearAllTownApplications();

        for (TerritoryData overlords : getOverlords()) {
            overlords.registerPlayer(tanNewPlayer);
        }

        EventManager.getInstance().callEvent(new PlayerJoinTownAcceptedInternalEvent(tanNewPlayer, this));
        TeamUtils.updateAllScoreboardColor();
        PrefixUtil.updatePrefix(tanNewPlayer.getPlayer());
        TownDataStorage.getInstance().save();
    }

    public void removePlayer(String tanPlayerID) {
        removePlayer(PlayerDataStorage.getInstance().get(tanPlayerID));
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
    public Collection<String> getPlayerIDList() {
        return townPlayerListId;
    }

    @Override
    public Collection<ITanPlayer> getITanPlayerList() {
        ArrayList<ITanPlayer> res = new ArrayList<>();
        for (String playerID : getPlayerIDList()) {
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
                        getOverlord().map(overlord -> Lang.GUI_TOWN_INFO_DESC5_REGION.get(overlord.getName()))
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
    public String getLeaderID() {
        if (this.UuidLeader == null)
            return townPlayerListId.iterator().next(); //If the leader is null, the first player in the list is the leader
        return this.UuidLeader;
    }

    @Override
    public ITanPlayer getLeaderData() {
        return PlayerDataStorage.getInstance().get(this.UuidLeader);
    }

    @Override
    public void setLeaderID(String leaderID) {
        this.UuidLeader = leaderID;
    }


    @Override
    public boolean isLeader(String leaderID) {
        return getLeaderID().equals(leaderID);
    }

    @Override
    protected Collection<TerritoryData> getOverlords() {
        List<TerritoryData> overlords = new ArrayList<>();

        if (haveOverlord()) {
            RegionData regionData = getRegion();
            overlords.add(regionData);
            regionData.getOverlord().ifPresent(overlords::add);
        }

        return overlords;
    }


    @Override
    public void broadCastMessage(FilledLang message) {
        for (String playerId : townPlayerListId) {
            Player player = Bukkit.getServer().getPlayer(UUID.fromString(playerId));
            if (player != null && player.isOnline()) {

                TanChatUtils.message(player, message.get(player));
            }
        }
    }

    @Override
    public void broadcastMessageWithSound(FilledLang message, SoundEnum soundEnum, boolean addPrefix) {
        for (String playerId : townPlayerListId) {
            Player player = Bukkit.getPlayer(UUID.fromString(playerId));
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

    public void addPlayerJoinRequest(String playerUUID) {
        this.PlayerJoinRequestSet.add(playerUUID);
    }

    public void removePlayerJoinRequest(String playerUUID) {
        PlayerJoinRequestSet.remove(playerUUID);
    }

    public void removePlayerJoinRequest(Player player) {
        removePlayerJoinRequest(player.getUniqueId().toString());
    }

    public boolean isPlayerAlreadyRequested(String playerUUID) {
        return PlayerJoinRequestSet.contains(playerUUID);
    }

    public boolean isPlayerAlreadyRequested(Player player) {
        return isPlayerAlreadyRequested(player.getUniqueId().toString());
    }

    public Set<String> getPlayerJoinRequestSet() {
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
                TransactionManager.getInstance().register(new PlayerTaxTransaction(this, tanPlayer.getID(), playerBalance, false));
            } else {
                EconomyUtil.removeFromBalance(offlinePlayer, tax);
                addToBalance(tax);
                TransactionManager.getInstance().register(new PlayerTaxTransaction(this, tanPlayer.getID(), tax, true));
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

        ClaimedChunk2 chunkClaimed = NewClaimedChunkStorage.getInstance().claimTownChunk(chunk, getID());
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

    public RegionData getRegion() {
        return RegionDataStorage.getInstance().get(this.overlordID);
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
    public List<GuiItem> getOrderedMemberList(ITanPlayer tanPlayer) {
        Player player = tanPlayer.getPlayer();
        List<GuiItem> res = new ArrayList<>();
        LangType langType = tanPlayer.getLang();

        for (String playerUUID : getOrderedPlayerIDList()) {
            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            ITanPlayer playerIterateData = PlayerDataStorage.getInstance().get(playerUUID);
            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate,
                    Lang.GUI_TOWN_MEMBER_DESC1.get(langType, playerIterateData.getTownRank().getColoredName()),
                    Lang.GUI_TOWN_MEMBER_DESC2.get(langType, StringUtil.formatMoney(EconomyUtil.getBalance(playerIterate))),
                    doesPlayerHavePermission(tanPlayer, RolePermission.KICK_PLAYER) ? Lang.GUI_TOWN_MEMBER_DESC3.get(langType) : "");

            GuiItem playerButton = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                if (event.getClick() == ClickType.RIGHT) {

                    ITanPlayer kickedPlayer = PlayerDataStorage.getInstance().get(playerIterate);
                    TownData townData = TownDataStorage.getInstance().get(tanPlayer);


                    if (!doesPlayerHavePermission(tanPlayer, RolePermission.KICK_PLAYER)) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(langType));
                        return;
                    }
                    if (townData.getRank(kickedPlayer).isSuperiorTo(townData.getRank(tanPlayer))) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get(langType));
                        return;
                    }
                    if (isLeader(kickedPlayer)) {
                        TanChatUtils.message(player, Lang.GUI_TOWN_MEMBER_CANT_KICK_LEADER.get(langType));
                        return;
                    }
                    if (tanPlayer.getID().equals(kickedPlayer.getID())) {
                        TanChatUtils.message(player, Lang.GUI_TOWN_MEMBER_CANT_KICK_YOURSELF.get(langType));
                        return;
                    }

                    PlayerGUI.openConfirmMenu(player, Lang.CONFIRM_PLAYER_KICKED.get(langType, playerIterate.getName()), confirmAction -> {
                        kickPlayer(playerIterate);
                        new TerritoryMemberMenu(player, this).open();

                    }, p -> new TerritoryMemberMenu(player, this).open());
                }
            });
            res.add(playerButton);
        }
        return res;
    }

    @Override
    protected void specificSetPlayerRank(ITanPlayer tanPlayer, int rankID) {
        tanPlayer.setTownRankID(rankID);
    }

    @Override
    protected void addSpecificTaxes(Budget budget) {
        budget.addProfitLine(new PlayerTaxLine(this));
        getOverlord().ifPresent(overlord -> budget.addProfitLine(new OverlordTaxLine(this, overlord)));
        budget.addProfitLine(new PropertyRentTaxLine(this));
        budget.addProfitLine(new PropertySellTaxLine(this));
        budget.addProfitLine(new PropertyCreationTaxLine(this));
    }

    public Map<String, PropertyData> getPropertyDataMap() {
        if (this.propertyDataMap == null) this.propertyDataMap = new HashMap<>();
        return this.propertyDataMap;
    }

    public Collection<PropertyData> getProperties() {
        return getPropertyDataMap().values();
    }

    public String nextPropertyID() {
        if (getPropertyDataMap().isEmpty()) return "P0";
        int size = getPropertyDataMap().size();
        int lastID = Integer.parseInt(getPropertyDataMap().values().stream().toList().get(size - 1).getTotalID().split("P")[1]);
        return "P" + (lastID + 1);
    }

    public PropertyData registerNewProperty(Vector3D p1, Vector3D p2, TerritoryData owner) {
        String propertyID = nextPropertyID();
        String id = this.getID() + "_" + propertyID;
        PropertyData newProperty = new PropertyData(id, p1, p2, owner);
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
        for (PropertyData propertyData : getProperties()) {
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

    public String getColoredTag() {
        return getChunkColor() + "[" + getTownTag() + "]";
    }


    public void kickPlayer(OfflinePlayer kickedPlayer) {
        ITanPlayer kickedITanPlayer = PlayerDataStorage.getInstance().get(kickedPlayer);

        removePlayer(kickedITanPlayer);

        broadcastMessageWithSound(Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS.get(kickedPlayer.getName()), SoundEnum.BAD);
        Player player = kickedPlayer.getPlayer();
        TanChatUtils.message(player,Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS_PLAYER.get(player), SoundEnum.BAD);
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

        if (haveOverlord()) {
            RegionData regionData = getRegion();
            regionData.removeVassal(this);
        }

        removeAllLandmark(); //Remove all Landmark from the deleted town
        removeAllProperty(); //Remove all Property from the deleted town

        List<String> playersToRemove = new ArrayList<>(getPlayerIDList());
        for (String playerID : playersToRemove) {
            removePlayer(playerID);
        }

        TeamUtils.updateAllScoreboardColor();
        TownDataStorage.getInstance().deleteTown(this);


    }

    private void removeAllProperty() {
        Iterator<PropertyData> iterator = getProperties().iterator();
        while (iterator.hasNext()) {
            PropertyData propertyData = iterator.next();
            propertyData.delete();
            iterator.remove();
        }
    }

    @Override
    public void openMainMenu(Player player) {
        PlayerGUI.dispatchPlayerTown(player);
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
    public List<String> getVassalsID() {
        return Collections.emptyList();
    }

    @Override
    public boolean isVassal(String territoryID) {
        return false;
    }
}

