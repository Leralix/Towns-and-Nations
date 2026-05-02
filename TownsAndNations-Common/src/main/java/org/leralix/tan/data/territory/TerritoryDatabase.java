package org.leralix.tan.data.territory;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.DataWrapperFactory;
import org.leralix.tan.data.DbManager;
import org.leralix.tan.data.building.Building;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.chunk.TerritoryChunkData;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.cosmetic.BannerBuilder;
import org.leralix.tan.data.territory.cosmetic.ICustomIcon;
import org.leralix.tan.data.territory.economy.Budget;
import org.leralix.tan.data.territory.permission.IClaimedChunkSettings;
import org.leralix.tan.data.territory.rank.RankData;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.data.territory.relation.DiplomacyProposal;
import org.leralix.tan.data.territory.relation.RelationData;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.data.territory.teleportation.TeleportationData;
import org.leralix.tan.data.upgrade.TerritoryStats;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.war.attack.CurrentAttack;
import org.tan.api.enums.EDiplomacyState;
import org.tan.api.enums.TerritoryPermission;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.buildings.TanProperty;
import org.tan.api.interfaces.chunk.TanClaimedChunk;
import org.tan.api.interfaces.territory.TanTerritory;

import java.util.*;
import java.util.function.Consumer;

public abstract class TerritoryDatabase<T extends TerritoryData> implements Territory {

    private final DbManager<T> manager;

    private T data;

    protected TerritoryDatabase(DbManager<T> manager, T data) {
        this.manager = manager;
        this.data = data;
    }

    protected void setTerritoryData(T data) {
        this.data = data;
    }

    @Override
    public String getID() {
        return data.getID();
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public void setName(String newName) {
        mutate(p -> p.setName(newName));
    }

    @Override
    public Optional<Town> getCapitalTown() {
        return data.getCapitalTown();
    }

    @Override
    public int getHierarchyRank() {
        return data.getHierarchyRank();
    }

    @Override
    public TextComponent getCustomColoredName() {
        return data.getCustomColoredName();
    }

    @Override
    public UUID getLeaderID() {
        return data.getLeaderID();
    }

    @Override
    public ITanPlayer getLeaderData() {
        return data.getLeaderData();
    }

    @Override
    public void setLeaderID(UUID leaderID) {
        mutate(p -> p.setLeaderID(leaderID));
    }

    @Override
    public boolean isLeader(UUID playerID) {
        return data.isLeader(playerID);
    }

    @Override
    public String getDescription() {
        return data.getDescription();
    }

    @Override
    public void setDescription(String newDescription) {
        mutate(p -> p.setDescription(newDescription));
    }

    @Override
    public TanPlayer getOwner() {
        return data.getOwner();
    }

    @Override
    public ItemStack getIcon() {
        return data.getIcon();
    }

    @Override
    public Color getColor() {
        return data.getColor();
    }

    @Override
    public void setColor(Color color) {
        mutate(p -> p.setColor(color));
    }

    @Override
    public int getNumberOfClaimedChunk() {
        return data.getNumberOfClaimedChunk();
    }

    @Override
    public Collection<TanClaimedChunk> getClaimedChunks() {
        return data.getClaimedChunks();
    }

    @Override
    public Collection<TanPlayer> getMembers() {
        return data.getMembers();
    }

    @Override
    public Collection<TanTerritory> getVassals() {
        return data.getVassals();
    }

    @Override
    public void setIcon(ICustomIcon icon) {
        mutate(p -> p.setIcon(icon));
    }

    @Override
    public Collection<UUID> getPlayerIDList() {
        return data.getPlayerIDList();
    }

    @Override
    public boolean isPlayerIn(UUID playerID) {
        return data.isPlayerIn(playerID);
    }

    @Override
    public Collection<UUID> getOrderedPlayerIDList() {
        return data.getOrderedPlayerIDList();
    }

    @Override
    public Collection<ITanPlayer> getITanPlayerList() {
        return data.getITanPlayerList();
    }

    @Override
    public IClaimedChunkSettings getChunkSettings() {
        return DataWrapperFactory.wrap(
                data.getChunkSettings(),
                () -> manager.save(data),
                IClaimedChunkSettings.class
        );
    }

    @Override
    public RelationData getRelations() {
        return data.getRelations();
    }

    @Override
    public void removeDiplomaticProposal(String proposingTerritoryID) {
        mutate(p -> p.removeDiplomaticProposal(proposingTerritoryID));
    }

    @Override
    public void addDiplomaticProposal(Territory proposingTerritory, TownRelation wantedRelation) {
        mutate(p -> p.addDiplomaticProposal(proposingTerritory, wantedRelation));
    }

    @Override
    public Collection<DiplomacyProposal> getAllDiplomacyProposal() {
        return data.getAllDiplomacyProposal();
    }

    @Override
    public TownRelation getWorstRelationWith(ITanPlayer player) {
        return data.getWorstRelationWith(player);
    }

    @Override
    public void setRelation(TownRelation relation, String territoryID) {
        mutate(p -> p.setRelation(relation, territoryID));
    }

    @Override
    public List<String> getTerritoriesIDWithRelation(TownRelation relation) {
        return data.getTerritoriesIDWithRelation(relation);
    }

    @Override
    public TownRelation getRelationWith(String territoryID) {
        return data.getRelationWith(territoryID);
    }

    @Override
    public List<Territory> getTerritoriesWithRelation(TownRelation townRelation) {
        return data.getTerritoriesWithRelation(townRelation);
    }

    @Override
    public long getCreationDate() {
        return data.getCreationDate();
    }

    @Override
    public void broadCastMessage(FilledLang message) {
        data.broadCastMessage(message);
    }

    @Override
    public void broadcastMessageWithSound(FilledLang message, SoundEnum soundEnum, boolean addPrefix) {
        data.broadcastMessageWithSound(message, soundEnum, addPrefix);
    }

    @Override
    public void broadcastMessageWithSound(FilledLang message, SoundEnum soundEnum) {
        data.broadcastMessageWithSound(message, soundEnum);
    }

    @Override
    public boolean haveNoLeader() {
        return data.haveNoLeader();
    }

    @Override
    public IconBuilder getIconWithInformations(LangType langType) {
        return data.getIconWithInformations(langType);
    }

    @Override
    public IconBuilder getIconWithInformationAndRelation(Territory territoryData, LangType langType) {
        return data.getIconWithInformationAndRelation(territoryData, langType);
    }

    @Override
    public Collection<CurrentAttack> getCurrentAttacks() {
        return data.getCurrentAttacks();
    }

    @Override
    public double getBalance() {
        return data.getBalance();
    }

    @Override
    public void addToBalance(double balance) {
        mutate(p -> p.addToBalance(balance));
    }

    @Override
    public void removeFromBalance(double balance) {
        mutate(p -> p.removeFromBalance(balance));
    }

    @Override
    public void setOverlord(Territory overlord) {
        mutate(p -> p.setOverlord(overlord));
    }

    @Override
    public Optional<Territory> getOverlordInternal() {
        return data.getOverlordInternal();
    }

    @Override
    public void removeOverlord() {
        mutate(TerritoryData::removeOverlord);
    }

    @Override
    public void addVassal(Territory vassal) {
        mutate(p -> p.addVassal(vassal));
    }

    @Override
    public boolean isCapital() {
        return data.isCapital();
    }

    @Override
    public Territory getCapital() {
        return data.getCapital();
    }

    @Override
    public int getChunkColorCode() {
        return data.getChunkColorCode();
    }

    @Override
    public ChatColor getChunkColor() {
        return data.getChunkColor();
    }

    @Override
    public void setChunkColor(int color) {
        mutate(p -> p.setChunkColor(color));
    }

    @Override
    public boolean haveOverlord() {
        return data.haveOverlord();
    }

    @Override
    public String getChunkColorInHex() {
        return data.getChunkColorInHex();
    }

    @Override
    public Optional<TanTerritory> getOverlord() {
        return data.getOverlord();
    }

    @Override
    public boolean canPlayerDoAction(TanPlayer player, TerritoryPermission permission) {
        return data.canPlayerDoAction(player, permission);
    }

    @Override
    public boolean checkPlayerPermission(TanPlayer player, TerritoryPermission rolePermission) {
        return data.checkPlayerPermission(player, rolePermission);
    }

    @Override
    public Map<String, Integer> getAvailableEnemyClaims() {
        return data.getAvailableEnemyClaims();
    }

    @Override
    public void addAvailableClaims(String territoryID, int amount) {
        mutate(p -> p.addAvailableClaims(territoryID, amount));
    }

    @Override
    public void consumeEnemyClaim(String territoryID) {
        mutate(p -> p.consumeEnemyClaim(territoryID));
    }

    @Override
    public boolean claimChunk(Player player, ITanPlayer tanPlayer, Chunk chunk) {
        boolean result = data.claimChunk(player, tanPlayer, chunk);
        manager.save(data);
        return result;
    }

    @Override
    public boolean claimChunk(Player player, ITanPlayer playerData, Chunk chunk, boolean ignoreAdjacent) {
        boolean result = data.claimChunk(player, playerData, chunk, ignoreAdjacent);
        manager.save(data);
        return result;
    }

    @Override
    public int getClaimCost() {
        return data.getClaimCost();
    }

    @Override
    public void delete() {
        mutate(Territory::delete);
    }

    @Override
    public boolean canConquerChunk(TerritoryChunkData chunk) {
        return data.canConquerChunk(chunk);
    }

    @Override
    public void addDonation(Player player, double amount) {
        mutate(p -> p.addDonation(player, amount));
    }

    @Override
    public void openMainMenu(Player player, ITanPlayer playerData) {
        data.openMainMenu(player, playerData);
    }

    @Override
    public boolean canHaveVassals() {
        return data.canHaveVassals();
    }

    @Override
    public boolean canHaveOverlord() {
        return data.canHaveOverlord();
    }

    @Override
    public Set<String> getVassalsID() {
        return data.getVassalsID();
    }

    @Override
    public List<Territory> getVassalsInternal() {
        return data.getVassalsInternal();
    }

    @Override
    public Collection<Territory> getPotentialVassals() {
        return data.getPotentialVassals();
    }

    @Override
    public List<String> getOverlordsProposals() {
        return data.getOverlordsProposals();
    }

    @Override
    public void addVassalisationProposal(Territory proposal) {
        mutate(p -> p.addVassalisationProposal(proposal));
    }

    @Override
    public Map<Integer, RankData> getRanks() {
        return data.getRanks();
    }

    @Override
    public Collection<RankData> getAllRanksSorted() {
        return data.getAllRanksSorted();
    }

    @Override
    public RankData getRank(int rankID) {
        return data.getRank(rankID);
    }

    @Override
    public RankData getRank(ITanPlayer tanPlayer) {
        return data.getRank(tanPlayer);
    }

    @Override
    public boolean isRankNameUsed(String message) {
        return data.isRankNameUsed(message);
    }

    @Override
    public RankData registerNewRank(String rankName) {
        return data.registerNewRank(rankName);
    }

    @Override
    public void removeRank(int key) {
        mutate(p -> p.removeRank(key));
    }

    @Override
    public int getDefaultRankID() {
        return data.getDefaultRankID();
    }

    @Override
    public void setDefaultRank(int rankID) {
        mutate(p -> p.setDefaultRank(rankID));
    }

    @Override
    public boolean doesPlayerHavePermission(ITanPlayer tanPlayer, RolePermission townRolePermission) {
        return data.doesPlayerHavePermission(tanPlayer, townRolePermission);
    }

    @Override
    public void setPlayerRank(ITanPlayer playerStat, RankData rankData) {
        mutate(p -> p.setPlayerRank(playerStat, rankData));
    }

    @Override
    public Budget getBudget() {
        return data.getBudget();
    }

    @Override
    public double getTax() {
        return data.getTax();
    }

    @Override
    public void setTax(double newTax) {
        mutate(p -> p.setTax(newTax));
    }

    @Override
    public void addToTax(double i) {
        mutate(p -> p.addToTax(i));
    }

    @Override
    public void executeTasks() {
        mutate(Territory::executeTasks);
    }

    @Override
    public double getTaxOnRentingProperty() {
        return data.getTaxOnRentingProperty();
    }

    @Override
    public void setTaxOnRentingProperty(double amount) {
        mutate(p -> p.setTaxOnRentingProperty(amount));
    }

    @Override
    public double getTaxOnBuyingProperty() {
        return data.getTaxOnBuyingProperty();
    }

    @Override
    public void setTaxOnBuyingProperty(double amount) {
        mutate(p -> p.setTaxOnBuyingProperty(amount));
    }

    @Override
    public double getTaxOnCreatingProperty() {
        return data.getTaxOnCreatingProperty();
    }

    @Override
    public void setTaxOnCreatingProperty(double amount) {
        mutate(p -> p.setTaxOnCreatingProperty(amount));
    }

    @Override
    public boolean attackInProgress() {
        return data.attackInProgress();
    }

    @Override
    public boolean isAtWar() {
        return data.isAtWar();
    }

    @Override
    public RankData getDefaultRank() {
        return data.getDefaultRank();
    }

    @Override
    public String getColoredName() {
        return data.getColoredName();
    }

    @Override
    public String getLeaderName() {
        return data.getLeaderName();
    }

    @Override
    public List<String> getOwnedFortIDs() {
        return data.getOwnedFortIDs();
    }

    @Override
    public List<String> getOccupiedFortIds() {
        return data.getOccupiedFortIds();
    }

    @Override
    public Collection<Building> getBuildings() {
        return data.getBuildings();
    }

    @Override
    public void addOwnedFort(Fort fortToCapture) {
        mutate(p -> p.addOwnedFort(fortToCapture));
    }

    @Override
    public void removeOwnedFort(Fort fortToCapture) {
        mutate(p -> p.removeOwnedFort(fortToCapture));
    }

    @Override
    public void applyToAllOnlinePlayer(Consumer<Player> action) {
        mutate(p -> p.applyToAllOnlinePlayer(action));
    }

    @Override
    public TerritoryStats getNewLevel() {
        return data.getNewLevel();
    }

    @Override
    public int getNumberOfOccupiedChunk() {
        return data.getNumberOfOccupiedChunk();
    }

    @Override
    public void checkIfShouldSurrender() {
        mutate(Territory::checkIfShouldSurrender);
    }

    @Override
    public void setBanner(Material material, List<Pattern> patterns) {
        mutate(p -> p.setBanner(material, patterns));
    }

    @Override
    public BannerBuilder getBanner() {
        return data.getBanner();
    }

    @Override
    public int getLevel() {
        return data.getLevel();
    }

    @Override
    public EDiplomacyState getRelationWith(TanPlayer playerData) {
        return data.getRelationWith(playerData);
    }

    @Override
    public Collection<TanProperty> getProperties() {
        return data.getProperties();
    }

    @Override
    public TeleportationData getTeleportationData() {
        return data.getTeleportationData();
    }

    @Override
    public void broadCastBarMessage(FilledLang filledLang) {
        data.broadCastBarMessage(filledLang);
    }

    @Override
    public boolean authorizeTeleportation(Territory territoryData) {
        return data.authorizeTeleportation(territoryData);
    }

    @Override
    public int getTotalPlayerCount() {
        return data.getTotalPlayerCount();
    }

    protected synchronized void mutate(Consumer<T> action) {
        action.accept(data);
        manager.save(data);
    }
}
