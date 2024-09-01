package org.tan.TownsAndNations.DataClass.territoryData;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tan.TownsAndNations.DataClass.wars.AttackInvolved;
import org.tan.TownsAndNations.DataClass.ClaimedChunkSettings;
import org.tan.TownsAndNations.DataClass.History.ChunkHistory;
import org.tan.TownsAndNations.DataClass.History.DonationHistory;
import org.tan.TownsAndNations.DataClass.History.MiscellaneousHistory;
import org.tan.TownsAndNations.DataClass.History.TaxHistory;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownRelations;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.DataClass.newChunkData.RegionClaimedChunk;
import org.tan.TownsAndNations.DataClass.wars.CurrentAttacks;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.SoundEnum;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.CurrentAttacksStorage;
import org.tan.TownsAndNations.storage.DataStorage.AttackInvolvedStorage;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.SoundUtil;
import org.tan.TownsAndNations.utils.TerritoryUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class RegionData extends ITerritoryData {

    private final String id;
    private String name;
    private String leaderID;
    private String capitalID;
    private String nationID;
    private Long dateTimeCreated;
    private String regionIconType;
    private Integer taxRate;
    private Integer balance;
    private Integer chunkColor;
    private String description;
    private final List<String> townsInRegion;
    private ChunkHistory chunkHistory;
    private DonationHistory donationHistory;
    private MiscellaneousHistory miscellaneousHistory;
    private TaxHistory taxHistory;
    private TownRelations relations;
    private Collection<String> attackIncomingList = new ArrayList<>();
    private Collection<String> currentAttackList = new ArrayList<>();


    public RegionData(String id, String name, String ownerID) {
        PlayerData owner = PlayerDataStorage.get(ownerID);
        TownData ownerTown = TownDataStorage.get(owner);

        this.id = id;
        this.name = name;
        this.capitalID = ownerTown.getID();
        this.dateTimeCreated = new Date().getTime();
        this.nationID = null;
        this.regionIconType = null;
        this.taxRate = 1;
        this.balance = 0;
        this.description = "default description";
        this.townsInRegion = new ArrayList<>();
        this.townsInRegion.add(ownerTown.getID());
        this.chunkColor = setBasicColor(ownerTown.getChunkColor());

        this.chunkHistory = new ChunkHistory();
        this.donationHistory = new DonationHistory();
        this.miscellaneousHistory = new MiscellaneousHistory();
        this.taxHistory = new TaxHistory();
    }

    //////////////////////////////////////
    //          ITerritoryData          //
    //////////////////////////////////////

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getRank() {
        return 1;
    }

    @Override
    public String getColoredName() {
        return "§b" + getName();
    }
    @Override
    public void rename(Player player, int regionCost, String newName) {
        removeFromBalance(regionCost);
        player.sendMessage(ChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get());
        SoundUtil.playSound(player, SoundEnum.GOOD);
        this.name = newName;
    }
    @Override
    public String getLeaderID(){
        if(leaderID == null)
            leaderID = getCapital().getLeaderID();
        return leaderID;
    }

    @Override
    public PlayerData getLeaderData(){
        return PlayerDataStorage.get(getLeaderID());
    }
    @Override
    public void setLeaderID(String newLeaderID){
        this.leaderID = newLeaderID;
    }

    @Override
    public boolean isLeader(String id) {
        return getLeaderID().equals(id);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public ItemStack getIconItem() {
        if(this.regionIconType == null)
            return getCapital().getIconItem();
        return new ItemStack(Material.valueOf(this.regionIconType));
    }

    @Override
    public void setIconMaterial(Material regionIconType) {
        setIconMaterial(regionIconType.name());
    }


    public void setIconMaterial(String regionIconType) {
        this.regionIconType = regionIconType;
    }

    @Override
    public Collection<String> getPlayerIDList(){
        ArrayList<String> playerList = new ArrayList<>();
        for (ITerritoryData townData : getSubjects()){
            playerList.addAll(townData.getPlayerIDList());
        }
        return playerList;
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
    public ClaimedChunkSettings getChunkSettings(){
        return null;
    }

    @Override
    public boolean havePlayer(String playerID) {
        return getPlayerIDList().contains(playerID);
    }
    @Override
    public boolean havePlayer(PlayerData playerData) {
        return havePlayer(playerData.getID());
    }

    @Override
    public ItemStack getIcon() {
        ItemStack icon = getIconItem();

        ItemMeta meta = icon.getItemMeta();
        if(meta != null){
            meta.setDisplayName(ChatColor.AQUA + getName());
            icon.setItemMeta(meta);
        }
        return icon;
    }

    @Override
    public ItemStack getIconWithInformations() {
        ItemStack icon = getIconItem();

        ItemMeta meta = icon.getItemMeta();
        if(meta != null){
            meta.setDisplayName(ChatColor.AQUA + getName());

            List<String> lore = new ArrayList<>();
            lore.add(Lang.GUI_REGION_INFO_DESC0.get(getDescription()));
            lore.add(Lang.GUI_REGION_INFO_DESC1.get(getCapital().getName()));
            lore.add(Lang.GUI_REGION_INFO_DESC2.get(getNumberOfTownsIn()));
            lore.add(Lang.GUI_REGION_INFO_DESC3.get(getTotalPlayerCount()));
            lore.add(Lang.GUI_REGION_INFO_DESC4.get(getBalance()));
            lore.add(Lang.GUI_REGION_INFO_DESC5.get(getNumberOfClaimedChunk()));
            meta.setLore(lore);
            icon.setItemMeta(meta);
        }
        return icon;
    }

    @Override
    public ItemStack getIconWithInformationAndRelation(ITerritoryData territoryData){
        ItemStack icon = getIconWithInformations();

        ItemMeta meta = icon.getItemMeta();
        if(meta != null){
            List<String> lore = meta.getLore();

            if(territoryData != null){
                TownRelation relation = getRelationWith(territoryData);
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

    public int getTotalPlayerCount() {
        int count = 0;
        for (ITerritoryData town : getSubjects()){
            count += town.getPlayerIDList().size();
        }
        return count;
    }


    public String getCapitalID() {
        return capitalID;
    }

    public TownData getCapital() {
        return TownDataStorage.get(capitalID);
    }

    @Override
    public boolean haveOverlord() {
        return nationID != null;
    }

    public boolean hasNation() {
        return nationID != null;
    }

    public ITerritoryData getOverlord() {
        return null;
    }
    public String getNationID() {
        return nationID;
    }

    public void setNationID(String nationID) {
        this.nationID = nationID;
    }

    public Integer getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Integer taxRate) {
        this.taxRate = taxRate;
    }

    public List<String> getSubjectsID() {
        return townsInRegion;
    }

    public List<ITerritoryData> getSubjects() {
        List<ITerritoryData> towns = new ArrayList<>();
        for (String townID : townsInRegion) {
            towns.add(TerritoryUtil.getTerritory(townID));
        }
        return towns;
    }

    @Override
    public boolean isCapital() {
        if(!hasNation())
            return false;
        return getOverlord().isCapital();
    }

    public int getNumberOfTownsIn() {
        return townsInRegion.size();
    }


    @Override
    public void addSubject(ITerritoryData territoryToAdd) {
        addSubject(territoryToAdd.getID());
    }

    public void addSubject(String townID) {
        townsInRegion.add(townID);
    }


    public boolean isCapital( TownData town) {
        return isCapital(town.getID());
    }
    public boolean isCapital( String townID) {
        return capitalID.equals(townID);
    }
    public void setCapital(TownData town) {
        setCapital(town.getID());
    }
    public void setCapital(String townID) {
        this.capitalID = townID;
    }


    public int getBalance() {
        return balance;
    }

    @Override
    public void removeOverlord() {
        // Kingdoms are not implemented yet
    }

    @Override
    public void setOverlord(ITerritoryData overlord) {
        // Kingdoms are not implemented yet
    }



    public void addBalance(Integer amount) {
        balance += amount;
    }
    public void removeFromBalance(Integer amount) {
        balance -= amount;
    }

    public int getIncomeTomorrow() {
        int income = 0;
        for (ITerritoryData town  : getSubjects()) {
            if(town.getBalance() > taxRate) {
                income += taxRate;
            }
        }
        return income;
    }

    public void addToTax(int i) {
        taxRate += i;
    }

    public ChunkHistory getChunkHistory() {
        if(chunkHistory == null)
            this.chunkHistory = new ChunkHistory();
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

    public TaxHistory getTaxHistory() {
        if(taxHistory == null)
            taxHistory = new TaxHistory();
        return taxHistory;
    }

    public boolean isTownInRegion(TownData townData) {
        return townsInRegion.contains(townData.getID());
    }

    public int setBasicColor(int colorHex) {
        int red = (colorHex >> 16) & 0xFF;
        int green = (colorHex >> 8) & 0xFF;
        int blue = colorHex & 0xFF;

        red = Math.max(0, red - 25);
        green = Math.max(0, green - 25);
        blue = Math.max(0, blue - 25);

        return (red << 16) | (green << 8) | blue;
    }

    public int getChunkColor() {
        return this.chunkColor;
    }

    @Override
    public String getChunkColorInHex() {
        return null;
    }

    public void setChunkColor(int newColor) {
        this.chunkColor = newColor;
    }

    public void removeSubject(ITerritoryData townToDelete) {
        removeSubject(townToDelete.getID());
    }
    public void removeSubject(String townID) {
        townsInRegion.remove(townID);
    }

    public int getNumberOfClaimedChunk() {
        int count = 0;
        for (ClaimedChunk2 claimedChunk : NewClaimedChunkStorage.getClaimedChunksMap().values()) {
            if (claimedChunk.getOwnerID().equals(this.id)) {
                count++;
            }
        }
        return count;
    }


    public void broadcastMessageWithSound(String message, SoundEnum soundEnum) {
        for (ITerritoryData town : getSubjects()) {
            town.broadCastMessageWithSound(message, soundEnum);
        }
    }

    public void broadcastMessage(String message) {
        for (ITerritoryData town : getSubjects()) {
            town.broadCastMessage(message);
        }
    }



    public boolean isPlayerInRegion(PlayerData playerData) {
        for (ITerritoryData town : getSubjects()){
            if(town.havePlayer(playerData))
                return true;
        }
        return false;
    }

    public Long getDateTimeCreated() {
        if(dateTimeCreated == null)
            dateTimeCreated = new Date().getTime();
        return dateTimeCreated;
    }

    public Collection<RegionClaimedChunk> getClaims() {
        Collection<RegionClaimedChunk> res = new ArrayList<>();
        for(ClaimedChunk2 claimedChunk : NewClaimedChunkStorage.getClaimedChunksMap().values()){
            if(claimedChunk instanceof RegionClaimedChunk regionClaimedChunk){
                if(regionClaimedChunk.getOwnerID().equals(getID())){
                    res.add(regionClaimedChunk);
                }
            }
        }
        return res;

    }


    @Override
    public TownRelations getRelations() {
        if(relations == null)
            relations = new TownRelations();
        return relations;
    }

    @Override
    public void addRelation(TownRelation relation, ITerritoryData territoryData) {
        addRelation(relation, territoryData.getID());
    }

    @Override
    public void addRelation(TownRelation relation, String territoryID) {
        getRelations().addRelation(relation, territoryID);
    }

    @Override
    public void removeRelation(TownRelation relation, ITerritoryData territoryData) {
        removeRelation(relation, territoryData.getID());
    }

    @Override
    public void removeRelation(TownRelation relation, String territoryID) {
        getRelations().removeRelation(relation, territoryID);
    }

    @Override
    public TownRelation getRelationWith(ITerritoryData relation) {
        return getRelationWith(relation.getID());
    }

    @Override
    public TownRelation getRelationWith(String otherTownID) {

        if(getID().equals(otherTownID))
            return TownRelation.REGION;

        return null;
    }

    @Override
    public void addToBalance(int balance) {
        this.balance += balance;
    }

    @Override
    public void removeFromBalance(int balance) {
        this.balance -= balance;
    }

    @Override
    public void broadCastMessage(String message) {
        for(ITerritoryData townData : getSubjects())
            townData.broadCastMessage(message);
    }

    @Override
    public void broadCastMessageWithSound(String message, SoundEnum soundEnum, boolean addPrefix) {
        for(ITerritoryData townData : getSubjects())
            townData.broadCastMessageWithSound(message, soundEnum, addPrefix);
    }

    @Override
    public void broadCastMessageWithSound(String message, SoundEnum soundEnum) {
        for(ITerritoryData townData : getSubjects())
            townData.broadCastMessageWithSound(message, soundEnum);
    }

    @Override
    public boolean haveNoLeader() {
        return false;
    }

    @Override
    public Collection<String> getAttacksInvolvedID(){
        if(attackIncomingList == null)
            this.attackIncomingList = new ArrayList<>();
        return attackIncomingList;
    }

    @Override
    public Collection<AttackInvolved> getAttacksInvolved() {
        Collection<AttackInvolved> res = new ArrayList<>();
        for(String attackID : getAttacksInvolvedID()){
            AttackInvolved attackInvolved = AttackInvolvedStorage.get(attackID);
            res.add(attackInvolved);
        }
        return res;
    }

    @Override
    public void addPlannedAttack(AttackInvolved war){
        getAttacksInvolvedID().add(war.getID());
    }
    @Override
    public void removePlannedAttack(AttackInvolved war){
        getAttacksInvolvedID().remove(war.getID());
    }

    @Override
    public Collection<String> getCurrentAttacksID() {
        if(currentAttackList == null)
            this.currentAttackList = new ArrayList<>();
        return currentAttackList;
    }

    @Override
    public Collection<CurrentAttacks> getCurrentAttacks() {
        Collection<CurrentAttacks> res = new ArrayList<>();
        for(String attackID : getAttacksInvolvedID()){
            CurrentAttacks attackInvolved = CurrentAttacksStorage.get(attackID);
            res.add(attackInvolved);
        }
        return res;
    }

    @Override
    public void addCurrentAttack(CurrentAttacks currentAttacks) {
        getAttacksInvolvedID().add(currentAttacks.getID());
    }


    @Override
    public void removeCurrentAttack(CurrentAttacks currentAttacks) {
        getAttacksInvolvedID().remove(currentAttacks.getID());
    }

    @Override
    public boolean atWarWith(String territoryID) {
        for(AttackInvolved attackInvolved : getAttacksInvolved()) {
            if(attackInvolved.getMainDefender().getID().equals(territoryID))
                return true;
        }
        return false;
    }

}
