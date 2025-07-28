package org.leralix.tan.dataclass.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.position.Vector3D;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.*;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TownClaimedChunk;
import org.leralix.tan.dataclass.newhistory.PlayerTaxHistory;
import org.leralix.tan.dataclass.territory.economy.*;
import org.leralix.tan.dataclass.wars.CurrentWar;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.PlayerJoinTownAcceptedInternalEvent;
import org.leralix.tan.events.events.PlayerJoinTownRequestInternalEvent;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.gui.user.territory.TerritoryMemberMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.ClaimBlacklistStorage;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.StringUtil;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.utils.TeamUtils;

import java.util.*;


public class TownData extends TerritoryData {

    @Deprecated(since = "0.14.0", forRemoval = true)
    private String TownId;
    @Deprecated(since = "0.14.0", forRemoval = true)
    private Integer townDefaultRankID;
    @Deprecated(since = "0.14.0", forRemoval = true)
    private Long townDateTimeCreated;
    @Deprecated(since = "0.14.0", forRemoval = true)
    private Integer chunkColor;
    @Deprecated(since = "0.14.0", forRemoval = true)
    private String TownName;
    @Deprecated(since = "0.14.0", forRemoval = true)
    private Map<Integer, RankData> newRanks = new HashMap<>();

    @Deprecated(since = "0.14.4", forRemoval = true)
    private Double balance;

    //This is all that should be kept after the transition to the parent class
    private String UuidLeader;
    private String townTag;
    private boolean isRecruiting;
    private Level townLevel;
    private Collection<String> ownedLandmarks;
    private HashSet<String> PlayerJoinRequestSet;
    private Map<String, PropertyData> propertyDataMap;
    private TeleportationPosition teleportationPosition;
    private final HashSet<String> townPlayerListId;


    public TownData(String townId, String townName) {
        this(townId, townName, null); // Appelle le constructeur principal
    }

    public TownData(String townId, String townName, ITanPlayer leader) {
        super(townId, townName, leader);
        this.townLevel = new Level();
        this.ownedLandmarks = new ArrayList<>();
        this.PlayerJoinRequestSet = new HashSet<>();
        this.townPlayerListId = new HashSet<>();
        this.isRecruiting = false;

        if (leader != null) {
            this.UuidLeader = leader.getID();
            addPlayer(leader);
        }

        int prefixSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("prefixSize", 3);
        this.townTag = townName.length() >= prefixSize
                ? townName.substring(0, prefixSize).toUpperCase()
                : townName.toUpperCase();
    }

    //because old code was not using the centralized attribute
    @Deprecated(since = "0.15.0", forRemoval = true)
    protected Map<Integer, RankData> getOldRanks() {
        if (newRanks == null)
            newRanks = new HashMap<>();
        return newRanks;
    }

    @Override
    public RankData getRank(ITanPlayer tanPlayer) {
        return getRank(tanPlayer.getTownRankID());
    }

    public Level getLevel() {
        return townLevel;
    }

    public void addPlayer(String tanPlayerID) {
        addPlayer(PlayerDataStorage.getInstance().get(tanPlayerID));
    }

    public void addPlayer(ITanPlayer tanPlayer) {
        townPlayerListId.add(tanPlayer.getID());
        getTownDefaultRank().addPlayer(tanPlayer);
        tanPlayer.joinTown(this);

        Player newMember = tanPlayer.getPlayer();
        if (newMember != null)
            newMember.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_INVITATION_ACCEPTED_MEMBER_SIDE.get(getBaseColoredName()));

        tanPlayer.clearAllTownApplications();

        for (TerritoryData overlords : getOverlords()) {
            overlords.registerPlayer(tanPlayer);
        }

        EventManager.getInstance().callEvent(new PlayerJoinTownAcceptedInternalEvent(tanPlayer, this));
        TeamUtils.updateAllScoreboardColor();
        TownDataStorage.getInstance().saveStats();
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
        TownDataStorage.getInstance().saveStats();
    }

    @Override
    public Collection<String> getPlayerIDList() {
        return townPlayerListId;
    }

    @Override
    public Collection<ITanPlayer> getITanPlayerList() {
        ArrayList<ITanPlayer> ITanPlayerList = new ArrayList<>();
        for (String playerID : getPlayerIDList()) {
            ITanPlayerList.add(PlayerDataStorage.getInstance().get(playerID));
        }
        return ITanPlayerList;
    }

    @Override
    public ItemStack getIconWithName() {
        ItemStack itemStack = getIcon();

        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + getName());
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    @Override
    public ItemStack getIconWithInformations(LangType langType) {
        ItemStack icon = getIcon();

        ItemMeta meta = icon.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + getName());

            List<String> lore = new ArrayList<>();
            lore.add(Lang.GUI_TOWN_INFO_DESC0.get(langType, getDescription()));
            lore.add(Lang.GUI_TOWN_INFO_DESC1.get(langType, getLeaderName()));
            lore.add(Lang.GUI_TOWN_INFO_DESC2.get(langType, getPlayerIDList().size()));
            lore.add(Lang.GUI_TOWN_INFO_DESC3.get(langType, getNumberOfClaimedChunk()));
            lore.add(getOverlord()
                    .map(overlord -> Lang.GUI_TOWN_INFO_DESC5_REGION.get(langType, overlord.getName()))
                    .orElseGet(() -> Lang.GUI_TOWN_INFO_DESC5_NO_REGION.get(langType)))
            ;

            meta.setLore(lore);
            icon.setItemMeta(meta);
        }
        return icon;
    }


    @Override
    public String getOldID() {
        return this.TownId;
    }

    @Override
    public String getOldName() {
        return this.TownName;
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
    public double getOldBalance() {
        return StringUtil.handleDigits(balance);
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
    public void broadCastMessage(String message) {
        for (String playerId : townPlayerListId) {
            Player player = Bukkit.getServer().getPlayer(UUID.fromString(playerId));
            if (player != null && player.isOnline()) {
                player.sendMessage(message);
            }
        }
    }

    @Override
    public void broadcastMessageWithSound(String message, SoundEnum soundEnum, boolean addPrefix) {
        for (String playerId : townPlayerListId) {
            Player player = Bukkit.getPlayer(UUID.fromString(playerId));
            if (player != null && player.isOnline()) {
                SoundUtil.playSound(player, soundEnum);
                if (addPrefix)
                    player.sendMessage(TanChatUtils.getTANString() + message);
                else
                    player.sendMessage(message);
            }
        }
    }

    @Override
    public void broadcastMessageWithSound(String message, SoundEnum soundEnum) {
        broadcastMessageWithSound(message, soundEnum, true);
    }

    public RankData getTownDefaultRank() {
        return getRank(getDefaultRankID());
    }

    @Override
    public int getNumberOfRank() {
        return newRanks.size();
    }

    public boolean isFull() {
        return this.townPlayerListId.size() >= this.townLevel.getPlayerCap();
    }

    public boolean canClaimMoreChunk() {
        return this.getNumberOfClaimedChunk() < this.townLevel.getChunkCap();
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
            if (tanPlayer.getTownRankID() == null) { //TODO : Remove in v0.15.0, used to fixed missing rank application
                tanPlayer.joinTown(this);
            }

            if (!getRank(tanPlayer).isPayingTaxes())
                continue;

            double tax = getTax();

            if (EconomyUtil.getBalance(offlinePlayer) > tax) {
                EconomyUtil.removeFromBalance(offlinePlayer, tax);
                addToBalance(tax);
                TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new PlayerTaxHistory(this, tanPlayer, tax));
            } else {
                TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new PlayerTaxHistory(this, tanPlayer, -1));
            }
        }
    }


    @Override
    public double getChunkUpkeepCost() {
        return ConfigUtil.getCustomConfig(ConfigTag.MAIN).getDouble("TownChunkUpkeepCost", 0);
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

    public boolean isSpawnLocked() {
        return this.townLevel.getBenefitsLevel("UNLOCK_TOWN_SPAWN") <= 0;
    }

    @Override
    public void claimChunk(Player player, Chunk chunk) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player.getUniqueId().toString());


        if (ClaimBlacklistStorage.cannotBeClaimed(chunk)) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.CHUNK_IS_BLACKLISTED.get());
            return;
        }

        if (!doesPlayerHavePermission(tanPlayer, RolePermission.CLAIM_CHUNK)) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            return;
        }

        if (!canClaimMoreChunk()) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.MAX_CHUNK_LIMIT_REACHED.get());
            return;
        }


        int cost = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("CostOfTownChunk", 0);
        if (getBalance() < cost) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY_EXTENDED.get(cost - getBalance()));
            return;
        }

        ClaimedChunk2 chunkData = NewClaimedChunkStorage.getInstance().get(chunk);
        if (!chunkData.canTerritoryClaim(player, this)) {
            return;
        }

        if (getNumberOfClaimedChunk() != 0 &&
                !NewClaimedChunkStorage.getInstance().isOneAdjacentChunkClaimedBySameTown(chunk, getID()) &&
                !ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("TownAllowNonAdjacentChunks", false)) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.CHUNK_NOT_ADJACENT.get());
            return;
        }

        removeFromBalance(cost);
        NewClaimedChunkStorage.getInstance().unclaimChunk(chunk);
        NewClaimedChunkStorage.getInstance().claimTownChunk(chunk, getID());

        player.sendMessage(TanChatUtils.getTANString() + Lang.CHUNK_CLAIMED_SUCCESS.get(
                getNumberOfClaimedChunk(),
                getLevel().getChunkCap())
        );
        NewClaimedChunkStorage.getInstance().get(chunk);
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
    public String getCapitalID() {
        return null;
    }

    @Override
    public int getDefaultRankID() {
        if (this.defaultRankID == null)
            this.defaultRankID = townDefaultRankID;
        if (this.defaultRankID == null)
            this.defaultRankID = getRanks().values().iterator().next().getID();
        return defaultRankID;
    }

    @Override
    public List<GuiItem> getOrderedMemberList(ITanPlayer tanPlayer) {
        Player player = tanPlayer.getPlayer();
        List<GuiItem> res = new ArrayList<>();
        for (String playerUUID : getOrderedPlayerIDList()) {
            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            ITanPlayer playerIterateData = PlayerDataStorage.getInstance().get(playerUUID);
            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate,
                    Lang.GUI_TOWN_MEMBER_DESC1.get(playerIterateData.getTownRank().getColoredName()),
                    Lang.GUI_TOWN_MEMBER_DESC2.get(StringUtil.formatMoney(EconomyUtil.getBalance(playerIterate))),
                    doesPlayerHavePermission(tanPlayer, RolePermission.KICK_PLAYER) ? Lang.GUI_TOWN_MEMBER_DESC3.get() : "");

            GuiItem playerButton = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                if (event.getClick() == ClickType.RIGHT) {

                    ITanPlayer kickedPlayer = PlayerDataStorage.getInstance().get(playerIterate);
                    TownData townData = TownDataStorage.getInstance().get(tanPlayer);


                    if (!doesPlayerHavePermission(tanPlayer, RolePermission.KICK_PLAYER)) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                        return;
                    }
                    if (townData.getRank(kickedPlayer).isSuperiorTo(townData.getRank(tanPlayer))) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get());
                        return;
                    }
                    if (isLeader(kickedPlayer)) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_LEADER.get());
                        return;
                    }
                    if (tanPlayer.getID().equals(kickedPlayer.getID())) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_YOURSELF.get());
                        return;
                    }

                    PlayerGUI.openConfirmMenu(player, Lang.CONFIRM_PLAYER_KICKED.get(playerIterate.getName()),
                            confirmAction -> {
                                kickPlayer(playerIterate);
                                new TerritoryMemberMenu(player, this).open();

                            },
                            p -> new TerritoryMemberMenu(player, this).open()
                    );
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
        getOverlord()
                .ifPresent(overlord -> budget.addProfitLine(new OverlordTaxLine(this, overlord)));
        budget.addProfitLine(new PropertyRentTax(this));
        budget.addProfitLine(new PropertySellTax(this));
    }

    public Map<String, PropertyData> getPropertyDataMap() {
        if (this.propertyDataMap == null)
            this.propertyDataMap = new HashMap<>();
        return this.propertyDataMap;
    }

    public Collection<PropertyData> getProperties() {
        return getPropertyDataMap().values();
    }

    public String nextPropertyID() {
        if (getPropertyDataMap().isEmpty())
            return "P0";
        int size = getPropertyDataMap().size();
        int lastID = Integer.parseInt(getPropertyDataMap().values().stream().toList().get(size - 1).getTotalID().split("P")[1]);
        return "P" + (lastID + 1);
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
        if (this.townTag == null)
            setTownTag(this.TownName.substring(0, 3).toUpperCase());
        return this.townTag;
    }

    public void setTownTag(String townTag) {
        this.townTag = townTag;
    }

    public String getColoredTag() {
        return getChunkColor() + "[" + getTownTag() + "]";
    }

    public Collection<TownClaimedChunk> getClaims() {
        Collection<TownClaimedChunk> res = new ArrayList<>();
        for (ClaimedChunk2 claimedChunk : NewClaimedChunkStorage.getInstance().getClaimedChunksMap().values()) {
            if (claimedChunk instanceof TownClaimedChunk townClaimedChunk && townClaimedChunk.getOwnerID().equals(getID())) {
                res.add(townClaimedChunk);
            }

        }
        return res;
    }


    public void kickPlayer(OfflinePlayer kickedPlayer) {
        ITanPlayer kickedITanPlayer = PlayerDataStorage.getInstance().get(kickedPlayer);

        removePlayer(kickedITanPlayer);
        broadcastMessageWithSound(Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS.get(kickedPlayer.getName()), SoundEnum.BAD);

        if (kickedPlayer.isOnline())
            kickedPlayer.getPlayer().sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS_PLAYER.get());

    }


    public void upgradeTown(Player player) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        Level townLevel = this.getLevel();
        if (!doesPlayerHavePermission(tanPlayer, RolePermission.UPGRADE_TOWN)) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
            return;
        }
        if (this.getBalance() < townLevel.getMoneyRequiredForLevelUp()) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get());
            SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
            return;
        }

        removeFromBalance(townLevel.getMoneyRequiredForLevelUp());
        townLevel.townLevelUp();
        SoundUtil.playSound(player, SoundEnum.LEVEL_UP);
        player.sendMessage(TanChatUtils.getTANString() + Lang.BASIC_LEVEL_UP.get());
    }

    public void upgradeTown(Player player, TownUpgrade townUpgrade, int townUpgradeLevel) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

        if (!doesPlayerHavePermission(tanPlayer, RolePermission.UPGRADE_TOWN)) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
            return;
        }
        int cost = townUpgrade.getCost(townLevel.getUpgradeLevel(townUpgrade.getName()));
        if (this.getBalance() < cost) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY_EXTENDED.get(cost - this.getBalance()));
            SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
            return;
        }
        Level level = this.getLevel();
        if (level.getUpgradeLevel(townUpgrade.getName()) >= townUpgrade.getMaxLevel()) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_UPGRADE_MAX_LEVEL.get());
            SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
            return;
        }

        removeFromBalance(townUpgrade.getCost(townUpgradeLevel));
        level.levelUp(townUpgrade);
        SoundUtil.playSound(player, SoundEnum.LEVEL_UP);
        player.sendMessage(TanChatUtils.getTANString() + Lang.BASIC_LEVEL_UP.get());
    }

    public boolean haveNoLeader() {
        return this.UuidLeader == null;
    }

    public Collection<String> getOwnedLandmarksID() {
        if (ownedLandmarks == null)
            ownedLandmarks = new ArrayList<>();
        return ownedLandmarks;
    }

    public Collection<Landmark> getOwnedLandmarks() {
        Collection<Landmark> res = new ArrayList<>();
        for (String landmarkID : getOwnedLandmarksID()) {
            res.add(LandmarkStorage.getInstance().get(landmarkID));
        }
        return res;
    }

    public int getNumberOfOwnedLandmarks() {
        return getOwnedLandmarksID().size();
    }

    public void addLandmark(String landmarkID) {
        getOwnedLandmarksID().add(landmarkID);
    }

    public void addLandmark(Landmark landmark) {
        addLandmark(landmark.getID());
        landmark.setOwner(this);
    }

    public void removeLandmark(String landmarkID) {
        getOwnedLandmarksID().remove(landmarkID);
    }

    public void removeLandmark(Landmark landmark) {
        removeLandmark(landmark.getID());
        landmark.removeOwnership();
    }

    public boolean ownLandmark(Landmark landmark) {
        return getOwnedLandmarksID().contains(landmark.getID());
    }

    public boolean canClaimMoreLandmarks() {
        return getLevel().getTotalBenefits().get("MAX_LANDMARKS") > getNumberOfOwnedLandmarks();
    }


    @Override
    public boolean atWarWith(String territoryID) {
        for (CurrentWar plannedAttack : getAttacksInvolved()) {
            if (plannedAttack.getMainDefender().getID().equals(territoryID))
                return true;
        }
        return false;
    }


    public void removeAllLandmark() {
        for (String landmarkID : getOwnedLandmarksID()) {
            Landmark landmark = LandmarkStorage.getInstance().get(landmarkID);
            landmark.removeOwnership();
        }
    }

    @Override
    public void delete() {
        super.delete();

        if(haveOverlord()){
            RegionData regionData = getRegion();
            regionData.removeVassal(this);
        }

        removeAllLandmark(); //Remove all Landmark from the deleted town
        removeAllProperty(); //Remove all Property from the deleted town

        List<String> playersToRemove = new ArrayList<>(getPlayerIDList());
        for (String playerID : playersToRemove) {
            removePlayer(playerID); // Modification sécurisée après la boucle
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

    protected long getOldDateTime() {
        return townDateTimeCreated;
    }
}

