package org.leralix.tan.data.territory;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.economy.Budget;
import org.leralix.tan.data.territory.economy.SubjectTaxLine;
import org.leralix.tan.data.territory.rank.RankData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.TerritoryIndependanceInternalEvent;
import org.leralix.tan.gui.common.PlayerGUI;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
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
import org.leralix.tan.utils.text.StringUtil;
import org.tan.api.interfaces.territory.TanRegion;

import java.util.*;

public class RegionData extends TerritoryData implements TanRegion {

    private UUID leaderID;
    private String capitalID;
    private final Set<String> townsInRegion;

    public RegionData(String id, String name, ITanPlayer owner) {
        super(id, name, owner);
        TownData ownerTown = owner.getTown();

        this.capitalID = ownerTown.getID();

        this.townsInRegion = new HashSet<>();

        setChunkColor(StringUtil.setBaseRegionColor(ownerTown.getChunkColorCode()));
    }

    public int getHierarchyRank() {
        return 1;
    }

    @Override
    public String getBaseColoredName() {
        return "§b" + getName();
    }

    @Override
    public UUID getLeaderID() {
        if (leaderID == null) leaderID = getCapital().getLeaderID();
        return leaderID;
    }

    @Override
    public ITanPlayer getLeaderData() {
        return PlayerDataStorage.getInstance().get(getLeaderID());
    }

    @Override
    public void setLeaderID(UUID newLeaderID) {
        this.leaderID = newLeaderID;
    }

    @Override
    public boolean isLeader(UUID id) {
        return getLeaderID().equals(id);
    }


    @Override
    public Collection<UUID> getPlayerIDList() {
        ArrayList<UUID> playerList = new ArrayList<>();
        for (TerritoryData townData : getSubjects()) {
            playerList.addAll(townData.getPlayerIDList());
        }
        return playerList;
    }

    @Override
    public Collection<ITanPlayer> getITanPlayerList() {
        ArrayList<ITanPlayer> tanPlayerList = new ArrayList<>();
        for (UUID playerID : getPlayerIDList()) {
            tanPlayerList.add(PlayerDataStorage.getInstance().get(playerID));
        }
        return tanPlayerList;
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
    public void abstractClaimChunk(Chunk chunk, boolean ignoreAdjacent) {

        removeFromBalance(getClaimCost());
        NewClaimedChunkStorage.getInstance().claimRegionChunk(chunk, getID());
    }

    @Override
    protected Collection<TerritoryData> getOverlords() {
        if (!haveOverlord()) {
            return new ArrayList<>();
        }
        return Collections.singletonList(getOverlordInternal().orElse(null));
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
        for (ITanPlayer tanPlayer : getITanPlayerList()) {
            tanPlayer.setNationRankID(null);
        }
    }

    @Override
    protected void collectTaxes() {
        for (TerritoryData town : getVassalsInternal()) {
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
            for (UUID playerID : town.getPlayerIDList()) {
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
    public void openMainMenu(Player player, ITanPlayer playerData) {
        PlayerGUI.dispatchPlayerRegion(player, playerData);
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

    public Optional<NationData> getNation() {
        var optNation = getOverlordInternal();
        if (optNation.isPresent() && optNation.get() instanceof NationData nationData) {
            return Optional.of(nationData);
        }
        return Optional.empty();

    }

    public @Nullable String getNationID() {
        return overlordID;
    }
}
