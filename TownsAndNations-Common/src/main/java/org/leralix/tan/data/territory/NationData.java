package org.leralix.tan.data.territory;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.TownsAndNations;
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
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.tan.api.interfaces.territory.TanNation;

import java.util.*;

public class NationData extends TerritoryData implements Nation, TanNation {

    private UUID leaderID;
    private String capitalID;
    private final Set<String> regionsInNation;

    public NationData(String id, String name, ITanPlayer leader, Region capital) {
        super(id, name, leader);
        this.leaderID = leader.getID();
        this.capitalID = capital.getID();
        this.regionsInNation = new HashSet<>();
    }

    @Override
    public int getHierarchyRank() {
        return 2;
    }

    public String getBaseColoredName() {
        return "§6" + getName();
    }

    @Override
    public UUID getLeaderID() {
        if (leaderID == null) {
            Territory capital = getCapital();
            if (capital != null) {
                leaderID = capital.getLeaderID();
            }
        }
        return leaderID;
    }

    @Override
    public ITanPlayer getLeaderData() {
        return TownsAndNations.getPlugin().getPlayerDataStorage().get(getLeaderID());
    }

    @Override
    public void setLeaderID(UUID leaderID) {
        this.leaderID = leaderID;
    }

    @Override
    public boolean isLeader(UUID playerID) {
        return getLeaderID().equals(playerID);
    }

    @Override
    public Collection<UUID> getPlayerIDList() {
        HashSet<UUID> playerList = new HashSet<>();
        for (Territory regionData : getSubjects()) {
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
        for (UUID playerID : getPlayerIDList()) {
            players.add(TownsAndNations.getPlugin().getPlayerDataStorage().get(playerID));
        }
        return players;
    }

    @Override
    public void broadCastMessage(FilledLang message) {
        for (Territory regionData : getSubjects()) {
            if (regionData == null) {
                continue;
            }
            regionData.broadCastMessage(message);
        }
    }

    @Override
    public void broadcastMessageWithSound(FilledLang message, SoundEnum soundEnum, boolean addPrefix) {
        for (Territory regionData : getSubjects()) {
            if (regionData == null) {
                continue;
            }
            regionData.broadcastMessageWithSound(message, soundEnum, addPrefix);
        }
    }

    @Override
    public void broadcastMessageWithSound(FilledLang message, SoundEnum soundEnum) {
        for (Territory regionData : getSubjects()) {
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
        Territory capital = getCapital();
        String capitalName = capital == null ? Lang.NO_REGION.get(langType) : capital.getName();
        return IconManager.getInstance().get(getIcon())
                .setName(ChatColor.GOLD + getName())
                .setDescription(
                        Lang.GUI_NATION_INFO_DESC0.get(getDescription()),
                        Lang.GUI_NATION_INFO_DESC1.get(capitalName),
                        Lang.GUI_NATION_INFO_DESC2.get(Integer.toString(getVassalsID().size())),
                        Lang.GUI_NATION_INFO_DESC3.get(Integer.toString(getPlayerIDList().size())),
                        Lang.GUI_NATION_INFO_DESC5.get(Integer.toString(getNumberOfClaimedChunk()))
                );
    }

    @Override
    protected Collection<Territory> getOverlords() {
        return new ArrayList<>();
    }

    @Override
    public void removeOverlordPrivate() {
        // Nations do not have overlords
    }

    public List<Territory> getSubjects() {
        List<Territory> regions = new ArrayList<>();
        for (String regionID : regionsInNation) {
            regions.add(TerritoryUtil.getTerritory(regionID));
        }
        return regions;
    }

    @Override
    protected void addVassalPrivate(Territory vassal) {
        regionsInNation.add(vassal.getID());
    }

    @Override
    public void removeVassal(Territory vassal) {
        EventManager.getInstance().callEvent(new TerritoryIndependanceInternalEvent(this, vassal));
        regionsInNation.remove(vassal.getID());

        if (vassal instanceof Region regionData) {
            for (RankData rank : getRanks().values()) {
                for (UUID playerID : regionData.getPlayerIDList()) {
                    rank.removePlayer(playerID);
                }
            }
        }
    }

    @Override
    public int getTotalPlayerCount() {
        int count = 0;
        for (Territory vassal : getSubjects()) {
            count += vassal.getTotalPlayerCount();
        }
        return count;
    }

    @Override
    public Territory getCapital() {
        if (capitalID == null) {
            if (regionsInNation.isEmpty()) {
                return null;
            }
            capitalID = regionsInNation.iterator().next();
        }
        return TerritoryUtil.getTerritory(capitalID);
    }

    @Override
    public void setCapital(String regionID) {
        this.capitalID = regionID;
    }

    @Override
    protected void abstractClaimChunk(Chunk chunk, boolean ignoreAdjacent) {
        removeFromBalance(getClaimCost());
        TownsAndNations.getPlugin().getClaimStorage().claimNationChunk(chunk, getID());
    }

    @Override
    public void openMainMenu(Player player, ITanPlayer playerData) {
        PlayerGUI.dispatchPlayerNation(player, playerData);
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
        return regionsInNation;
    }

    @Override
    public Collection<Territory> getPotentialVassals() {
        return new ArrayList<>(TownsAndNations.getPlugin().getRegionStorage().getAll().values());
    }

    @Override
    public RankData getRank(ITanPlayer tanPlayer) {
        return getRank(tanPlayer.getNationRankID());
    }

    @Override
    protected void specificSetPlayerRank(ITanPlayer playerStat, int rankID) {
        playerStat.setNationRankID(rankID);
    }

    @Override
    protected void addSpecificTaxes(Budget budget) {
        budget.addProfitLine(new SubjectTaxLine(this));
    }

    @Override
    protected void collectTaxes() {
        for (Territory region : getVassalsInternal()) {
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
        TownsAndNations.getPlugin().getNationStorage().delete(getID());
    }
}
