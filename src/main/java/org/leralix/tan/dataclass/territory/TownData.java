package org.leralix.tan.dataclass.territory;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.position.Vector3D;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.*;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TownClaimedChunk;
import org.leralix.tan.dataclass.newhistory.PlayerTaxHistory;
import org.leralix.tan.dataclass.territory.economy.*;
import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.newsletter.NewsletterStorage;
import org.leralix.tan.newsletter.news.PlayerJoinRequestNL;
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
    private Integer townDefaultRankID; //TODO : remove before v1.0.0
    @Deprecated(since = "0.14.0", forRemoval = true)
    private Long townDateTimeCreated;
    @Deprecated(since = "0.14.0", forRemoval = true)
    private Integer chunkColor;
    @Deprecated(since = "0.14.0", forRemoval = true)//TODO : remove before v1.0.0
    private String TownName;
    @Deprecated(since = "0.14.0", forRemoval = true)
    private Map<Integer, RankData> newRanks = new HashMap<>();

    @Deprecated(since = "0.14.0")
    //Transition has not yet been implemented. Do not remove balance before create a safe version to transfer the data to parent class
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

    public TownData(String townId, String townName, PlayerData leader) {
        super(townId, townName, leader != null ? leader.getID() : null);
        this.townLevel = new Level();
        this.ownedLandmarks = new ArrayList<>();
        this.PlayerJoinRequestSet = new HashSet<>();
        this.townPlayerListId = new HashSet<>();
        this.isRecruiting = false;
        this.balance = 0.0;

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
    public RankData getRank(PlayerData playerData) {
        return getRank(playerData.getTownRankID());
    }

    public String getLeaderName() {
        if (this.UuidLeader == null)
            return Lang.NO_LEADER.get();
        return Bukkit.getOfflinePlayer(UUID.fromString(this.UuidLeader)).getName();
    }

    public Level getLevel() {
        return townLevel;
    }

    public void addPlayer(String playerDataID) {
        addPlayer(PlayerDataStorage.getInstance().get(playerDataID));
    }

    public void addPlayer(PlayerData playerData) {
        townPlayerListId.add(playerData.getID());
        getTownDefaultRank().addPlayer(playerData);
        playerData.joinTown(this);

        Player newMember = playerData.getPlayer();
        if (newMember != null)
            newMember.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_INVITATION_ACCEPTED_MEMBER_SIDE.get(getColoredName()));

        playerData.clearAllTownApplications();

        for(TerritoryData overlords : getOverlords()){
            overlords.registerPlayer(playerData);
        }

        TeamUtils.updateAllScoreboardColor();
        TownDataStorage.getInstance().saveStats();
    }

    public void removePlayer(String playerDataID) {
        removePlayer(PlayerDataStorage.getInstance().get(playerDataID));
    }

    public void removePlayer(PlayerData playerData) {
        for(TerritoryData overlords : getOverlords()){
            overlords.unregisterPlayer(playerData);
        }

        getRank(playerData).removePlayer(playerData);
        townPlayerListId.remove(playerData.getID());
        playerData.leaveTown();
        TownDataStorage.getInstance().saveStats();
    }

    @Override
    public Collection<String> getPlayerIDList() {
        return townPlayerListId;
    }

    @Override
    public Collection<PlayerData> getPlayerDataList() {
        ArrayList<PlayerData> playerDataList = new ArrayList<>();
        for (String playerID : getPlayerIDList()) {
            playerDataList.add(PlayerDataStorage.getInstance().get(playerID));
        }
        return playerDataList;
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
            lore.add(haveOverlord() ? Lang.GUI_TOWN_INFO_DESC5_REGION.get(langType, getOverlord().getName()) : Lang.GUI_TOWN_INFO_DESC5_NO_REGION.get(langType));

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
    public String getColoredName() {
        return "§9" + getName();
    }

    @Override
    public String getLeaderID() {
        if (this.UuidLeader == null)
            return townPlayerListId.iterator().next(); //If the leader is null, the first player in the list is the leader
        return this.UuidLeader;
    }

    @Override
    public PlayerData getLeaderData() {
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
    public double getBalance() {
        return StringUtil.handleDigits(balance);
    }

    @Override
    protected Collection<TerritoryData> getOverlords() {
        List<TerritoryData> overlords = new ArrayList<>();

        if(haveOverlord()){
            RegionData regionData = getRegion();
            overlords.add(regionData);
            if(regionData.haveOverlord()){
                overlords.add(regionData.getOverlord());
            }
        }

        return overlords;
    }


    @Override
    public void addToBalance(double balance) {
        this.balance += balance;
    }

    @Override
    public void removeFromBalance(double balance) {
        this.balance -= balance;
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
        addPlayerJoinRequest(player.getUniqueId().toString());
        NewsletterStorage.registerNewsletter(new PlayerJoinRequestNL(player, this));
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

        for (PlayerData playerData : getPlayerDataList()) {
            OfflinePlayer offlinePlayer = playerData.getOfflinePlayer();
            if (playerData.getTownRankID() == null) { //TODO : Remove in v0.15.0, used to fixed missing rank application
                playerData.joinTown(this);
            }

            if (!getRank(playerData).isPayingTaxes())
                continue;

            double tax = getTax();

            if (EconomyUtil.getBalance(offlinePlayer) > tax) {
                EconomyUtil.removeFromBalance(offlinePlayer, tax);
                addToBalance(tax);
                TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new PlayerTaxHistory(this, playerData, tax));
            } else {
                TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new PlayerTaxHistory(this, playerData, -1));
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
    public Optional<ClaimedChunk2> claimChunkInternal(Player player, Chunk chunk) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player.getUniqueId().toString());


        if (ClaimBlacklistStorage.cannotBeClaimed(chunk)) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.CHUNK_IS_BLACKLISTED.get());
            return Optional.empty();
        }

        if (!doesPlayerHavePermission(playerData, RolePermission.CLAIM_CHUNK)) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            return Optional.empty();
        }

        if (!canClaimMoreChunk()) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.MAX_CHUNK_LIMIT_REACHED.get());
            return Optional.empty();
        }


        int cost = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("CostOfTownChunk", 0);
        if (getBalance() < cost) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY_EXTENDED.get(cost - getBalance()));
            return Optional.empty();
        }

        ClaimedChunk2 chunkData = NewClaimedChunkStorage.getInstance().get(chunk);
        if (!chunkData.canTerritoryClaim(Optional.of(player), this)) {
            return Optional.empty();
        }

        if (getNumberOfClaimedChunk() != 0 &&
                !NewClaimedChunkStorage.getInstance().isAdjacentChunkClaimedBySameTown(chunk, getID()) &&
                !ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("TownAllowNonAdjacentChunks", false)) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.CHUNK_NOT_ADJACENT.get());
            return Optional.empty();
        }

        removeFromBalance(cost);
        NewClaimedChunkStorage.getInstance().unclaimChunk(chunk);
        NewClaimedChunkStorage.getInstance().claimTownChunk(chunk, getID());

        player.sendMessage(TanChatUtils.getTANString() + Lang.CHUNK_CLAIMED_SUCCESS.get(
                getNumberOfClaimedChunk(),
                getLevel().getChunkCap())
        );
        return Optional.of(NewClaimedChunkStorage.getInstance().get(chunk));
    }

    public RegionData getRegion() {
        return RegionDataStorage.getInstance().get(this.overlordID);
    }


    @Override
    public Collection<TerritoryData> getPotentialVassals() {
        return Collections.emptyList();
    }

    public void removeOverlordPrivate() {
        for (PlayerData playerData : getPlayerDataList()) {
            playerData.setRegionRankID(null);
        }
    }

    @Override
    protected void addVassalPrivate(TerritoryData vassal) {
        //town have no vassals
    }

    @Override
    protected void removeVassal(String vassalID) {
        //Town have no vassals
    }


    @Override
    public boolean isCapital() {
        if (!haveOverlord())
            return false;
        return getOverlord().getCapital().getID().equals(getID());
    }

    @Override
    public String getCapitalID() {
        return null;
    }


    public boolean isRegionalCapital() {
        if (!haveOverlord())
            return false;
        return getOverlord().getCapitalID().equals(getID());
    }

    public boolean haveRelationWith(TownData otherTown) {
        return this.getRelationWith(otherTown) != null;
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
    public List<GuiItem> getOrderedMemberList(PlayerData playerData) {
        Player player = playerData.getPlayer();
        List<GuiItem> res = new ArrayList<>();
        for (String playerUUID : getOrderedPlayerIDList()) {
            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            PlayerData playerIterateData = PlayerDataStorage.getInstance().get(playerUUID);
            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate,
                    Lang.GUI_TOWN_MEMBER_DESC1.get(playerIterateData.getTownRank().getColoredName()),
                    Lang.GUI_TOWN_MEMBER_DESC2.get(StringUtil.formatMoney(EconomyUtil.getBalance(playerIterate))),
                    doesPlayerHavePermission(playerData, RolePermission.KICK_PLAYER) ? Lang.GUI_TOWN_MEMBER_DESC3.get() : "");

            GuiItem playerButton = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                if (event.getClick() == ClickType.RIGHT) {

                    PlayerData kickedPlayerData = PlayerDataStorage.getInstance().get(playerIterate);
                    TownData townData = TownDataStorage.getInstance().get(playerData);


                    if (!doesPlayerHavePermission(playerData, RolePermission.KICK_PLAYER)) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                        return;
                    }
                    if (townData.getRank(kickedPlayerData).isSuperiorTo(townData.getRank(playerData))) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get());
                        return;
                    }
                    if (isLeader(kickedPlayerData)) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_LEADER.get());
                        return;
                    }
                    if (playerData.getID().equals(kickedPlayerData.getID())) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_YOURSELF.get());
                        return;
                    }

                    PlayerGUI.openConfirmMenu(player, Lang.CONFIRM_PLAYER_KICKED.get(playerIterate.getName()),
                            confirmAction -> {
                                kickPlayer(playerIterate);
                                PlayerGUI.openMemberList(player, this);
                            },
                            p -> PlayerGUI.openMemberList(player, this));
                }
            });
            res.add(playerButton);
        }
        return res;
    }

    @Override
    protected void specificSetPlayerRank(PlayerData playerData, int rankID) {
        playerData.setTownRankID(rankID);
    }

    @Override
    protected void addSpecificTaxes(Budget budget) {
        budget.addProfitLine(new PlayerTaxLine(this));
        if (haveOverlord())
            budget.addProfitLine(new OverlordTaxLine(this));
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

    public PropertyData registerNewProperty(Vector3D p1, Vector3D p2, PlayerData owner) {
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

    @SuppressWarnings("unused") //API
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
        PlayerData kickedPlayerData = PlayerDataStorage.getInstance().get(kickedPlayer);

        removePlayer(kickedPlayerData);
        broadcastMessageWithSound(Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS.get(kickedPlayer.getName()), SoundEnum.BAD);

        if (kickedPlayer.isOnline())
            kickedPlayer.getPlayer().sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS_PLAYER.get());

    }


    public void upgradeTown(Player player) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        Level townLevel = this.getLevel();
        if (!doesPlayerHavePermission(playerData, RolePermission.UPGRADE_TOWN)) {
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
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);

        if (!doesPlayerHavePermission(playerData, RolePermission.UPGRADE_TOWN)) {
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
        for (PlannedAttack plannedAttack : getAttacksInvolved()) {
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
        broadcastMessageWithSound(Lang.BROADCAST_PLAYER_TOWN_DELETED.get(getLeaderName(), getColoredName()), SoundEnum.BAD);
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

    @Override
    public boolean isCapitalOf(String territoryID) {
        if (!haveOverlord())
            return false;
        return getOverlord().getCapitalID().equals(getID());
    }

    protected long getOldDateTime() {
        return townDateTimeCreated;
    }
}

