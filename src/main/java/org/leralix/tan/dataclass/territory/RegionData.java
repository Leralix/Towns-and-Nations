package org.leralix.tan.dataclass.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.RegionClaimedChunk;
import org.leralix.tan.dataclass.newhistory.SubjectTaxHistory;
import org.leralix.tan.dataclass.territory.economy.Budget;
import org.leralix.tan.dataclass.territory.economy.SubjectTaxLine;
import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.ClaimBlacklistStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.*;

import java.util.*;

public class RegionData extends TerritoryData {

    private final String regionId;
    private final String regionName;
    private String leaderID;
    private String capitalID;
    private String nationID;
    private final Long regionDateTimeCreated;
    private Double balance;
    private final List<String> townsInRegion;

    public RegionData(String id, String name, String ownerID) {
        super(id, name, ownerID);
        PlayerData owner = PlayerDataStorage.getInstance().get(ownerID);
        TownData ownerTown = owner.getTown();

        this.regionId = id;
        this.regionName = name;
        this.capitalID = ownerTown.getID();
        this.regionDateTimeCreated = new Date().getTime();
        this.nationID = null;

        this.balance = 0.0;
        this.townsInRegion = new ArrayList<>();
    }

    @Override
    public String getOldID() {
        return regionId;
    }

    @Override
    public String getOldName() {
        return regionName;
    }

    public int getHierarchyRank() {
        return 1;
    }

    @Override
    public String getColoredName() {
        return "§b" + getName();
    }

    @Override
    public String getLeaderID() {
        if (leaderID == null)
            leaderID = getCapital().getLeaderID();
        return leaderID;
    }

    @Override
    public PlayerData getLeaderData() {
        return PlayerDataStorage.getInstance().get(getLeaderID());
    }

    @Override
    public void setLeaderID(String newLeaderID) {
        this.leaderID = newLeaderID;
    }

    @Override
    public boolean isLeader(String id) {
        return getLeaderID().equals(id);
    }


    @Override
    public Collection<String> getPlayerIDList() {
        ArrayList<String> playerList = new ArrayList<>();
        for (TerritoryData townData : getSubjects()) {
            playerList.addAll(townData.getPlayerIDList());
        }
        return playerList;
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
        ItemStack icon = getIcon();

        ItemMeta meta = icon.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + getName());
            icon.setItemMeta(meta);
        }
        return icon;

    }

    @Override
    public ItemStack getIconWithInformations(LangType langType) {
        ItemStack icon = getIcon();

        ItemMeta meta = icon.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + getName());

            List<String> lore = new ArrayList<>();
            lore.add(Lang.GUI_REGION_INFO_DESC0.get(langType, getDescription()));
            lore.add(Lang.GUI_REGION_INFO_DESC1.get(langType, getCapital().getName()));
            lore.add(Lang.GUI_REGION_INFO_DESC2.get(langType, getNumberOfTownsIn()));
            lore.add(Lang.GUI_REGION_INFO_DESC3.get(langType, getTotalPlayerCount()));
            lore.add(Lang.GUI_REGION_INFO_DESC5.get(langType, getNumberOfClaimedChunk()));

            meta.setLore(lore);
            icon.setItemMeta(meta);
        }
        return icon;
    }

    public int getTotalPlayerCount() {
        int count = 0;
        for (TerritoryData town : getSubjects()) {
            count += town.getPlayerIDList().size();
        }
        return count;
    }

    @Override
    public String getCapitalID() {
        return capitalID;
    }

    @Override
    public boolean haveOverlord() {
        return nationID != null;
    }

    @Override
    public Optional<ClaimedChunk2> claimChunkInternal(Player player, Chunk chunk) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        TownData townData = TownDataStorage.getInstance().get(player);
        RegionData regionData = townData.getRegion(); //TODO : Does regionData is usefull ? We are inside a region

        if (ClaimBlacklistStorage.cannotBeClaimed(chunk)) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.CHUNK_IS_BLACKLISTED.get());
            return Optional.empty();
        }


        if (!regionData.doesPlayerHavePermission(playerData, RolePermission.CLAIM_CHUNK)) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NOT_LEADER_OF_REGION.get());
            return Optional.empty();
        }
        int cost = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("CostOfRegionChunk", 5);

        if (regionData.getBalance() < cost) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.REGION_NOT_ENOUGH_MONEY_EXTENDED.get(cost - regionData.getBalance()));
            return Optional.empty();
        }

        ClaimedChunk2 currentClaimedChunk = NewClaimedChunkStorage.getInstance().get(chunk);
        if (!currentClaimedChunk.canTerritoryClaim(Optional.of(player), regionData)) {
            return Optional.empty();
        }

        regionData.removeFromBalance(cost);
        NewClaimedChunkStorage.getInstance().claimRegionChunk(chunk, regionData.getID());
        player.sendMessage(TanChatUtils.getTANString() + Lang.CHUNK_CLAIMED_SUCCESS_REGION.get());
        return Optional.of(NewClaimedChunkStorage.getInstance().get(chunk));
    }

    public boolean hasNation() {
        return nationID != null;
    }

    @Override
    public TerritoryData getOverlord() {
        return null;
    }

    @Override
    protected Collection<TerritoryData> getOverlords() {
        List<TerritoryData> overlords = new ArrayList<>();

        if(hasNation()){
            // TODO : Add Nations
        }
        return overlords;
    }

    public List<TerritoryData> getSubjects() {
        List<TerritoryData> towns = new ArrayList<>();
        for (String townID : townsInRegion) {
            towns.add(TerritoryUtil.getTerritory(townID));
        }
        return towns;
    }

    @Override
    public boolean isCapital() {
        if (!hasNation())
            return false;
        return getOverlord().isCapital();
    }

    public int getNumberOfTownsIn() {
        return townsInRegion.size();
    }

    @Override
    protected void addVassalPrivate(TerritoryData vassal) {
        townsInRegion.add(vassal.getID());
    }


    public void setCapital(TownData town) {
        setCapital(town.getID());
    }

    public void setCapital(String townID) {
        this.capitalID = townID;
    }


    public double getBalance() {
        return StringUtil.handleDigits(balance);
    }

    @Override
    public void removeOverlordPrivate() {
        // Kingdoms are not implemented yet
    }

    @Override
    protected void collectTaxes() {
        for (TerritoryData town : getVassals()) {
            if (town == null) continue;
            double tax = getTax();
            if (town.getBalance() < tax) {
                TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new SubjectTaxHistory(this, town, -1));
            } else {
                town.removeFromBalance(tax);
                addToBalance(tax);
                TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new SubjectTaxHistory(this, town, tax));
            }
        }
    }

    public boolean isTownInRegion(TownData townData) {
        return townsInRegion.contains(townData.getID());
    }

    protected void removeVassal(String vassalID) {
        TownData town = TownDataStorage.getInstance().get(vassalID);
        townsInRegion.remove(vassalID);


        for (RankData rank : getRanks().values()) {
            for (String playerID : town.getPlayerIDList()) {
                rank.removePlayer(playerID);
            }
        }
    }

    @Override
    public double getChunkUpkeepCost() {
        return ConfigUtil.getCustomConfig(ConfigTag.MAIN).getDouble("RegionChunkUpkeepCost", 0);
    }

    public boolean isPlayerInRegion(PlayerData playerData) {
        for (TerritoryData town : getSubjects()) {
            if (town.isPlayerIn(playerData))
                return true;
        }
        return false;
    }


    public Collection<RegionClaimedChunk> getClaims() {
        Collection<RegionClaimedChunk> res = new ArrayList<>();
        for (ClaimedChunk2 claimedChunk : NewClaimedChunkStorage.getInstance().getClaimedChunksMap().values()) {
            if (claimedChunk instanceof RegionClaimedChunk regionClaimedChunk && regionClaimedChunk.getOwnerID().equals(getID())) {
                res.add(regionClaimedChunk);
            }
        }
        return res;
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
        for (TerritoryData townData : getSubjects())
            townData.broadCastMessage(message);
    }

    @Override
    public void broadcastMessageWithSound(String message, SoundEnum soundEnum, boolean addPrefix) {
        for (TerritoryData townData : getSubjects())
            townData.broadcastMessageWithSound(message, soundEnum, addPrefix);
    }

    @Override
    public void broadcastMessageWithSound(String message, SoundEnum soundEnum) {
        for (TerritoryData townData : getSubjects())
            townData.broadcastMessageWithSound(message, soundEnum);
    }

    @Override
    public boolean haveNoLeader() {
        return false; //Region always have a leader
    }


    @Override
    public boolean atWarWith(String territoryID) {
        for (PlannedAttack plannedAttack : getAttacksInvolved()) {
            if (plannedAttack.getMainDefender().getID().equals(territoryID))
                return true;
        }
        return false;
    }

    @Override
    public void delete() {
        super.delete();
        broadcastMessageWithSound(Lang.BROADCAST_PLAYER_REGION_DELETED.get(getLeaderData().getNameStored(), getColoredName()), SoundEnum.BAD);
        TeamUtils.updateAllScoreboardColor();
        RegionDataStorage.getInstance().deleteRegion(this);
    }

    @Override
    public void openMainMenu(Player player) {
        PlayerGUI.dispatchPlayerRegion(player);
    }

    @Override
    public boolean canHaveVassals() {
        return true;
    }

    @Override
    public boolean canHaveOverlord() {
        return true;
    }

    @Override
    public List<String> getVassalsID() {
        return townsInRegion;
    }

    @Override
    public boolean isVassal(String territoryID) {
        return townsInRegion.contains(territoryID);
    }

    @Override
    public boolean isCapitalOf(String territoryID) {
        if (!hasNation())
            return false;
        return getOverlord().getCapitalID().equals(territoryID);
    }

    @Override
    public Collection<TerritoryData> getPotentialVassals() {
        return new ArrayList<>(TownDataStorage.getInstance().getTownMap().values());
    }


    @Override
    public RankData getRank(PlayerData playerData) {
        if(!playerData.hasRegion()){
            return null;
        }
        return getRank(playerData.getRegionRankID());
    }

    @Override
    public List<GuiItem> getOrderedMemberList(PlayerData playerData) {
        List<GuiItem> res = new ArrayList<>();
        for (String playerUUID : getOrderedPlayerIDList()) {
            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            PlayerData playerIterateData = PlayerDataStorage.getInstance().get(playerUUID);
            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate,
                    Lang.GUI_TOWN_MEMBER_DESC1.get(playerIterateData.getRegionRank().getColoredName()));

            GuiItem playerButton = ItemBuilder.from(playerHead).asGuiItem(event -> event.setCancelled(true));
            res.add(playerButton);
        }
        return res;
    }

    @Override
    protected void specificSetPlayerRank(PlayerData playerStat, int rankID) {
        playerStat.setRegionRankID(rankID);
    }

    @Override
    protected void addSpecificTaxes(Budget budget) {
        budget.addProfitLine(new SubjectTaxLine(this));
    }

    protected long getOldDateTime() {
        return regionDateTimeCreated;
    }
}
