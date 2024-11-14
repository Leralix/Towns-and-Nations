package org.leralix.tan.dataclass.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.*;
import org.leralix.tan.dataclass.history.*;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TownClaimedChunk;
import org.leralix.tan.dataclass.territory.economy.Budget;
import org.leralix.tan.dataclass.territory.economy.PlayerTaxLine;
import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.enums.*;
import org.leralix.tan.storage.ClaimBlacklistStorage;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.listeners.chatlistener.PlayerChatListenerStorage;
import org.leralix.tan.utils.*;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import java.util.*;

import static org.leralix.tan.enums.SoundEnum.*;
import static org.leralix.tan.enums.RolePermission.KICK_PLAYER;
import static org.leralix.tan.utils.ChatUtils.getTANString;
import static org.leralix.tan.utils.HeadUtils.getPlayerHead;

public class TownData extends ITerritoryData {

    private final String TownId;
    private String TownName;
    private String UuidLeader;
    private Integer townDefaultRankID;
    private String Description;
    private Long dateTimeCreated;
    private String townIconMaterialCode;
    private TerritoryIcon territoryIcon;
    private String regionID;
    private boolean isRecruiting;
    private Double balance;
    private Integer flatTax;
    private Integer chunkColor;
    private String townTag;
    private TownLevel townLevel = new TownLevel();
    private final HashSet<String> townPlayerListId = new HashSet<>();
    private TownRelations relations = new TownRelations();
    private Map<Integer, RankData> newRanks = new HashMap<>();
    private Collection<String> ownedLandmarks = new ArrayList<>();
    private HashSet<String> PlayerJoinRequestSet = new HashSet<>();
    private Map<String, PropertyData> propertyDataMap;
    private ClaimedChunkSettings chunkSettings = new ClaimedChunkSettings();
    private TeleportationPosition teleportationPosition;

    private ChunkHistory chunkHistory;
    private DonationHistory donationHistory;
    private MiscellaneousHistory miscellaneousHistory;
    private SalaryHistory salaryHistory;
    private TaxHistory taxHistory;

    //First time creating a town
    public TownData(String townId, String townName, String leaderID){
        this.TownId = townId;
        this.UuidLeader = leaderID;
        this.TownName = townName;
        this.Description = "default description";
        this.dateTimeCreated = new Date().getTime();
        this.townIconMaterialCode = null;
        this.isRecruiting = false;
        this.balance = 0.0;
        this.flatTax = 1;
        this.townDefaultRankID = 0;
        int prefixSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("prefixSize",3);
        this.townTag = townName.length() >= prefixSize ? townName.substring(0, prefixSize).toUpperCase() : townName.toUpperCase();
        super.color = StringUtil.randomColor();

        this.chunkHistory = new ChunkHistory();
        this.donationHistory = new DonationHistory();
        this.miscellaneousHistory = new MiscellaneousHistory();
        this.salaryHistory = new SalaryHistory();
        this.taxHistory = new TaxHistory();

        registerNewRank("default");
        if(leaderID != null)
            addPlayer(leaderID);
    }

    @Override //because old code was not using the centralised attribute
    protected Map<Integer, RankData> getRanks(){
        if(newRanks == null)
            newRanks = new HashMap<>();

        return newRanks;
    }

    @Override
    public RankData getRank(PlayerData playerData) {
        return getRank(playerData.getTownRankID());
    }

    public String getLeaderName() {
        if(this.UuidLeader == null)
            return Lang.NO_LEADER.get();
        return Bukkit.getOfflinePlayer(UUID.fromString(this.UuidLeader)).getName();
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
        getTownDefaultRank().addPlayer(playerData);
        playerData.joinTown(this);

        Player playerIterateOnline = playerData.getPlayer();
        if(playerIterateOnline != null)
            playerIterateOnline.sendMessage(getTANString() + Lang.TOWN_INVITATION_ACCEPTED_MEMBER_SIDE.get(getColoredName()));
        broadCastMessageWithSound(Lang.TOWN_INVITATION_ACCEPTED_TOWN_SIDE.get(playerData.getName()), MINOR_GOOD);

        for (TownData allTown : TownDataStorage.getTownMap().values()){
            allTown.removePlayerJoinRequest(playerData.getID());
        }

        TeamUtils.updateAllScoreboardColor();
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
    public int getHierarchyRank() {
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
        return getLeaderID().equals(leaderID);
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
        if(this.territoryIcon == null){
            if(this.townIconMaterialCode == null){
                return getPlayerHead(getName(), Bukkit.getOfflinePlayer(UUID.fromString(getLeaderID())));
            }
            else { // townIconMaterialCode is a legacy code, it should be updated anymore but we keep it for compatibility (todo delete before v0.1)
                territoryIcon = new TerritoryIcon(new ItemStack(Material.getMaterial(townIconMaterialCode)));
            }
        }
        return territoryIcon.getIcon();
    }

    @Override
    public void setIcon(ItemStack icon) {
        this.territoryIcon = new TerritoryIcon(icon);
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
    public ClaimedChunkSettings getChunkSettings() {
        if(chunkSettings == null)
            chunkSettings = new ClaimedChunkSettings();
        return chunkSettings;
    }

    //////////////////////////////////////
    //              IMoney              //
    //////////////////////////////////////

    @Override
    public double getBalance(){
        double digitVal = Math.pow(10,ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("DecimalDigits",2));
        return (long)(balance * digitVal) / digitVal;
    }



    @Override
    public void addToBalance(double balance){
        this.balance += balance;
    }
    @Override
    public void removeFromBalance(double balance){
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

    public RankData getRank(Player player){
        return getRank(PlayerDataStorage.get(player));
    }

    public RankData getTownDefaultRank(){
        return getRank(getDefaultRankID());
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

    @Override
    public double getChunkUpkeepCost() {
        return ConfigUtil.getCustomConfig(ConfigTag.MAIN).getDouble("TownChunkUpkeepCost",0) / 10;
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
    public int getChildColorCode() {
        if(this.chunkColor == null)
            this.chunkColor = 0xff0000;
        return chunkColor;
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
    public void claimChunk(Player player, Chunk chunk) {

        PlayerData playerData = PlayerDataStorage.get(player.getUniqueId().toString());


        if(ClaimBlacklistStorage.cannotBeClaimed(chunk)){
            player.sendMessage(ChatUtils.getTANString() + Lang.CHUNK_IS_BLACKLISTED.get());
            return;
        }

        if(!doesPlayerHavePermission(playerData, RolePermission.CLAIM_CHUNK)){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            return;
        }

        if(!canClaimMoreChunk()){
            player.sendMessage(getTANString() + Lang.MAX_CHUNK_LIMIT_REACHED.get());
            return;
        }


        int cost = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("CostOfTownChunk",0);
        if(getBalance() < cost){
            player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY_EXTENDED.get(cost - getBalance()));
            return;
        }

        ClaimedChunk2 chunkData = NewClaimedChunkStorage.get(chunk);
        if(!chunkData.canPlayerClaim(player,this)){
            return;
        }

        if(getNumberOfClaimedChunk() != 0 &&
                !NewClaimedChunkStorage.isAdjacentChunkClaimedBySameTown(chunk,getID()) &&
                !ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("TownAllowNonAdjacentChunks",false)) {
            player.sendMessage(getTANString() + Lang.CHUNK_NOT_ADJACENT.get());
            return;
        }

        NewClaimedChunkStorage.unclaimChunk(chunk); //Un-claim in case it was already claimed and territory has the right to claim it
        NewClaimedChunkStorage.claimTownChunk(chunk,getID());

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

    public void setOverlordPrivate(ITerritoryData region){
        setOverlordPrivate(region.getID());
    }
    public void setOverlordPrivate(String regionID){
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

    @Override
    public Collection<ITerritoryData> getPotentialVassals() {
        return Collections.emptyList();
    }

    public void removeOverlord() {
        this.regionID = null;
        for(PlayerData playerData : getPlayerDataList()){
            playerData.setRegionRankID(null);
        }
    }
    @Override
    public void addVassalPrivate(ITerritoryData vassal) {
        //town have no vassals
    }

    @Override
    public void removeVassal(String townID) {
        //Town have no vassals
    }


    @Override
    public boolean isCapital() {
        if(!haveOverlord())
            return false;
        return getOverlord().getCapital().getID().equals(getID());
    }

    @Override
    public String getCapitalID() {
        return regionID;
    }


    public boolean isRegionalCapital() {
        if(!haveOverlord())
            return false;
        return getOverlord().getCapitalID().equals(getID());
    }

    public boolean haveRelationWith(TownData otherTown){
        return this.getRelationWith(otherTown) != null;
    }

    @Override
    public void setDefaultRank(int rankID){
        this.townDefaultRankID = rankID;
    }
    @Override
    public int getDefaultRankID() {
        return this.townDefaultRankID;
    }

    @Override
    public List<GuiItem> getMemberList(PlayerData playerData) {
        Player player = playerData.getPlayer();
        List<GuiItem> res = new ArrayList<>();
        for (String playerUUID: getPlayerIDList()) {
            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            PlayerData playerIterateData = PlayerDataStorage.get(playerUUID);
            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate,
                    Lang.GUI_TOWN_MEMBER_DESC1.get(playerIterateData.getTownRank().getColoredName()),
                    Lang.GUI_TOWN_MEMBER_DESC2.get(EconomyUtil.getBalance(playerIterate)),
                    doesPlayerHavePermission(playerData,KICK_PLAYER) ? Lang.GUI_TOWN_MEMBER_DESC3.get() : "");

            GuiItem playerButton = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                if(event.getClick() == ClickType.RIGHT){

                    PlayerData kickedPlayerData = PlayerDataStorage.get(playerIterate);
                    TownData townData = TownDataStorage.get(playerData);


                    if(!doesPlayerHavePermission(playerData,KICK_PLAYER)){
                        player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                        return;
                    }
                    if(townData.getRank(kickedPlayerData).isSuperiorTo(townData.getRank(playerData))){
                        player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get());
                        return;
                    }
                    if(isLeader(kickedPlayerData)){
                        player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_LEADER.get());
                        return;
                    }
                    if(playerData.getID().equals(kickedPlayerData.getID())){
                        player.sendMessage(ChatUtils.getTANString() + Lang.GUI_TOWN_MEMBER_CANT_KICK_YOURSELF.get());
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
    protected void specificSetPlayerRank(PlayerData playerStat, int rankID) {
        playerStat.setTownRankID(rankID);
    }

    @Override
    protected void addSpecificTaxes(Budget budget) {
        budget.addProfitLine(new PlayerTaxLine(this));
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
        return getChunkColorCode() + "[" + getTownTag() + "]";
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
        if(!doesPlayerHavePermission(playerData, RolePermission.UPGRADE_TOWN)){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }
        if(this.getBalance() < townLevel.getMoneyRequiredForLevelUp()) {
            player.sendMessage(getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get());
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }

        removeFromBalance(townLevel.getMoneyRequiredForLevelUp());
        townLevel.townLevelUp();
        SoundUtil.playSound(player,LEVEL_UP);
        player.sendMessage(getTANString() + Lang.BASIC_LEVEL_UP.get());
    }
    public void upgradeTown(Player player, TownUpgrade townUpgrade, int townUpgradeLevel){
        PlayerData playerData = PlayerDataStorage.get(player);

        if(!doesPlayerHavePermission(playerData, RolePermission.UPGRADE_TOWN)){
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
        TownLevel townLevel = this.getTownLevel();
        if(townLevel.getUpgradeLevel(townUpgrade.getName()) >= townUpgrade.getMaxLevel()){
            player.sendMessage(getTANString() + Lang.TOWN_UPGRADE_MAX_LEVEL.get());
            SoundUtil.playSound(player,NOT_ALLOWED);
            return;
        }

        removeFromBalance(townUpgrade.getCost(townUpgradeLevel));
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
        broadCastMessageWithSound(Lang.BROADCAST_PLAYER_TOWN_DELETED.get(getLeaderData().getName(), getColoredName()), BAD);
        removeAllLandmark(); //Remove all Landmark from the deleted town
        removeAllProperty(); //Remove all Property from the deleted town
        for(String playerID : getPlayerIDList()){ //Kick all Players from the deleted town
            removePlayer(PlayerDataStorage.get(playerID));
        }
       TeamUtils.updateAllScoreboardColor();
        TownDataStorage.deleteTown(this);
    }

    private void removeAllProperty() {
        for(PropertyData propertyData : getPropertyDataList()){
            propertyData.delete();
        }
    }

    @Override
    public boolean canConquerChunk(ClaimedChunk2 chunk) {
        return false;
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
        if(!haveOverlord())
            return false;
        return getOverlord().getCapitalID().equals(getID());
    }
}

