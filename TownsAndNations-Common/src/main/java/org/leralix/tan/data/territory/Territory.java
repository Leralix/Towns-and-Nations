package org.leralix.tan.data.territory;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
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
import org.leralix.tan.data.territory.relation.Relation;
import org.leralix.tan.data.territory.relation.RelationData;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.data.territory.teleportation.TeleportationData;
import org.leralix.tan.data.upgrade.TerritoryStats;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.war.attack.CurrentAttack;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.territory.TanTerritory;

import java.util.*;
import java.util.function.Consumer;

public interface Territory extends TanTerritory, Relation {

    String getID();

    String getName();

    void setName(String newName);

    Optional<Town> getCapitalTown();

    int getHierarchyRank();

    TextComponent getCustomColoredName();

    UUID getLeaderID();

    ITanPlayer getLeaderData();

    void setLeaderID(UUID leaderID);

    default boolean isLeader(TanPlayer tanPlayer){
        return isLeader(tanPlayer.getID());
    }

    default boolean isLeader(Player player){
        return isLeader(player.getUniqueId());
    }

    boolean isLeader(UUID playerID);


    String getDescription();

    void setDescription(String newDescription);

    ItemStack getIcon();

    void setIcon(ICustomIcon icon);

    Collection<UUID> getPlayerIDList();

    boolean isPlayerIn(UUID playerID);

    default boolean isPlayerIn(TanPlayer tanPlayer) {
        return isPlayerIn(tanPlayer.getID());
    }

    default boolean isPlayerIn(Player player) {
        return isPlayerIn(player.getUniqueId());
    }

    Collection<UUID> getOrderedPlayerIDList();

    Collection<ITanPlayer> getITanPlayerList();

    IClaimedChunkSettings getChunkSettings();

    RelationData getRelations();

    default void removeDiplomaticProposal(Territory proposingTerritory) {
        removeDiplomaticProposal(proposingTerritory.getID());
    }

    void removeDiplomaticProposal(String proposingTerritoryID);

    void addDiplomaticProposal(Territory proposingTerritory, TownRelation wantedRelation);

    Collection<DiplomacyProposal> getAllDiplomacyProposal();

    TownRelation getWorstRelationWith(ITanPlayer player);

    default TownRelation getRelationWith(Territory territoryData) {
        return getRelationWith(territoryData.getID());
    }

    TownRelation getRelationWith(String territoryID);

    long getCreationDate();

    void broadCastMessage(FilledLang message);

    void broadcastMessageWithSound(FilledLang message, SoundEnum soundEnum, boolean addPrefix);

    void broadcastMessageWithSound(FilledLang message, SoundEnum soundEnum);

    boolean haveNoLeader();

    IconBuilder getIconWithInformations(LangType langType);

    IconBuilder getIconWithInformationAndRelation(Territory territoryData, LangType langType);

    Collection<CurrentAttack> getCurrentAttacks();

    double getBalance();

    void addToBalance(double balance);

    void removeFromBalance(double balance);

    void setOverlord(Territory overlord);

    Optional<Territory> getOverlordInternal();

    void removeOverlord();

    void addVassal(Territory vassal);

    boolean isCapital();

    Territory getCapital();

    int getChunkColorCode();

    ChatColor getChunkColor();

    void setChunkColor(int color);

    boolean haveOverlord();

    Map<String, Integer> getAvailableEnemyClaims();

    void addAvailableClaims(String territoryID, int amount);

    void consumeEnemyClaim(String territoryID);

    /**
     * Claim the chunk for the territory. The chunk used will be the one where the player stands
     *
     * @param player The player wishing to claim a chunk
     * @return True if the chunk has been claimed successfully, false otherwise
     */
    default boolean claimChunk(Player player, ITanPlayer tanPlayer) {
        return claimChunk(player, tanPlayer, player.getLocation().getChunk());
    }

    /**
     * Claim the chunk for the territory. The chunk used will be the one where the player stands
     *
     * @param player The player wishing to claim a chunk
     * @param chunk  The chunk to claim
     * @return True if the chunk has been claimed successfully, false otherwise
     */
    boolean claimChunk(Player player, ITanPlayer tanPlayer, Chunk chunk);

    /**
     * Claim the chunk for the territory
     *
     * @param player         The player wishing to claim a chunk
     * @param chunk          The chunk to claim
     * @param ignoreAdjacent Defines whether the chunk to claim should respect adjacent claiming
     * @return True if the chunk has been claimed successfully, false otherwise
     */
    boolean claimChunk(Player player, ITanPlayer playerData, Chunk chunk, boolean ignoreAdjacent);

    int getClaimCost();

    void delete();

    /**
     * Check if the territory can claim over another one.
     * If the territory can claim, the number of chunks it can claim will be directly decreased.
     * @param chunk The chunk wanted to overclaim
     * @return True if this territory can claim, false otherwise.
     */
    boolean canConquerChunk(TerritoryChunkData chunk);

    void addDonation(Player player, double amount);

    void openMainMenu(Player player, ITanPlayer playerData);

    boolean canHaveVassals();

    boolean canHaveOverlord();

    Set<String> getVassalsID();

    List<Territory> getVassalsInternal();

    default int getVassalCount() {
        return getVassalsID().size();
    }

    /**
     * @return All the territories that can be vassals of this territory.
     */
    Collection<Territory> getPotentialVassals();

    List<String> getOverlordsProposals();

    List<Territory> getSubjects();

    void addVassalisationProposal(Territory proposal);

    default boolean containsVassalisationProposal(Territory proposal) {
        return getOverlordsProposals().contains(proposal.getID());
    }

    default int getNumberOfVassalisationProposals(){
        return getOverlordsProposals().size();
    }

    Map<Integer, RankData> getRanks();

    default Collection<RankData> getAllRanks() {
        return getRanks().values();
    }

    Collection<RankData> getAllRanksSorted();

    RankData getRank(int rankID);

    RankData getRank(ITanPlayer tanPlayer);

    default int getNumberOfRank(){
        return getRanks().size();
    }

    boolean isRankNameUsed(String message);

    RankData registerNewRank(String rankName);

    void removeRank(int key);

    int getDefaultRankID();

    default void setDefaultRank(RankData rank){
        setDefaultRank(rank.getID());
    }

    void setDefaultRank(int rankID);

    boolean doesPlayerHavePermission(ITanPlayer tanPlayer, RolePermission townRolePermission);

    void setPlayerRank(ITanPlayer playerStat, RankData rankData);

    Budget getBudget();

    double getTax();

    void setTax(double newTax);

    void addToTax(double i);

    void executeTasks();

    double getTaxOnRentingProperty();

    void setTaxOnRentingProperty(double amount);

    double getTaxOnBuyingProperty();

    void setTaxOnBuyingProperty(double amount);

    double getTaxOnCreatingProperty();

    void setTaxOnCreatingProperty(double amount);

    boolean attackInProgress();

    boolean isAtWar();

    RankData getDefaultRank();

    void registerPlayer(ITanPlayer tanPlayer);

    void unregisterPlayer(ITanPlayer tanPlayer);

    String getColoredName();

    String getLeaderName();

    List<String> getOwnedFortIDs();

    List<String> getOccupiedFortIds();

    default void removeOccupiedFort(Fort fort) {
        removeOccupiedFortID(fort.getID());
    }

    default void removeOccupiedFortID(String fortID) {
        getOccupiedFortIds().remove(fortID);
    }

    default void addOccupiedFort(Fort fort) {
        addOccupiedFortID(fort.getID());
    }

    default void addOccupiedFortID(String fortID) {
        getOccupiedFortIds().add(fortID);
    }

    default void removeFort(String fortID) {
        getOwnedFortIDs().remove(fortID);
    }

    Collection<Building> getBuildings();

    void addOwnedFort(Fort fortToCapture);

    void removeOwnedFort(Fort fortToCapture);

    void applyToAllOnlinePlayer(Consumer<Player> action);

    TerritoryStats getNewLevel();

    int getNumberOfOccupiedChunk();

    void checkIfShouldSurrender();

    void setBanner(Material material, List<Pattern> patterns);

    BannerBuilder getBanner();

    int getLevel();

    TeleportationData getTeleportationData();

    void broadCastBarMessage(FilledLang filledLang);

    boolean authorizeTeleportation(Territory territoryData);

    void removeVassal(Territory territoryData);

    int getTotalPlayerCount();
}
