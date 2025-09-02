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
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.newhistory.SubjectTaxHistory;
import org.leralix.tan.dataclass.territory.economy.Budget;
import org.leralix.tan.dataclass.territory.economy.SubjectTaxLine;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.TerritoryIndependanceInternalEvent;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.ClaimBlacklistStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.graphic.TeamUtils;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class RegionData extends TerritoryData {


    private String leaderID;
    private String capitalID;
    private String nationID;
    private final List<String> townsInRegion;

    public RegionData(String id, String name, ITanPlayer owner) {
        super(id, name, owner);
        TownData ownerTown = owner.getTown();

        this.capitalID = ownerTown.getID();
        this.nationID = null;

        this.townsInRegion = new ArrayList<>();
    }

    public int getHierarchyRank() {
        return 1;
    }

    @Override
    public String getBaseColoredName() {
        return "§b" + getName();
    }

    @Override
    public String getLeaderID() {
        if (leaderID == null)
            leaderID = getCapital().getLeaderID();
        return leaderID;
    }

    @Override
    public ITanPlayer getLeaderData() {
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
    public Collection<ITanPlayer> getITanPlayerList() {
        ArrayList<ITanPlayer> ITanPlayerList = new ArrayList<>();
        for (String playerID : getPlayerIDList()) {
            ITanPlayerList.add(PlayerDataStorage.getInstance().get(playerID));
        }
        return ITanPlayerList;
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
    public boolean haveOverlord() {
        return nationID != null;
    }

    @Override
    public boolean claimChunk(Player player, Chunk chunk, boolean ignoreAdjacent) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

        if (ClaimBlacklistStorage.cannotBeClaimed(chunk)) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.CHUNK_IS_BLACKLISTED.get());
            return false;
        }


        if (!doesPlayerHavePermission(tanPlayer, RolePermission.CLAIM_CHUNK)) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NOT_LEADER_OF_REGION.get());
            return false;
        }
        int cost = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("CostOfRegionChunk", 5);

        if (getBalance() < cost) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.REGION_NOT_ENOUGH_MONEY_EXTENDED.get(cost - getBalance()));
            return false;
        }

        if (!ignoreAdjacent && getNumberOfClaimedChunk() != 0 &&
                !NewClaimedChunkStorage.getInstance().isOneAdjacentChunkClaimedBySameTown(chunk, getID()) &&
                !Constants.allowNonAdjacentChunksForRegion()) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.CHUNK_NOT_ADJACENT.get());
            return false;
        }

        ClaimedChunk2 currentClaimedChunk = NewClaimedChunkStorage.getInstance().get(chunk);
        if (!currentClaimedChunk.canTerritoryClaim(player, this)) {
            return false;
        }

        removeFromBalance(cost);
        NewClaimedChunkStorage.getInstance().claimRegionChunk(chunk, getID());
        player.sendMessage(TanChatUtils.getTANString() + Lang.CHUNK_CLAIMED_SUCCESS_REGION.get());
        NewClaimedChunkStorage.getInstance().get(chunk);
        return true;
    }


    @Override
    protected Collection<TerritoryData> getOverlords() {
        return new ArrayList<>();
    }

    public List<TerritoryData> getSubjects() {
        List<TerritoryData> towns = new ArrayList<>();
        for (String townID : townsInRegion) {
            towns.add(TerritoryUtil.getTerritory(townID));
        }
        return towns;
    }

    public int getNumberOfTownsIn() {
        return townsInRegion.size();
    }

    @Override
    protected void addVassalPrivate(TerritoryData vassal) {
        townsInRegion.add(vassal.getID());
    }


    public void setCapital(String townID) {
        this.capitalID = townID;
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

    @Override
    protected void removeVassal(TerritoryData vassal) {

        EventManager.getInstance().callEvent(new TerritoryIndependanceInternalEvent(this, vassal));

        townsInRegion.remove(vassal.getID());

        TownData town = (TownData) vassal;

        for (RankData rank : getRanks().values()) {
            for (String playerID : town.getPlayerIDList()) {
                rank.removePlayer(playerID);
            }
        }
    }

    @Override
    public TerritoryData getCapital() {
        if(capitalID == null) {
            capitalID = getSubjects().get(0).getID();
        }
        return TerritoryUtil.getTerritory(capitalID);
    }

    @Override
    public double getChunkUpkeepCost() {
        return ConfigUtil.getCustomConfig(ConfigTag.MAIN).getDouble("RegionChunkUpkeepCost", 0);
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
    public void delete() {
        super.delete();

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
    public Collection<TerritoryData> getPotentialVassals() {
        return new ArrayList<>(TownDataStorage.getInstance().getAll().values());
    }


    @Override
    public RankData getRank(ITanPlayer tanPlayer) {
        if(!tanPlayer.hasRegion()){
            return null;
        }
        return getRank(tanPlayer.getRegionRankID());
    }

    @Override
    public List<GuiItem> getOrderedMemberList(ITanPlayer tanPlayer) {
        List<GuiItem> res = new ArrayList<>();
        for (String playerUUID : getOrderedPlayerIDList()) {
            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            ITanPlayer playerIterateData = PlayerDataStorage.getInstance().get(playerUUID);
            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate,
                    Lang.GUI_TOWN_MEMBER_DESC1.get(playerIterateData.getRegionRank().getColoredName()));

            GuiItem playerButton = ItemBuilder.from(playerHead).asGuiItem(event -> event.setCancelled(true));
            res.add(playerButton);
        }
        return res;
    }

    @Override
    protected void specificSetPlayerRank(ITanPlayer playerStat, int rankID) {
        playerStat.setRegionRankID(rankID);
    }

    @Override
    protected void addSpecificTaxes(Budget budget) {
        budget.addProfitLine(new SubjectTaxLine(this));
    }

}
