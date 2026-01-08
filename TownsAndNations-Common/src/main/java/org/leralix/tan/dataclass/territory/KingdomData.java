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
import org.leralix.tan.storage.stored.KingdomDataStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;

import java.util.*;

public class KingdomData extends TerritoryData {

    private String leaderID;
    private String capitalID;
    private final Set<String> regionsInKingdom;

    public KingdomData(String id, String name, ITanPlayer leader, RegionData capital) {
        super(id, name, leader);
        this.leaderID = leader == null ? null : leader.getID();
        this.capitalID = capital == null ? null : capital.getID();
        this.regionsInKingdom = new HashSet<>();
    }

    @Override
    public int getHierarchyRank() {
        return 2;
    }

    @Override
    public String getBaseColoredName() {
        return "§6" + getName();
    }

    @Override
    public String getLeaderID() {
        if (leaderID == null) {
            TerritoryData capital = getCapital();
            if (capital != null) {
                leaderID = capital.getLeaderID();
            }
        }
        return leaderID;
    }

    @Override
    public ITanPlayer getLeaderData() {
        return PlayerDataStorage.getInstance().get(getLeaderID());
    }

    @Override
    public void setLeaderID(String leaderID) {
        this.leaderID = leaderID;
    }

    @Override
    public boolean isLeader(String playerID) {
        return Objects.equals(getLeaderID(), playerID);
    }

    @Override
    public Collection<String> getPlayerIDList() {
        ArrayList<String> playerList = new ArrayList<>();
        for (TerritoryData regionData : getSubjects()) {
            if (regionData == null) {
                continue;
            }
            playerList.addAll(regionData.getPlayerIDList());
        }
        return playerList;
    }

    @Override
    public Collection<ITanPlayer> getITanPlayerList() {
        ArrayList<ITanPlayer> players = new ArrayList<>();
        for (String playerID : getPlayerIDList()) {
            players.add(PlayerDataStorage.getInstance().get(playerID));
        }
        return players;
    }

    @Override
    public void broadCastMessage(FilledLang message) {
        for (TerritoryData regionData : getSubjects()) {
            if (regionData == null) {
                continue;
            }
            regionData.broadCastMessage(message);
        }
    }

    @Override
    public void broadcastMessageWithSound(FilledLang message, SoundEnum soundEnum, boolean addPrefix) {
        for (TerritoryData regionData : getSubjects()) {
            if (regionData == null) {
                continue;
            }
            regionData.broadcastMessageWithSound(message, soundEnum, addPrefix);
        }
    }

    @Override
    public void broadcastMessageWithSound(FilledLang message, SoundEnum soundEnum) {
        for (TerritoryData regionData : getSubjects()) {
            if (regionData == null) {
                continue;
            }
            regionData.broadcastMessageWithSound(message, soundEnum);
        }
    }

    @Override
    public boolean haveNoLeader() {
        return false;
    }

    @Override
    public IconBuilder getIconWithInformations(LangType langType) {
        TerritoryData capital = getCapital();
        String capitalName = capital == null ? Lang.NO_REGION.get(langType) : capital.getName();
        return IconManager.getInstance().get(getIcon())
                .setName(ChatColor.GOLD + getName())
                .setDescription(
                        Lang.GUI_KINGDOM_INFO_DESC0.get(getDescription()),
                        Lang.GUI_KINGDOM_INFO_DESC1.get(capitalName),
                        Lang.GUI_KINGDOM_INFO_DESC2.get(Integer.toString(getVassalsID().size())),
                        Lang.GUI_KINGDOM_INFO_DESC3.get(Integer.toString(getPlayerIDList().size())),
                        Lang.GUI_KINGDOM_INFO_DESC5.get(Integer.toString(getNumberOfClaimedChunk()))
                );
    }

    @Override
    protected Collection<TerritoryData> getOverlords() {
        return new ArrayList<>();
    }

    @Override
    public void removeOverlordPrivate() {
        throw new UnsupportedOperationException("Kingdoms cannot have an overlord");
    }

    public List<TerritoryData> getSubjects() {
        List<TerritoryData> regions = new ArrayList<>();
        for (String regionID : regionsInKingdom) {
            regions.add(org.leralix.tan.utils.gameplay.TerritoryUtil.getTerritory(regionID));
        }
        return regions;
    }

    @Override
    protected void addVassalPrivate(TerritoryData vassal) {
        regionsInKingdom.add(vassal.getID());
    }

    @Override
    protected void removeVassal(TerritoryData vassal) {
        EventManager.getInstance().callEvent(new TerritoryIndependanceInternalEvent(this, vassal));
        regionsInKingdom.remove(vassal.getID());

        if (vassal instanceof RegionData regionData) {
            for (RankData rank : getRanks().values()) {
                for (String playerID : regionData.getPlayerIDList()) {
                    rank.removePlayer(playerID);
                }
            }
        }
    }

    @Override
    public TerritoryData getCapital() {
        if (capitalID == null) {
            if (regionsInKingdom.isEmpty()) {
                return null;
            }
            capitalID = regionsInKingdom.iterator().next();
        }
        return org.leralix.tan.utils.gameplay.TerritoryUtil.getTerritory(capitalID);
    }

    public void setCapital(String regionID) {
        this.capitalID = regionID;
        this.leaderID = null;
    }

    @Override
    protected void abstractClaimChunk(Player player, Chunk chunk, boolean ignoreAdjacent) {
        removeFromBalance(getClaimCost());
        NewClaimedChunkStorage.getInstance().claimKingdomChunk(chunk, getID());
    }

    @Override
    public void openMainMenu(Player player) {
        PlayerGUI.dispatchPlayerKingdom(player);
    }

    @Override
    public boolean canHaveVassals() {
        return true;
    }

    @Override
    public boolean canHaveOverlord() {
        return false;
    }

    @Override
    public Set<String> getVassalsID() {
        return regionsInKingdom;
    }

    @Override
    public boolean isVassal(String territoryID) {
        return regionsInKingdom.contains(territoryID);
    }

    @Override
    public Collection<TerritoryData> getPotentialVassals() {
        return new ArrayList<>(RegionDataStorage.getInstance().getAll().values());
    }

    @Override
    public RankData getRank(ITanPlayer tanPlayer) {
        return getRank(tanPlayer.getRankID(this));
    }

    @Override
    protected void specificSetPlayerRank(ITanPlayer playerStat, int rankID) {
        playerStat.setRankID(this, rankID);
    }

    @Override
    protected void addSpecificTaxes(Budget budget) {
        budget.addProfitLine(new SubjectTaxLine(this));
    }

    @Override
    protected void collectTaxes() {
        for (TerritoryData region : getVassals()) {
            if (region == null) {
                continue;
            }

            double tax = getTax();
            double currentBalance = region.getBalance();

            if (currentBalance < tax) {
                addToBalance(currentBalance);
                region.removeFromBalance(currentBalance);
                TransactionManager.getInstance().register(new TerritoryTaxTransaction(region.getID(), this.getID(), currentBalance, false));
            } else {
                region.removeFromBalance(tax);
                addToBalance(tax);
                TransactionManager.getInstance().register(new TerritoryTaxTransaction(region.getID(), this.getID(), tax, true));
            }
        }
    }

    @Override
    public synchronized void delete() {
        super.delete();
        KingdomDataStorage.getInstance().delete(getID());
    }
}
