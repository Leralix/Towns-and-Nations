package org.leralix.tan.dataclass.territory;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.territory.economy.Budget;
import org.leralix.tan.dataclass.territory.economy.SubjectTaxLine;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.TerritoryIndependanceInternalEvent;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.database.transactions.TransactionManager;
import org.leralix.tan.storage.database.transactions.instance.TerritoryTaxTransaction;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.graphic.TeamUtils;

import java.util.*;

public class RegionData extends TerritoryData {


    private String leaderID;
    private String capitalID;
    private String nationID;
    private final Set<String> townsInRegion;

    public RegionData(String id, String name, ITanPlayer owner) {
        super(id, name, owner);
        TownData ownerTown = owner.getTown();

        this.capitalID = ownerTown.getID();
        this.nationID = null;

        this.townsInRegion = new HashSet<>();
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
        if (leaderID == null) leaderID = getCapital().getLeaderID();
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
    public IconBuilder getIconWithInformations(LangType langType) {
        return IconManager.getInstance().get(getIcon())
                .setName(ChatColor.AQUA + getName())
                .setDescription(
                        Lang.GUI_REGION_INFO_DESC0.get(getDescription()),
                        Lang.GUI_REGION_INFO_DESC1.get(getCapital().getName()),
                        Lang.GUI_REGION_INFO_DESC2.get(Integer.toString(getNumberOfTownsIn())),
                        Lang.GUI_REGION_INFO_DESC3.get(Integer.toString(getTotalPlayerCount())),
                        Lang.GUI_REGION_INFO_DESC5.get(Integer.toString(getNumberOfClaimedChunk()))
                );
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
    public void abstractClaimChunk(Player player, Chunk chunk, boolean ignoreAdjacent) {

        removeFromBalance(getClaimCost());
        NewClaimedChunkStorage.getInstance().claimRegionChunk(chunk, getID());
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
            double currentBalance = town.getBalance();

            //If town does not have enough money, take what they can give
            if (currentBalance < tax) {
                addToBalance(currentBalance);
                town.removeFromBalance(currentBalance);
                TransactionManager.getInstance().register(new TerritoryTaxTransaction(town.getID(), this.getID(), currentBalance, false));
            }
            else {
                town.removeFromBalance(tax);
                addToBalance(tax);
                TransactionManager.getInstance().register(new TerritoryTaxTransaction(town.getID(), this.getID(), tax, true));
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
        if (capitalID == null) {
            capitalID = getSubjects().getFirst().getID();
        }
        return TerritoryUtil.getTerritory(capitalID);
    }

    @Override
    public void broadCastMessage(FilledLang message) {
        for (TerritoryData townData : getSubjects())
            townData.broadCastMessage(message);
    }

    @Override
    public void broadcastMessageWithSound(FilledLang message, SoundEnum soundEnum, boolean addPrefix) {
        for (TerritoryData townData : getSubjects()){
            townData.broadcastMessageWithSound(message, soundEnum, addPrefix);
        }
    }

    @Override
    public void broadcastMessageWithSound(FilledLang message, SoundEnum soundEnum) {
        for (TerritoryData townData : getSubjects()){
            townData.broadcastMessageWithSound(message, soundEnum);
        }
    }

    @Override
    public boolean haveNoLeader() {
        return false; //Region always have a leader
    }


    @Override
    public synchronized void delete() {
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
    public Set<String> getVassalsID() {
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
        if (!tanPlayer.hasRegion()) {
            return null;
        }
        return getRank(tanPlayer.getRegionRankID());
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
