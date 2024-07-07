package org.tan.TownsAndNations.DataClass.territoryData;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;
import org.tan.TownsAndNations.DataClass.ClaimedChunkSettings;
import org.tan.TownsAndNations.DataClass.History.ChunkHistory;
import org.tan.TownsAndNations.DataClass.History.DonationHistory;
import org.tan.TownsAndNations.DataClass.History.MiscellaneousHistory;
import org.tan.TownsAndNations.DataClass.History.TaxHistory;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.DataClass.newChunkData.RegionClaimedChunk;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.SoundEnum;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.SoundUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class RegionData implements ITerritoryData {

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

    @Override
    public void rename(Player player, int regionCost, String newName) {
        removeBalance(regionCost);
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
    public Collection<String> getPlayerList(){
        ArrayList<String> playerList = new ArrayList<>();
        for (TownData townData : getTownsInRegion()){
            playerList.addAll(townData.getPlayerList());
        }
        return playerList;
    }

    @Override
    public ClaimedChunkSettings getChunkSettings(){
        return null;
    }

    @Override
    public boolean havePlayer(String playerID) {
        return getPlayerList().contains(playerID);
    }
    @Override
    public boolean havePlayer(PlayerData playerData) {
        return havePlayer(playerData.getID());
    }

    public int getTotalPlayerCount() {
        int count = 0;
        for (TownData town : getTownsInRegion()){
            count += town.getPlayerList().size();
        }
        return count;
    }


    public String getCapitalID() {
        return capitalID;
    }

    public TownData getCapital() {
        return TownDataStorage.get(capitalID);
    }

    public boolean hasNation() {
        return nationID != null;
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

    public List<String> getTownsID() {
        return townsInRegion;
    }

    public List<TownData> getTownsInRegion() {
        List<TownData> towns = new ArrayList<>();
        for (String townID : townsInRegion) {
            towns.add(TownDataStorage.get(townID));
        }
        return towns;
    }

    public int getNumberOfTownsIn() {
        return townsInRegion.size();
    }

    public void addTown(String townID) {
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


    public Integer getBalance() {
        return balance;
    }
    public Integer addBalance(Integer amount) {
        return balance += amount;
    }
    public Integer removeBalance(Integer amount) {
        return balance -= amount;
    }

    public int getIncomeTomorrow() {
        int income = 0;
        for (TownData town  : getTownsInRegion()) {
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
    public void setChunkColor(int newColor) {
        this.chunkColor = newColor;
    }

    public void removeTown(TownData townToDelete) {
        removeTown(townToDelete.getID());
    }
    public void removeTown(String townID) {
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
        for (TownData town : getTownsInRegion()) {
            town.broadCastMessageWithSound(message, soundEnum);
        }
    }

    public void broadcastMessage(String message) {
        for (TownData town : getTownsInRegion()) {
            town.broadCastMessage(message);
        }
    }



    public boolean isPlayerInRegion(PlayerData playerData) {
        for (TownData town : getTownsInRegion()){
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


}
