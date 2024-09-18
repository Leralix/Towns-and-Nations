package org.tan.TownsAndNations.DataClass.territoryData;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.tan.TownsAndNations.DataClass.*;
import org.tan.TownsAndNations.DataClass.History.*;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.DataClass.newChunkData.TownClaimedChunk;
import org.tan.TownsAndNations.DataClass.wars.PlannedAttack;
import org.tan.TownsAndNations.DataClass.wars.CurrentAttacks;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.*;
import org.tan.TownsAndNations.storage.CurrentAttacksStorage;
import org.tan.TownsAndNations.storage.DataStorage.*;
import org.tan.TownsAndNations.storage.PlayerChatListenerStorage;
import org.tan.TownsAndNations.utils.*;

import java.util.*;

import static org.tan.TownsAndNations.TownsAndNations.isSQLEnabled;
import static org.tan.TownsAndNations.enums.SoundEnum.*;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;
import static org.tan.TownsAndNations.utils.HeadUtils.getPlayerHead;
import static org.tan.TownsAndNations.utils.TeamUtils.updateAllScoreboardColor;

public class TownData extends ITerritoryData {

    private final String TownId;
    private String TownName;
    private String UuidLeader;
    private Integer townDefaultRankID;
    private String Description;
    private Long dateTimeCreated;
    private String townIconMaterialCode;
    private String regionID;
    private boolean isRecruiting;
    private Integer balance;
    private Integer flatTax;
    private Integer chunkColor;
    private String townTag;

    private ChunkHistory chunkHistory;
    private DonationHistory donationHistory;
    private MiscellaneousHistory miscellaneousHistory;
    private SalaryHistory salaryHistory;
    private TaxHistory taxHistory;

    private final HashSet<String> townPlayerListId = new HashSet<>();
    private HashMap<Integer,TownRank> newRanks = new HashMap<>();
    private Collection<String> ownedLandmarks = new ArrayList<>();

    private HashSet<String> PlayerJoinRequestSet = new HashSet<>();
    private Map<String, PropertyData> propertyDataMap;

    private TownLevel townLevel = new TownLevel();;
    private ClaimedChunkSettings chunkSettings = new ClaimedChunkSettings();;
    private TownRelations relations = new TownRelations();;

    private TeleportationPosition teleportationPosition;


    //First time creating a town
    public TownData(String townId, String townName, String leaderID){
        this.TownId = townId;
        this.UuidLeader = leaderID;
        this.TownName = townName;
        this.Description = "default description";
        this.dateTimeCreated = new Date().getTime();
        this.townIconMaterialCode = null;
        this.isRecruiting = false;
        this.balance = 0;
        this.flatTax = 1;
        this.townDefaultRankID = 0;
        this.townTag = townName.substring(0,3).toUpperCase();
        this.chunkColor = StringUtil.randomColor();

        this.chunkHistory = new ChunkHistory();
        this.donationHistory = new DonationHistory();
        this.miscellaneousHistory = new MiscellaneousHistory();
        this.salaryHistory = new SalaryHistory();
        this.taxHistory = new TaxHistory();

        addRank("default");
        if(leaderID != null)
            addPlayer(leaderID);
    }

    //used for sql, loading a town
    public TownData(String townId, String townName, String leaderID, String description, String dateCreated,
                    String townIconMaterialCode, int townDefaultRankID, long dateTimeCreated, Boolean isRecruiting, int balance,
                    int flatTax, int chunkColor, HashMap<Integer, TownRank> newRanks){
        this.TownId = townId;
        this.TownName = townName;
        this.UuidLeader = leaderID;
        this.Description = description;
        this.townIconMaterialCode = townIconMaterialCode;
        this.dateTimeCreated = dateTimeCreated;
        this.newRanks = newRanks;
        this.PlayerJoinRequestSet= new HashSet<>();
        this.townPlayerListId.add(leaderID);
        this.townDefaultRankID = townDefaultRankID;
        this.isRecruiting = isRecruiting;
        this.balance = balance;
        this.flatTax = flatTax;
        this.chunkColor = chunkColor;

        this.relations = null;
        this.chunkSettings = null;
        this.townLevel = null;

    }

    public String getLeaderName() {
        if(this.UuidLeader == null)
            return Lang.NO_LEADER.get();
        return Bukkit.getOfflinePlayer(UUID.fromString(this.UuidLeader)).getName();
    }

    public boolean isRankNameUsed(String message) {
        if(ConfigUtil.getCustomConfig("config.yml").getBoolean("AllowNameDuplication",false))
            return false;

        for (TownRank rank : this.getRanks()) {
            if (rank.getName().equals(message)) {
                return true;
            }
        }
        return false;
    }

    public void removeRank(int key){
        this.newRanks.remove(key);
    }

    public long getDateTimeCreated() {
        if(this.dateTimeCreated == null)
            this.dateTimeCreated = new Date().getTime();
        return this.dateTimeCreated;
    }
    public TownLevel getTownLevel() {
        return townLevel;
    }

    public void addPlayer(String playerDataID){
        PlayerData playerData = PlayerDataStorage.get(playerDataID);
        addPlayer(playerData);
    }

    public void addPlayer(PlayerData playerData){
        townPlayerListId.add(playerData.getID());
        getTownDefaultRank().addPlayer(playerData.getID());
        playerData.joinTown(this);
        TownDataStorage.saveStats();
    }

    public void removePlayer(PlayerData playerData){
        getRank(playerData).removePlayer(playerData);
        townPlayerListId.remove(playerData.getID());
        playerData.leaveTown();

        TownDataStorage.saveStats();
    }

    public Collection<String> getPlayerIDList(){
        return townPlayerListId;
    }

    @Override
    public Collection<PlayerData> getPlayerDataList(){
        ArrayList<PlayerData> playerDataList = new ArrayList<>();
        for (String playerID : getPlayerIDList()){
            playerDataList.add(PlayerDataStorage.get(playerID));
        }
        return playerDataList;
    }

    @Override
    public ItemStack getIcon(){
        ItemStack itemStack = getIconItem();

        ItemMeta meta = itemStack.getItemMeta();
        if(meta != null){
            meta.setDisplayName(ChatColor.GREEN + getName());
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    @Override
    public ItemStack getIconWithInformations(){
        ItemStack icon = getIcon();

        ItemMeta meta = icon.getItemMeta();
        if(meta != null){
            meta.setDisplayName(ChatColor.GREEN + getName());

            List<String> lore = new ArrayList<>();
            lore.add(Lang.GUI_TOWN_INFO_DESC0.get(getDescription()));
            lore.add("");
            lore.add(Lang.GUI_TOWN_INFO_DESC1.get(getLeaderName()));
            lore.add(Lang.GUI_TOWN_INFO_DESC2.get(getPlayerIDList().size()));
            lore.add(Lang.GUI_TOWN_INFO_DESC3.get(getNumberOfClaimedChunk()));
            lore.add(haveOverlord()? Lang.GUI_TOWN_INFO_DESC5_REGION.get(getOverlord().getName()): Lang.GUI_TOWN_INFO_DESC5_NO_REGION.get());

            meta.setLore(lore);
            icon.setItemMeta(meta);
        }
        return icon;
    }

    @Override
    public ItemStack getIconWithInformationAndRelation(ITerritoryData ownTerritory){
        ItemStack icon = getIconWithInformations();

        ItemMeta meta = icon.getItemMeta();
        if(meta != null){
            List<String> lore = meta.getLore();

            if(ownTerritory != null){
                TownRelation relation = getRelationWith(ownTerritory);
                String relationName;
                if(relation == null){
                    relationName = Lang.GUI_TOWN_RELATION_NEUTRAL.get();
                }
                else {
                    relationName = relation.getColor() + relation.getName();
                }
                lore.add(Lang.GUI_TOWN_INFO_TOWN_RELATION.get(relationName));
            }

            meta.setLore(lore);
            icon.setItemMeta(meta);
        }
        return icon;
    }
    //////////////////////////////////////
    //          ITerritoryData          //
    //////////////////////////////////////

    @Override
    public String getID() {
        return this.TownId;
    }

    @Override
    public String getName(){
        return this.TownName;
    }

    @Override
    public int getRank() {
        return 0;
    }

    @Override
    public String getColoredName() {
        return "§9" + getName();
    }

    @Override
    public void rename(Player player, int townCost, String newName) {
        if(getBalance() <= townCost){
            player.sendMessage(ChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get());
            return;
        }

        PlayerChatListenerStorage.removePlayer(player);
        player.sendMessage(ChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get(this.getName(),newName));
        getMiscellaneousHistory().add(Lang.GUI_TOWN_SETTINGS_NEW_TOWN_NAME_HISTORY.get(this.getName() ,newName),townCost);
        removeFromBalance(townCost);
        FileUtil.addLineToHistory(Lang.HISTORY_TOWN_NAME_CHANGED.get(player.getName(),this.getName(),newName));
        this.TownName = newName;
    }

    public TownRank addRank(String rankName)    {
        int nextRankId = 0;
        for(TownRank rank : this.getRanks()){
            if(rank.getID() >= nextRankId)
                nextRankId = rank.getID() + 1;
        }

        TownRank newRank = new TownRank(nextRankId, rankName);
        this.newRanks.put(nextRankId,newRank);
        return newRank;
    }

    @Override
    public String getLeaderID() {
        return this.UuidLeader;
    }

    @Override
    public PlayerData getLeaderData() {
        return PlayerDataStorage.get(this.UuidLeader);
    }

    @Override
    public void setLeaderID(String leaderID) {
        this.UuidLeader = leaderID;
    }


    @Override
    public boolean isLeader(String leaderID){
        return this.UuidLeader.equals(leaderID);
    }

    public boolean isLeader(@NotNull Player player){
        return isLeader(player.getUniqueId().toString());
    }

    @Override
    public String getDescription() {
        return this.Description;
    }

    @Override
    public void setDescription(String description) {
        this.Description = description;
    }

    @Override
    public ItemStack getIconItem() {
        if(haveNoLeader()){
            return new ItemStack(Material.SKELETON_SKULL);
        }
        if(this.townIconMaterialCode == null){
            return getPlayerHead(getName(), Bukkit.getOfflinePlayer(UUID.fromString(getLeaderID())));
        }
        Material material = Material.getMaterial(townIconMaterialCode);
        if(material == null)
            return null;
        else
            return new ItemStack(material);
    }

    @Override
    public void setIconMaterial(Material material) {
        this.townIconMaterialCode = material.name();
    }

    @Override
    public boolean havePlayer(PlayerData player){
        return havePlayer(player.getID());
    }

    @Override
    public boolean havePlayer(String playerID){
        return this.townPlayerListId.contains(playerID);
    }

    //////////////////////////////////////
    //             IRelation            //
    //////////////////////////////////////
    @Override
    public TownRelations getRelations(){
        if(this.relations == null)
            this.relations = new TownRelations();
        return relations;
    }

    @Override
    public void addRelation(TownRelation relation, ITerritoryData territoryData){
        addRelation(relation,territoryData.getID());
    }

    @Override
    public void addRelation(TownRelation relation, String territoryID){
        this.relations.addRelation(relation,territoryID);
    }

    @Override
    public void removeRelation(TownRelation relation, ITerritoryData townData) {
        removeRelation(relation,townData.getID());
    }

    @Override
    public void removeRelation(TownRelation relation, String townId) {
        this.relations.removeRelation(relation,townId);
    }

    @Override
    public ClaimedChunkSettings getChunkSettings() {
        if(chunkSettings == null)
            chunkSettings = new ClaimedChunkSettings();
        return chunkSettings;
    }

    @Override
    public TownRelation getRelationWith(ITerritoryData otherPlayerTown) {
        return getRelationWith(otherPlayerTown.getID());
    }

    @Override
    public TownRelation getRelationWith(String otherTownID) {
        String townID = getID();

        if(townID.equals(otherTownID))
            return TownRelation.CITY;

        return this.relations.getRelationWith(otherTownID);
    }

    //////////////////////////////////////
    //              IMoney              //
    //////////////////////////////////////

    @Override
    public int getBalance(){
        if (this.balance == null)
            this.balance = 0;
        return this.balance;
    }



    @Override
    public void addToBalance(int balance){
        this.balance += balance;
    }
    @Override
    public void removeFromBalance(int balance){
        this.balance -= balance;
    }

    //////////////////////////////////////
    //            IBroadcast            //
    //////////////////////////////////////

    @Override
    public void broadCastMessage(String message){
        for (String playerId : townPlayerListId){
            Player player = Bukkit.getServer().getPlayer(UUID.fromString(playerId));
            if (player != null && player.isOnline()) {
                player.sendMessage(message);
            }
        }
    }
    @Override
    public void broadCastMessageWithSound(String message, SoundEnum soundEnum, boolean addPrefix){
        for (String playerId : townPlayerListId){
            Player player = Bukkit.getServer().getPlayer(UUID.fromString(playerId));
            if (player != null && player.isOnline()) {
                SoundUtil.playSound(player, soundEnum);
                if(addPrefix)
                    player.sendMessage(getTANString() + message);
                else
                    player.sendMessage(message);
            }
        }
    }

    @Override
    public void broadCastMessageWithSound(String message, SoundEnum soundEnum){
        broadCastMessageWithSound(message, soundEnum, true);
    }




    public TownRank getRank(int rankID) {
        return this.newRanks.get(rankID);
    }

    public TownRank getRank(PlayerData playerData){
        return getRank(playerData.getTownRankId());
    }

    public TownRank getRank(Player player){
        return getRank(PlayerDataStorage.get(player));
    }

    public List<TownRank> getRanks(){
        if(newRanks == null)
            return null;
        return this.newRanks.values().stream().toList();
    }

    public void setTownDefaultRank(int rankID){
        this.townDefaultRankID = rankID;
    }

    public TownRank getTownDefaultRank(){
        return getRank(getTownDefaultRankID());
    }

    public int getNumberOfRank(){
        return newRanks.size();
    }

    public boolean isFull(){
        return this.townPlayerListId.size() >= this.townLevel.getPlayerCap();
    }
    public boolean canClaimMoreChunk(){
        return this.getNumberOfClaimedChunk() < this.townLevel.getChunkCap();
    }



    public void cancelAllRelation() {
        getRelations().cleanAll(getID());
    }

    public void addPlayerJoinRequest(Player player) {
        addPlayerJoinRequest(player.getUniqueId().toString());
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

    public HashSet<String> getPlayerJoinRequestSet(){
        return this.PlayerJoinRequestSet;
    }

    public boolean isRecruiting() {
        return isRecruiting;
    }

    public void swapRecruiting() {
        this.isRecruiting = !this.isRecruiting;
    }

    public int getFlatTax() {
        if(this.flatTax == null)
            this.flatTax = 1;
        return this.flatTax;
    }

    public void addToFlatTax(int flatTax) {
        this.flatTax += flatTax;
    }

    public int getNumberOfClaimedChunk() {
        int count = 0;
        for (ClaimedChunk2 claimedChunk : NewClaimedChunkStorage.getClaimedChunksMap().values()) {
            if (claimedChunk.getOwnerID().equals(this.TownId)) {
                count++;
            }
        }
        return count;
    }

    public TownChunkPermission getPermission(ChunkPermissionType type) {
        return this.chunkSettings.getPermission(type);
    }

    public void nextPermission(ChunkPermissionType type) {
        this.chunkSettings.nextPermission(type);
    }

    //////////////////////////////////////
    //           IChunkColor            //
    //////////////////////////////////////

    @Override
    public int getChunkColor() {
        if(this.chunkColor == null)
            this.chunkColor = 0xff0000;
        return chunkColor;
    }

    @Override
    public String getChunkColorInHex() {
        return String.format("#%06X", getChunkColor());
    }

    @Override
    public void setChunkColor(int color) {
        this.chunkColor = color;
    }


    public void setSpawn(Location location){
        this.teleportationPosition = new TeleportationPosition(location);
    }

    public boolean isSpawnSet(){
        return this.teleportationPosition != null;
    }

    public TeleportationPosition getSpawn(){
        return this.teleportationPosition;
    }

    public boolean teleportPlayerToSpawn(PlayerData playerData){
        return teleportPlayerToSpawn(playerData.getPlayer());
    }

    public boolean teleportPlayerToSpawn(Player player){
        if(isSpawnLocked()){
            return false;
        }
        if(this.teleportationPosition == null)
            return false;
        this.teleportationPosition.teleport(player);
        return true;
    }

    public boolean isSpawnLocked(){
        return this.townLevel.getBenefitsLevel("UNLOCK_TOWN_SPAWN") <= 0;
    }

    public boolean haveOverlord(){
        return this.regionID != null;
    }

    @Override
    public void claimChunk(Player player) {

        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        Chunk chunkToClaim = player.getLocation().getChunk();

        if(!playerStat.hasPermission(TownRolePermission.CLAIM_CHUNK)){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            return;
        }

        if(!canClaimMoreChunk()){
            player.sendMessage(getTANString() + Lang.MAX_CHUNK_LIMIT_REACHED.get());
            return;
        }


        int cost = ConfigUtil.getCustomConfig("config.yml").getInt("CostOfTownChunk",0);
        if(getBalance() < cost){
            player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY_EXTENDED.get(cost - getBalance()));
            return;
        }
        boolean needToUnclaimBeforeClaiming = false;

        //Chunk already claimed by the town
        ClaimedChunk2 currentClaimedChunk = NewClaimedChunkStorage.get(chunkToClaim);
        if(currentClaimedChunk != null){
            //If chunk belongs to the region in which the town is, then the town can claim the chunk
            if(NewClaimedChunkStorage.isChunkClaimedByTownRegion(this,chunkToClaim)){
                needToUnclaimBeforeClaiming = true;
            }
            //If the chunk is claimed by another territory that the player town have claims on, then the town can claim the chunk
            else if(getAvailableEnemyClaims().containsKey(currentClaimedChunk.getOwnerID())){
                needToUnclaimBeforeClaiming = true;
                consumeEnemyClaim(currentClaimedChunk.getOwnerID());
            }

            else{
                player.sendMessage(getTANString() + Lang.CHUNK_ALREADY_CLAIMED_WARNING.get(NewClaimedChunkStorage.getChunkOwnerName(chunkToClaim)));
                return;
            }
        }

        if(getNumberOfClaimedChunk() != 0 &&
                !NewClaimedChunkStorage.isAdjacentChunkClaimedBySameTown(chunkToClaim,getID()) &&
                !ConfigUtil.getCustomConfig("config.yml").getBoolean("TownAllowNonAdjacentChunks",false)) {
            player.sendMessage(getTANString() + Lang.CHUNK_NOT_ADJACENT.get());
            return;
        }

        if(needToUnclaimBeforeClaiming)
            NewClaimedChunkStorage.unclaimChunk(chunkToClaim);
        NewClaimedChunkStorage.claimTownChunk(chunkToClaim,getID());

        player.sendMessage(getTANString() + Lang.CHUNK_CLAIMED_SUCCESS.get(
                getNumberOfClaimedChunk(),
                getTownLevel().getChunkCap())
        );
    }

    public RegionData getOverlord(){
        return RegionDataStorage.get(this.regionID);
    }

    public String getRegionID(){
        return this.regionID;
    }

    public void setOverlord(ITerritoryData region){
        setOverlord(region.getID());
    }
    public void setOverlord(String regionID){
        this.regionID = regionID;
    }

    public ChunkHistory getChunkHistory() {
        if(chunkHistory == null)
            chunkHistory = new ChunkHistory();
        return chunkHistory;
    }

    public DonationHistory getDonationHistory() {
        if(donationHistory == null)
            donationHistory = new DonationHistory();
        return donationHistory;
    }

    public MiscellaneousHistory getMiscellaneousHistory() {
        if(miscellaneousHistory == null)
            miscellaneousHistory = new MiscellaneousHistory();
        return miscellaneousHistory;
    }

    public SalaryHistory getSalaryHistory() {
        if(salaryHistory == null)
            salaryHistory = new SalaryHistory();
        return salaryHistory;
    }

    public TaxHistory getTaxHistory() {
        if(taxHistory == null)
            taxHistory = new TaxHistory();
        return taxHistory;
    }

    public boolean isLeaderOnline() {
        Player player = Bukkit.getServer().getPlayer(UUID.fromString(this.UuidLeader));
        return player != null && player.isOnline();
    }

    public void removeOverlord() {
        this.regionID = null;
    }
    @Override
    public void addSubject(ITerritoryData territoryToAdd) {
        //town have no subjects
    }

    @Override
    public void removeSubject(ITerritoryData townToDelete) {
        //Town have no subjects
    }

    @Override
    public void removeSubject(String townID) {
        //Region have no subjects
    }

    @Override
    public List<String> getSubjectsID() {
        return null;
    }

    @Override
    public List<ITerritoryData> getSubjects() {
        return null;
    }

    @Override
    public boolean isCapital() {
        if(!haveOverlord())
            return false;
        return getOverlord().getCapital().getID().equals(getID());
    }

    @Override
    public ITerritoryData getCapital() {
        return null;
    }

    public boolean isRegionalCapital() {
        if(!haveOverlord())
            return false;
        return getOverlord().getCapitalID().equals(getID());
    }

    public boolean haveRelationWith(TownData otherTown){
        return this.getRelationWith(otherTown) != null;
    }

    public void setPlayerRank(PlayerData playerStat, int rankID) {
        getRank(playerStat).removePlayer(playerStat);
        getRank(rankID).addPlayer(playerStat);
        playerStat.setTownRankID(rankID);
    }

    public Integer getTownDefaultRankID() {
        if(this.townDefaultRankID == null)
            this.townDefaultRankID = getRanks().get(0).getID(); //Bad fix of a bug, removed in 0.9.0
        return townDefaultRankID;
    }

    public void setTownDefaultRankID(int rankID){
        this.townDefaultRankID = rankID;
    }

    public Map<String, PropertyData> getPropertyDataMap(){
        if(this.propertyDataMap == null)
            this.propertyDataMap = new HashMap<>();
        return this.propertyDataMap;
    }
    public Collection<PropertyData> getPropertyDataList(){
        return getPropertyDataMap().values();
    }

    public String nextPropertyID(){
        if(getPropertyDataMap().isEmpty())
            return "P0";
        int size = getPropertyDataMap().size();
        int lastID = Integer.parseInt(getPropertyDataMap().values().stream().toList().get(size - 1).getTotalID().split("P")[1]);
        return "P" + (lastID + 1);
    }

    public PropertyData registerNewProperty(Vector3D p1, Vector3D p2,PlayerData owner){
        String propertyID = nextPropertyID();
        String ID = this.getID() + "_" + propertyID;
        PropertyData newProperty = new PropertyData(ID,p1,p2,owner);
        this.propertyDataMap.put(propertyID, newProperty);
        owner.addProperty(newProperty);
        return newProperty;
    }

    public PropertyData getProperty(String ID){
        return getPropertyDataMap().get(ID);
    }

    public PropertyData getProperty(Location location) {
        for(PropertyData propertyData : getPropertyDataList()){
            if(propertyData.containsLocation(location)){
                return propertyData;
            }
        }
        return null;
    }

    public void removeProperty(PropertyData propertyData) {
        this.propertyDataMap.remove(propertyData.getPropertyID());
    }

    public String getTownTag() {
        if(this.townTag == null)
            setTownTag(this.TownName.substring(0,3).toUpperCase());
        return this.townTag;
    }
    public void setTownTag(String townTag) {
        this.townTag = townTag;
    }

    public String getColoredTag() {
        return getChunkColor() + "[" + getTownTag() + "]";
    }

    @SuppressWarnings("unused") //API
    public Collection<TownClaimedChunk> getClaims(){
        Collection<TownClaimedChunk> res = new ArrayList<>();
        for(ClaimedChunk2 claimedChunk : NewClaimedChunkStorage.getClaimedChunksMap().values()){
            if(claimedChunk instanceof TownClaimedChunk townClaimedChunk){
                if(townClaimedChunk.getOwnerID().equals(getID())){
                    res.add(townClaimedChunk);
                }
            }
        }
        return res;
    }

    public int computeNextRevenue() {

        int nextTaxes = 0;
        for (String playerID : getPlayerIDList()){
            PlayerData otherPlayerData = PlayerDataStorage.get(playerID);
            OfflinePlayer otherPlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));
            if(!otherPlayerData.getTownRank().isPayingTaxes()){
                continue;
            }
            if(EconomyUtil.getBalance(otherPlayer) < getFlatTax()){
                continue;
            }
            nextTaxes = nextTaxes + getFlatTax();
        }
        return nextTaxes;
    }

    public int getTotalSalaryCost() {
        int totalSalary = 0;
        for (TownRank rank : getRanks()) {

            List<String> playerIdList = rank.getPlayers(getID());
            totalSalary += playerIdList.size() * rank.getSalary();
        }
        return totalSalary;
    }

    public void addDonation(Player player, int amountDonated){
        int playerBalance = EconomyUtil.getBalance(player);

        if(playerBalance < amountDonated ){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NOT_ENOUGH_MONEY.get());
            PlayerChatListenerStorage.removePlayer(player);
            return;
        }
        if(amountDonated <= 0 ){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NEED_1_OR_ABOVE.get());
            PlayerChatListenerStorage.removePlayer(player);
            return;
        }

        TownData playerTown = TownDataStorage.get(player);

        EconomyUtil.removeFromBalance(player,amountDonated);
        playerTown.addToBalance(amountDonated);

        if(!isSQLEnabled())
            playerTown.getDonationHistory().add(player.getName(),player.getUniqueId().toString(),amountDonated);

        player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_SEND_MONEY_TO_TOWN.get(amountDonated));
        PlayerChatListenerStorage.removePlayer(player);
        SoundUtil.playSound(player, MINOR_LEVEL_UP);
    }

    public void kickPlayer(OfflinePlayer kickedPlayer) {
        PlayerData kickedPlayerData = PlayerDataStorage.get(kickedPlayer);

        removePlayer(kickedPlayerData);
        broadCastMessageWithSound(Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS.get(kickedPlayer.getName()), BAD);

        if(kickedPlayer.isOnline())
            kickedPlayer.getPlayer().sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS_PLAYER.get());

    }



    public void upgradeTown(Player player) {
        PlayerData playerData = PlayerDataStorage.get(player);
        TownLevel townLevel = this.getTownLevel();
        if(!playerData.hasPermission(TownRolePermission.UPGRADE_TOWN)){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }
        if(this.getBalance() < townLevel.getMoneyRequiredTownLevel()) {
            player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get());
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }

        this.removeFromBalance(townLevel.getMoneyRequiredTownLevel());
        townLevel.TownLevelUp();
        SoundUtil.playSound(player,LEVEL_UP);
        player.sendMessage(getTANString() + Lang.BASIC_LEVEL_UP.get());
    }
    public void upgradeTown(Player player, TownUpgrade townUpgrade, int townUpgradeLevel){
        PlayerData playerData = PlayerDataStorage.get(player);

        TownLevel townLevel = this.getTownLevel();
        if(!playerData.hasPermission(TownRolePermission.UPGRADE_TOWN)){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }
        int cost = townUpgrade.getCost(townLevel.getUpgradeLevel(townUpgrade.getName()));
        if(this.getBalance() < cost ) {
            player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY_EXTENDED.get(cost - this.getBalance()));
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }
        if(townLevel.getUpgradeLevel(townUpgrade.getName()) >= townUpgrade.getMaxLevel()){
            player.sendMessage(getTANString() + Lang.TOWN_UPGRADE_MAX_LEVEL.get());
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }

        this.removeFromBalance(townUpgrade.getCost(townUpgradeLevel));
        townLevel.levelUp(townUpgrade);
        SoundUtil.playSound(player,LEVEL_UP);
        player.sendMessage(getTANString() + Lang.BASIC_LEVEL_UP.get());
    }

    public boolean haveNoLeader() {
        return this.UuidLeader == null;
    }

    public Collection<String> getOwnedLandmarks() {
        if(ownedLandmarks == null)
            ownedLandmarks = new ArrayList<>();
        return ownedLandmarks;
    }

    public int getNumberOfOwnedLandmarks() {
        return getOwnedLandmarks().size();
    }

    public void addLandmark(String landmarkID){
        getOwnedLandmarks().add(landmarkID);
    }
    public void addLandmark(Landmark landmark){
        addLandmark(landmark.getID());
        landmark.setOwnerID(this);
    }
    public void removeLandmark(String landmarkID){
        getOwnedLandmarks().remove(landmarkID);
    }
    public void removeLandmark(Landmark landmark){
        removeLandmark(landmark.getID());
        landmark.clearOwner();
    }

    public boolean ownLandmark(Landmark landmark) {
        return getOwnedLandmarks().contains(landmark.getID());
    }

    public boolean canClaimMoreLandmarks() {
        return getTownLevel().getTotalBenefits().get("MAX_LANDMARKS") > getNumberOfOwnedLandmarks();
    }

    public int getRegionTaxRate() {
        if(!haveOverlord())
            return 0;
        return getOverlord().getTaxRate();
    }


    @Override
    public boolean atWarWith(String territoryID) {
        for(PlannedAttack plannedAttack : getAttacksInvolved()) {
            if(plannedAttack.getMainDefender().getID().equals(territoryID))
                return true;
        }
        return false;
    }


    public void removeAllLandmark() {
        for(String landmarkID : getOwnedLandmarks()){
            Landmark landmark = LandmarkStorage.get(landmarkID);
            landmark.clearOwner();
        }
    }

    @Override
    public void delete(){
        super.delete();

        removeAllLandmark(); //Remove all Landmark from the deleted town
        for(String playerID : getPlayerIDList()){ //Kick all Players from the deleted town
            removePlayer(PlayerDataStorage.get(playerID));
        }
        updateAllScoreboardColor();
        TownDataStorage.deleteTown(this);
    }
}

