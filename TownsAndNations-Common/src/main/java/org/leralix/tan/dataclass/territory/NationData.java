package org.leralix.tan.dataclass.territory;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.territory.economy.Budget;
import org.leralix.tan.dataclass.territory.economy.NationTaxLine;
import org.leralix.tan.dataclass.territory.economy.SubjectTaxLine;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.database.transactions.TransactionManager;
import org.leralix.tan.storage.database.transactions.instance.TerritoryTaxTransaction;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

import java.util.*;

public class NationData extends TerritoryData {

    private String leaderID;
    private String capitalID;
    private final Set<String> regionsInNation;

    public NationData(String id, String name, ITanPlayer owner) {
        super(id, name, owner);

        if (owner != null) {
            this.leaderID = owner.getID();
            if (owner.hasRegion()) {
                this.capitalID = owner.getRegion().getID();
            }
        }

        this.regionsInNation = new HashSet<>();
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
        return leaderID;
    }

    @Override
    public ITanPlayer getLeaderData() {
        if (leaderID == null) {
            return null;
        }
        return PlayerDataStorage.getInstance().get(leaderID);
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
    public boolean haveNoLeader() {
        return leaderID == null;
    }

    @Override
    public Collection<String> getPlayerIDList() {
        ArrayList<String> playerList = new ArrayList<>();
        for (TerritoryData regionData : getVassals()) {
            if (regionData == null) continue;
            playerList.addAll(regionData.getPlayerIDList());
        }
        return playerList;
    }

    @Override
    public Collection<ITanPlayer> getITanPlayerList() {
        ArrayList<ITanPlayer> res = new ArrayList<>();
        for (String playerID : getPlayerIDList()) {
            res.add(PlayerDataStorage.getInstance().get(playerID));
        }
        return res;
    }

    @Override
    public IconBuilder getIconWithInformations(LangType langType) {
        return IconManager.getInstance().get(getIcon())
                .setName(ChatColor.GOLD + getName())
                .setDescription(
                        Lang.GUI_REGION_INFO_DESC0.get(getDescription()),
                        Lang.GUI_TOWN_INFO_DESC1.get(getLeaderName()),
                        Lang.GUI_REGION_INFO_DESC3.get(Integer.toString(getPlayerIDList().size())),
                        Lang.GUI_REGION_INFO_DESC5.get(Integer.toString(getNumberOfClaimedChunk()))
                );
    }

    @Override
    public void abstractClaimChunk(Player player, Chunk chunk, boolean ignoreAdjacent) {
    }

    @Override
    protected Collection<TerritoryData> getOverlords() {
        return Collections.emptyList();
    }

    @Override
    public void removeOverlordPrivate() {
    }

    @Override
    protected void collectTaxes() {
        for (TerritoryData region : getVassals()) {
            if (region == null) continue;
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
    protected void removeVassal(TerritoryData vassal) {
        regionsInNation.remove(vassal.getID());

        for (RankData rank : getRanks().values()) {
            for (String playerID : vassal.getPlayerIDList()) {
                rank.removePlayer(playerID);
            }
        }
    }

    @Override
    protected void addVassalPrivate(TerritoryData vassal) {
        regionsInNation.add(vassal.getID());
    }

    @Override
    public TerritoryData getCapital() {
        if (capitalID == null) {
            return null;
        }
        return TerritoryUtil.getTerritory(capitalID);
    }

    @Override
    public void broadCastMessage(FilledLang message) {
        for (TerritoryData regionData : getVassals()) {
            if (regionData == null) continue;
            regionData.broadCastMessage(message);
        }
    }

    @Override
    public void broadcastMessageWithSound(FilledLang message, SoundEnum soundEnum, boolean addPrefix) {
        for (TerritoryData regionData : getVassals()) {
            if (regionData == null) continue;
            regionData.broadcastMessageWithSound(message, soundEnum, addPrefix);
        }
    }

    @Override
    public void broadcastMessageWithSound(FilledLang message, SoundEnum soundEnum) {
        for (TerritoryData regionData : getVassals()) {
            if (regionData == null) continue;
            regionData.broadcastMessageWithSound(message, soundEnum);
        }
    }

    @Override
    public void openMainMenu(Player player) {
        new org.leralix.tan.gui.user.territory.NationMenu(player, this);
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
    public boolean isVassal(String territoryID) {
        return regionsInNation.contains(territoryID);
    }

    @Override
    public Collection<TerritoryData> getPotentialVassals() {
        return new ArrayList<>(RegionDataStorage.getInstance().getAll().values());
    }

    @Override
    public RankData getRank(ITanPlayer tanPlayer) {
        if (!tanPlayer.hasNation()) {
            return null;
        }
        return getRank(tanPlayer.getNationRankID());
    }

    @Override
    protected void specificSetPlayerRank(ITanPlayer playerStat, int rankID) {
        playerStat.setNationRankID(rankID);
    }

    @Override
    protected void addSpecificTaxes(Budget budget) {
        budget.addProfitLine(new NationTaxLine(this));
    }
}
