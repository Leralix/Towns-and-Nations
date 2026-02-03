package org.leralix.tan.data.territory;


import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.position.Vector3D;
import org.leralix.lib.utils.RandomUtil;
import org.leralix.tan.data.building.Building;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.chunk.ClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunk;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.cosmetic.BannerBuilder;
import org.leralix.tan.data.territory.cosmetic.CustomIcon;
import org.leralix.tan.data.territory.cosmetic.ICustomIcon;
import org.leralix.tan.data.territory.cosmetic.PlayerHeadIcon;
import org.leralix.tan.data.territory.economy.Budget;
import org.leralix.tan.data.territory.economy.ChunkUpkeepLine;
import org.leralix.tan.data.territory.economy.SalaryPaymentLine;
import org.leralix.tan.data.territory.permission.ClaimedChunkSettings;
import org.leralix.tan.data.territory.permission.PermissionGiven;
import org.leralix.tan.data.territory.rank.RankData;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.data.territory.relation.DiplomacyProposal;
import org.leralix.tan.data.territory.relation.RelationData;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.data.upgrade.TerritoryStats;
import org.leralix.tan.data.upgrade.rewards.StatsType;
import org.leralix.tan.data.upgrade.rewards.list.BiomeStat;
import org.leralix.tan.data.upgrade.rewards.numeric.ChunkCap;
import org.leralix.tan.data.upgrade.rewards.numeric.ChunkCost;
import org.leralix.tan.data.upgrade.rewards.numeric.ChunkUpkeepCost;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.DiplomacyProposalAcceptedInternalEvent;
import org.leralix.tan.events.events.DiplomacyProposalInternalEvent;
import org.leralix.tan.events.events.TerritoryVassalAcceptedInternalEvent;
import org.leralix.tan.events.events.TerritoryVassalProposalInternalEvent;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.ClaimBlacklistStorage;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.database.transactions.TransactionManager;
import org.leralix.tan.storage.database.transactions.instance.DonationTransaction;
import org.leralix.tan.storage.database.transactions.instance.SalaryTransaction;
import org.leralix.tan.storage.database.transactions.instance.TerritoryChunkUpkeepTransaction;
import org.leralix.tan.storage.stored.FortStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.graphic.PrefixUtil;
import org.leralix.tan.utils.graphic.TeamUtils;
import org.leralix.tan.utils.territory.ChunkUtil;
import org.leralix.tan.utils.text.StringUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.war.War;
import org.leralix.tan.war.attack.CurrentAttack;
import org.leralix.tan.war.info.WarRole;
import org.tan.api.enums.TerritoryPermission;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.territory.TanTerritory;
import org.tan.api.interfaces.chunk.TanClaimedChunk;

import java.util.*;
import java.util.function.Consumer;

public abstract class TerritoryData implements TanTerritory {

    protected String id;
    protected String name;
    protected String description;
    private String overlordID;
    private Double treasury;
    private final Long dateTimeCreated;
    private ICustomIcon customIcon;
    private RelationData relations;
    private Double baseTax;
    private double propertyRentTax;
    private double propertyBuyTax;
    private double propertyCreateTax;
    protected Integer color;
    protected Integer defaultRankID;
    protected Map<Integer, RankData> ranks;
    private HashMap<String, Integer> availableClaims;
    private Map<String, DiplomacyProposal> diplomacyProposals;
    private List<String> overlordsProposals;
    private ClaimedChunkSettings chunkSettings;
    private List<String> fortIds;
    private List<String> occupiedFortIds;
    protected TerritoryStats upgradesStatus;
    protected BannerBuilder bannerBuilder;

    protected TerritoryData(String id, String name, ITanPlayer owner) {
        this.id = id;
        this.name = name;
        this.description = Lang.DEFAULT_DESCRIPTION.getDefault();
        this.overlordID = null;
        this.dateTimeCreated = System.currentTimeMillis();

        this.customIcon = new PlayerHeadIcon(owner);
        bannerBuilder = new BannerBuilder();

        this.treasury = 0.0;
        this.baseTax = 1.0;
        this.propertyRentTax = 0.1;
        this.propertyBuyTax = 0.1;
        this.propertyCreateTax = 0.5;

        ranks = new HashMap<>();
        RankData defaultRank = registerNewRank("default");
        setDefaultRank(defaultRank);

        availableClaims = new HashMap<>();
        diplomacyProposals = new HashMap<>();
        overlordsProposals = new ArrayList<>();

        chunkSettings = new ClaimedChunkSettings(PermissionGiven.ofTerritory(this));

        color = StringUtil.randomColor();
    }

    public Optional<TownData> getCapitalTown() {
        if (this instanceof TownData townData) {
            return Optional.of(townData);
        }

        Set<String> visited = new HashSet<>();
        TerritoryData current = this;
        while (current != null && visited.add(current.getID())) {
            TerritoryData capital = current.getCapital();
            if (capital == null) {
                return Optional.empty();
            }
            if (capital instanceof TownData capitalTown) {
                return Optional.of(capitalTown);
            }
            current = capital;
        }
        return Optional.empty();
    }

    @Override
    public TanPlayer getOwner() {
        return getLeaderData();
    }

    @Override
    public Color getColor() {
        return Color.fromRGB(getChunkColorCode());
    }

    @Override
    public void setColor(Color color) {
        setChunkColor(color.asRGB());
    }

    @Override
    public Collection<TanClaimedChunk> getClaimedChunks() {
        return List.copyOf(NewClaimedChunkStorage.getInstance().getAllChunkFrom(this));
    }

    @Override
    public boolean canPlayerDoAction(TanPlayer player, TerritoryPermission permission) {
        return doesPlayerHavePermission(
                PlayerDataStorage.getInstance().get(player.getID()),
                RolePermission.valueOf(permission.name())
        );
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void rename(Player player, int cost, String newName) {
        removeFromBalance(cost);
        if (this instanceof TownData) {
            FileUtil.addLineToHistory(Lang.HISTORY_TOWN_NAME_CHANGED.get(player.getName(), name, newName));
        } else if (this instanceof NationData) {
            FileUtil.addLineToHistory(Lang.HISTORY_NATION_NAME_CHANGED.get(player.getName(), name, newName));
        } else {
            FileUtil.addLineToHistory(Lang.HISTORY_REGION_NAME_CHANGED.get(player.getName(), name, newName));
        }
        setName(newName);
    }


    @Override
    public void setName(String newName) {
        this.name = newName;
    }

    public abstract int getHierarchyRank();

    public TextComponent getCustomColoredName() {
        TextComponent coloredName = new TextComponent(getName());
        coloredName.setColor(getChunkColor());
        return coloredName;
    }

    public abstract UUID getLeaderID();

    public abstract ITanPlayer getLeaderData();

    public abstract void setLeaderID(UUID leaderID);

    public boolean isLeader(TanPlayer tanPlayer) {
        return isLeader(tanPlayer.getID());
    }

    public abstract boolean isLeader(UUID playerID);

    public boolean isLeader(Player player) {
        return isLeader(player.getUniqueId());
    }

    public String getDescription() {
        if (description == null) description = Lang.DEFAULT_DESCRIPTION.getDefault();
        return description;
    }

    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    public ItemStack getIcon() {
        if (this.customIcon == null) {
            if (haveNoLeader()) {
                customIcon = new CustomIcon(new ItemStack(Material.BARRIER));
            } else {
                customIcon = new PlayerHeadIcon(getLeaderID());
            }
        }
        return customIcon.getIcon();
    }

    public void setIcon(ICustomIcon icon) {
        this.customIcon = icon;
    }

    public abstract Collection<UUID> getPlayerIDList();

    public boolean isPlayerIn(TanPlayer tanPlayer) {
        return isPlayerIn(tanPlayer.getID());
    }

    @Override
    public boolean isPlayerIn(Player player) {
        return isPlayerIn(player.getUniqueId());
    }

    @Override
    public boolean checkPlayerPermission(TanPlayer player, TerritoryPermission rolePermission) {
        return doesPlayerHavePermission(player, RolePermission.valueOf(rolePermission.name()));
    }

    public boolean isPlayerIn(UUID playerID) {
        return getPlayerIDList().contains(playerID);
    }

    public Collection<UUID> getOrderedPlayerIDList() {
        List<UUID> sortedList = new ArrayList<>();
        List<ITanPlayer> playersSorted = getITanPlayerList().stream().sorted(Comparator.comparingInt(tanPlayer -> -this.getRank(tanPlayer.getRankID(this)).getLevel())).toList();

        for (ITanPlayer tanPlayer : playersSorted) {
            sortedList.add(tanPlayer.getID());
        }
        return sortedList;
    }

    public abstract Collection<ITanPlayer> getITanPlayerList();

    public ClaimedChunkSettings getChunkSettings() {
        if (chunkSettings == null)
            chunkSettings = new ClaimedChunkSettings(PermissionGiven.TOWN);
        return chunkSettings;
    }

    public RelationData getRelations() {
        if (relations == null) relations = new RelationData();
        return relations;
    }

    public void setRelation(TerritoryData otherTerritory, TownRelation newRelation) {

        TownRelation oldRelation = getRelationWith(otherTerritory);

        if(oldRelation == newRelation) {
            return;
        }

        EventManager.getInstance().callEvent(new DiplomacyProposalAcceptedInternalEvent(otherTerritory, this, oldRelation, newRelation));

        this.getRelations().setRelation(newRelation, otherTerritory);
        otherTerritory.getRelations().setRelation(newRelation, this);

        TeamUtils.updateAllScoreboardColor();
    }


    private Map<String, DiplomacyProposal> getDiplomacyProposals() {
        if (diplomacyProposals == null) diplomacyProposals = new HashMap<>();
        return diplomacyProposals;
    }

    public void removeDiplomaticProposal(TerritoryData proposingTerritory) {
        removeDiplomaticProposal(proposingTerritory.getID());
    }

    public void removeDiplomaticProposal(String proposingTerritoryID) {
        getDiplomacyProposals().remove(proposingTerritoryID);
    }

    private void addDiplomaticProposal(TerritoryData proposingTerritory, TownRelation wantedRelation) {
        EventManager.getInstance().callEvent(new DiplomacyProposalInternalEvent(this, proposingTerritory, wantedRelation));
        getDiplomacyProposals().put(proposingTerritory.getID(), new DiplomacyProposal(proposingTerritory.getID(), getID(), wantedRelation));
    }

    public void receiveDiplomaticProposal(TerritoryData proposingTerritory, TownRelation wantedRelation) {
        removeDiplomaticProposal(proposingTerritory);
        addDiplomaticProposal(proposingTerritory, wantedRelation);
    }

    public Collection<DiplomacyProposal> getAllDiplomacyProposal() {
        return getDiplomacyProposals().values();
    }

    /**
     * Get the worst relation a territory may have with a all the territory a player is part of (town, region...)
     *
     * @param player The player to check
     * @return The worst relation
     */
    public TownRelation getWorstRelationWith(ITanPlayer player) {
        TownRelation worstRelation = null;
        for (TerritoryData territoryData : player.getAllTerritoriesPlayerIsIn()) {
            TownRelation actualRelation = getRelationWith(territoryData);
            if (worstRelation == null || worstRelation.isSuperiorTo(actualRelation)) {
                worstRelation = actualRelation;
            }
        }
        if (worstRelation == null) {
            return TownRelation.NEUTRAL;
        }
        return worstRelation;
    }

    public TownRelation getRelationWith(TerritoryData territoryData) {
        return getRelationWith(territoryData.getID());
    }

    public TownRelation getRelationWith(String territoryID) {
        if (getID().equals(territoryID)) return TownRelation.SELF;

        Optional<TerritoryData> overlord = getOverlord();
        if (overlord.isPresent() && overlord.get().getID().equals(territoryID)) return TownRelation.OVERLORD;

        if (getVassalsID().contains(territoryID)) return TownRelation.VASSAL;

        return getRelations().getRelationWith(territoryID);
    }

    public long getCreationDate() {
        return dateTimeCreated;
    }

    public abstract void broadCastMessage(FilledLang message);

    public abstract void broadcastMessageWithSound(FilledLang message, SoundEnum soundEnum, boolean addPrefix);

    public abstract void broadcastMessageWithSound(FilledLang message, SoundEnum soundEnum);

    public abstract boolean haveNoLeader();

    public abstract IconBuilder getIconWithInformations(LangType langType);

    public IconBuilder getIconWithInformationAndRelation(TerritoryData territoryData, LangType langType) {
        IconBuilder icon = getIconWithInformations(langType);

        if(territoryData == null){
            return icon;
        }

        TownRelation relation = getRelationWith(territoryData);
        return icon.addDescription(Lang.GUI_TOWN_INFO_TOWN_RELATION.get(relation.getColoredName(langType)));
    }

    /**
     * @return all attacks currently ongoing where this territory takes part in.
     */
    public Collection<CurrentAttack> getCurrentAttacks() {
        Collection<CurrentAttack> res = new ArrayList<>();
        for (CurrentAttack currentAttack : CurrentAttacksStorage.getAll()) {
            if (currentAttack.getAttackData().getWar().getTerritoryRole(this) != WarRole.NEUTRAL) {
                res.add(currentAttack);
            }
        }
        return res;
    }

    public double getBalance() {
        if (treasury == null) treasury = 0.;
        return treasury;
    }

    public void addToBalance(double balance) {
        this.treasury += balance;
    }

    public void removeFromBalance(double balance) {
        this.treasury -= balance;
    }


    public void setOverlord(TerritoryData overlord) {
        getOverlordsProposals().remove(overlord.getID());
        if (overlord instanceof NationData) {
            broadcastMessageWithSound(Lang.NATION_ACCEPTED_VASSALISATION_PROPOSAL_ALL.get(this.getColoredName(), overlord.getColoredName()), SoundEnum.GOOD);
        } else {
            broadcastMessageWithSound(Lang.ACCEPTED_VASSALISATION_PROPOSAL_ALL.get(this.getColoredName(), overlord.getColoredName()), SoundEnum.GOOD);
        }

        this.overlordID = overlord.getID();
        overlord.addVassal(this);
    }

    public Optional<TerritoryData> getOverlord() {
        if (overlordID == null) {
            return Optional.empty();
        }
        TerritoryData overlord = TerritoryUtil.getTerritory(overlordID);
        if (overlord == null) {
            overlordID = null;
            return Optional.empty();
        }
        return Optional.of(overlord);

    }

    /**
     * @return All potential overlords of this territory (Nation and region)
     */
    protected abstract Collection<TerritoryData> getOverlords();

    public void removeOverlord() {
        getOverlord().ifPresent(overlord -> {
            overlord.removeVassal(this);
            removeOverlordPrivate();
            this.overlordID = null;
        });
    }

    public abstract void removeOverlordPrivate();


    public void addVassal(TerritoryData vassal) {
        EventManager.getInstance().callEvent(new TerritoryVassalAcceptedInternalEvent(vassal, this));
        addVassalPrivate(vassal);
    }

    protected abstract void addVassalPrivate(TerritoryData vassal);

    protected abstract void removeVassal(TerritoryData vassalID);

    public boolean isCapital() {
        Optional<TerritoryData> capital = getOverlord();
        return capital.map(overlord -> {
            TerritoryData overlordCapital = overlord.getCapital();
            return overlordCapital != null && Objects.equals(overlordCapital.getID(), getID());
        }).orElse(false);
    }

    public abstract TerritoryData getCapital();


    public int getChunkColorCode() {
        if (color == null) color = StringUtil.randomColor();
        return color;
    }

    @Override
    public String getChunkColorInHex() {
        return String.format("#%06X", getChunkColorCode());
    }

    public ChatColor getChunkColor() {
        return ChatColor.of(getChunkColorInHex());
    }

    public void setChunkColor(int color) {
        this.color = color;
        applyToAllOnlinePlayer(PrefixUtil::updatePrefix);
    }

    public boolean haveOverlord() {
        return getOverlord().isPresent();
    }


    public Map<String, Integer> getAvailableEnemyClaims() {
        if (availableClaims == null) availableClaims = new HashMap<>();
        return availableClaims;
    }

    public void addAvailableClaims(String territoryID, int amount) {
        getAvailableEnemyClaims().merge(territoryID, amount, Integer::sum);
    }

    public void consumeEnemyClaim(String territoryID) {
        getAvailableEnemyClaims().merge(territoryID, -1, Integer::sum);
        if (getAvailableEnemyClaims().get(territoryID) <= 0) getAvailableEnemyClaims().remove(territoryID);
    }


    /**
     * Claim the chunk for the territory. The chunk used will be the one where the player stands
     *
     * @param player The player wishing to claim a chunk
     * @return True if the chunk has been claimed successfully, false otherwise
     */
    public boolean claimChunk(Player player) {
        return claimChunk(player, player.getLocation().getChunk());
    }

    /**
     * Claim the chunk for the territory. The chunk used will be the one where the player stands
     *
     * @param player The player wishing to claim a chunk
     * @param chunk  The chunk to claim
     * @return True if the chunk has been claimed successfully, false otherwise
     */
    public boolean claimChunk(Player player, Chunk chunk) {
        return claimChunk(player, chunk, Constants.allowNonAdjacentChunksFor(this));
    }

    /**
     * Claim the chunk for the territory
     *
     * @param player         The player wishing to claim a chunk
     * @param chunk          The chunk to claim
     * @param ignoreAdjacent Defines whether the chunk to claim should respect adjacent claiming
     * @return True if the chunk has been claimed successfully, false otherwise
     */
    public boolean claimChunk(Player player, Chunk chunk, boolean ignoreAdjacent) {
        if (!canClaimChunk(player, chunk, ignoreAdjacent)) {
            return false;
        }

        abstractClaimChunk(player, chunk, ignoreAdjacent);

        ChunkCap chunkCap = getNewLevel().getStat(ChunkCap.class);

        FilledLang message;
        if (chunkCap.isUnlimited()) {
            message = Lang.CHUNK_CLAIMED_SUCCESS_UNLIMITED.get(getColoredName());
        } else {
            String currentAmountOfChunks = Integer.toString(getNumberOfClaimedChunk());
            String maxAmountOfChunks = Integer.toString(chunkCap.getMaxAmount());
            message = Lang.CHUNK_CLAIMED_SUCCESS_LIMITED.get(getColoredName(), currentAmountOfChunks, maxAmountOfChunks);
        }


        TanChatUtils.message(player, message);

        return true;
    }

    /**
     * Claim the chunk for the territory
     *
     * @param player         The player wishing to claim a chunk
     * @param chunk          The chunk to claim
     * @param ignoreAdjacent Defines if the chunk to claim should respect adjacent claiming
     */
    protected abstract void abstractClaimChunk(Player player, Chunk chunk, boolean ignoreAdjacent);

    public boolean canClaimChunk(Player player, Chunk chunk, boolean ignoreAdjacent) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

        if (ClaimBlacklistStorage.cannotBeClaimed(chunk)) {
            TanChatUtils.message(player, Lang.CHUNK_IS_BLACKLISTED.get(tanPlayer.getLang()));
            return false;
        }

        if (!doesPlayerHavePermission(tanPlayer, RolePermission.CLAIM_CHUNK)) {
            TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer.getLang()));
            return false;
        }

        TerritoryStats territoryStats = getNewLevel();
        int nbOfClaimedChunks = getNumberOfClaimedChunk();

        if (!territoryStats.getStat(BiomeStat.class).canClaimBiome(chunk)) {
            TanChatUtils.message(player, Lang.CHUNK_BIOME_NOT_ALLOWED.get(tanPlayer.getLang()));
            return false;
        }

        if (!territoryStats.getStat(ChunkCap.class).canDoAction(nbOfClaimedChunks)) {
            TanChatUtils.message(player, Lang.MAX_CHUNK_LIMIT_REACHED.get(tanPlayer.getLang()));
            return false;
        }

        int cost = getClaimCost();
        if (getBalance() < cost) {
            TanChatUtils.message(player, Lang.TERRITORY_NOT_ENOUGH_MONEY.get(tanPlayer.getLang(), getColoredName(), Double.toString(cost - getBalance())));
            return false;
        }

        ClaimedChunk chunkData = NewClaimedChunkStorage.getInstance().get(chunk);
        if (!chunkData.canTerritoryClaim(player, this,  tanPlayer.getLang())) {
            return false;
        }

        if (ignoreAdjacent) {
            return true;
        }

        // If first claim of the territory and in a buffer zone of another territory, deny the claim
        if (getNumberOfClaimedChunk() == 0) {
            int bufferZone = Constants.territoryClaimBufferZone();
            if (ChunkUtil.isInBufferZone(chunkData, this, bufferZone)) {
                TanChatUtils.message(player, Lang.CHUNK_IN_BUFFER_ZONE.get(tanPlayer.getLang(), Integer.toString(bufferZone)));
                return false;
            }
            return true;
        }


        if (!NewClaimedChunkStorage.getInstance().isOneAdjacentChunkClaimedBySameTerritory(chunk, getID())) {
            TanChatUtils.message(player, Lang.CHUNK_NOT_ADJACENT.get(tanPlayer.getLang()));
            return false;
        }
        return true;
    }

    public int getClaimCost() {
        return getNewLevel().getStat(ChunkCost.class).getCost();
    }


    public synchronized void delete() {
        NewClaimedChunkStorage.getInstance().unclaimAllChunksFromTerritory(this); //Unclaim all chunk from town

        applyToAllOnlinePlayer(Player::closeInventory);

        for (TerritoryData territory : getVassalsInternal()) {
            territory.removeOverlord();
        }

        for (Fort occupiedFort : getOccupiedForts()) {
            occupiedFort.liberate();
        }

        for (Fort ownedFort : getOwnedForts()) {
            FortStorage.getInstance().delete(ownedFort);
        }

        getRelations().cleanAll(this);   //Cancel all Relation between the deleted territory and other territories
    }

    /**
     * Check if the territory can claim over another one.
     * If the territory can claim, the number of chunks it can claim will be directly decreased.
     * @param chunk The chunk wanted to overclaim
     * @return True if this territory can claim, false otherwise.
     */
    public boolean canConquerChunk(TerritoryChunk chunk) {
        if (getAvailableEnemyClaims().containsKey(chunk.getOwnerID())) {
            consumeEnemyClaim(chunk.getOwnerID());
            return true;
        }
        return false;
    }

    public void addDonation(Player player, double amount) {
        LangType langType = PlayerDataStorage.getInstance().get(player).getLang();
        double playerBalance = EconomyUtil.getBalance(player);

        if (playerBalance < amount) {
            TanChatUtils.message(player, Lang.PLAYER_NOT_ENOUGH_MONEY.get(langType));
            return;
        }
        if (amount <= 0) {
            TanChatUtils.message(player, Lang.PAY_MINIMUM_REQUIRED.get(langType));
            return;
        }


        EconomyUtil.removeFromBalance(player, amount);
        addToBalance(amount);

        TransactionManager.getInstance().register(new DonationTransaction(this, player, amount));
        TanChatUtils.message(player, Lang.PLAYER_SEND_MONEY_SUCCESS.get(langType, Double.toString(amount), this.getColoredName()), SoundEnum.MINOR_GOOD);
    }

    public abstract void openMainMenu(Player player, ITanPlayer playerData);

    public abstract boolean canHaveVassals();

    public abstract boolean canHaveOverlord();

    public abstract Set<String> getVassalsID();

    /**
     * @return The list of vassals as TerritoryData
     */
    public List<TerritoryData> getVassalsInternal() {
        List<TerritoryData> res = new ArrayList<>();
        for (String vassalID : getVassalsID()) {
            TerritoryData vassal = TerritoryUtil.getTerritory(vassalID);
            if (vassal != null) res.add(vassal);
        }
        return res;
    }

    @Override
    public Collection<TanPlayer> getMembers() {
        return List.copyOf(getITanPlayerList());
    }

    @Override
    public Collection<TanTerritory> getVassals() {
        return List.copyOf(getVassalsInternal());
    }

    public int getVassalCount() {
        return getVassalsID().size();
    }


    public abstract Collection<TerritoryData> getPotentialVassals();

    public List<String> getOverlordsProposals() {
        if (overlordsProposals == null) overlordsProposals = new ArrayList<>();
        return overlordsProposals;
    }

    public void addVassalisationProposal(TerritoryData proposal) {
        getOverlordsProposals().add(proposal.getID());
        if (proposal instanceof NationData) {
            broadcastMessageWithSound(Lang.NATION_DIPLOMATIC_INVITATION_RECEIVED_1.get(proposal.getColoredName(), this.getColoredName()), SoundEnum.MINOR_GOOD);
        } else {
            broadcastMessageWithSound(Lang.REGION_DIPLOMATIC_INVITATION_RECEIVED_1.get(proposal.getColoredName(), this.getColoredName()), SoundEnum.MINOR_GOOD);
        }
        EventManager.getInstance().callEvent(new TerritoryVassalProposalInternalEvent(proposal, this));
    }

    public void removeVassalisationProposal(TerritoryData proposal) {
        getOverlordsProposals().remove(proposal.getID());
    }

    public boolean containsVassalisationProposal(TerritoryData proposal) {
        return getOverlordsProposals().contains(proposal.getID());
    }

    public int getNumberOfVassalisationProposals() {
        return getOverlordsProposals().size();
    }

    protected Map<Integer, RankData> getRanks() {
        if (ranks == null) {
            ranks = new HashMap<>();
        }
        return ranks;
    }

    public Collection<RankData> getAllRanks() {
        return getRanks().values();
    }

    public Collection<RankData> getAllRanksSorted() {
        return getRanks().values().stream().sorted(Comparator.comparingInt(p -> -p.getLevel())).toList();
    }

    public RankData getRank(int rankID) {
        return getRanks().get(rankID);
    }

    //TODO : enable TanPlayer to be used directly
    public RankData getRank(TanPlayer tanPlayer){
        return getRank(PlayerDataStorage.getInstance().get(tanPlayer.getID()));
    }

    public abstract RankData getRank(ITanPlayer tanPlayer);

    public RankData getRank(Player player) {
        return getRank(PlayerDataStorage.getInstance().get(player));
    }

    public int getNumberOfRank() {
        return getRanks().size();
    }

    public boolean isRankNameUsed(String message) {
        for (RankData rank : getAllRanks()) {
            if (rank.getName().equals(message)) {
                return true;
            }
        }
        return false;
    }

    public RankData registerNewRank(String rankName) {
        int nextRankId = 0;
        for (RankData rank : getAllRanks()) {
            if (rank.getID() >= nextRankId) nextRankId = rank.getID() + 1;
        }

        RankData newRank = new RankData(nextRankId, rankName);
        getRanks().put(nextRankId, newRank);
        return newRank;
    }

    public void removeRank(int key) {
        getRanks().remove(key);
    }

    public int getDefaultRankID() {
        if (defaultRankID == null) {
            defaultRankID = getAllRanks().iterator().next().getID(); //If no default rank is set, we take the first one
        }
        return defaultRankID;
    }

    public void setDefaultRank(RankData rank) {
        setDefaultRank(rank.getID());
    }

    public void setDefaultRank(int rankID) {
        this.defaultRankID = rankID;
    }


    public boolean doesPlayerHavePermission(Player player, RolePermission townRolePermission) {
        return doesPlayerHavePermission(PlayerDataStorage.getInstance().get(player), townRolePermission);
    }

    public boolean doesPlayerHavePermission(TanPlayer tanPlayer, RolePermission townRolePermission) {

        if (!this.isPlayerIn(tanPlayer)) {
            return false;
        }

        if (isLeader(tanPlayer)) return true;

        return getRank(tanPlayer).hasPermission(townRolePermission);
    }

    public void setPlayerRank(ITanPlayer playerStat, RankData rankData) {
        getRank(playerStat).removePlayer(playerStat);
        rankData.addPlayer(playerStat);
        specificSetPlayerRank(playerStat, rankData.getID());
    }

    protected abstract void specificSetPlayerRank(ITanPlayer playerStat, int rankID);

    public Budget getBudget() {
        Budget budget = new Budget();
        addCommonTaxes(budget);
        addSpecificTaxes(budget);
        return budget;
    }

    private void addCommonTaxes(Budget budget) {
        budget.addProfitLine(new SalaryPaymentLine(this));
        budget.addProfitLine(new ChunkUpkeepLine(this));
    }

    protected abstract void addSpecificTaxes(Budget budget);

    public int getNumberOfClaimedChunk() {
        return NewClaimedChunkStorage.getInstance().getAllChunkFrom(this).size();
    }

    public double getTax() {
        if (baseTax == null) {
            baseTax = 0.0;
        }
        return baseTax;
    }

    public void setTax(double newTax) {
        baseTax = newTax;
    }

    public void addToTax(double i) {
        setTax(getTax() + i);
    }

    public void executeTasks() {
        collectTaxes();
        paySalaries();
        payChunkUpkeep();
    }

    private void paySalaries() {
        for (RankData rank : getAllRanks()) {
            int rankSalary = rank.getSalary();
            List<UUID> playerIdList = rank.getPlayersID();
            double costOfSalary = (double) playerIdList.size() * rankSalary;

            if (rankSalary == 0 || costOfSalary > getBalance()) {
                continue;
            }
            removeFromBalance(costOfSalary);
            for (UUID playerId : playerIdList) {
                ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(playerId);
                EconomyUtil.addFromBalance(tanPlayer, rankSalary);
                TransactionManager.getInstance().register(new SalaryTransaction(getID(), playerId.toString(), costOfSalary));
            }
        }
    }

    private void payChunkUpkeep() {
        double upkeepCost = getNewLevel().getStat(ChunkUpkeepCost.class).getCost();

        int numberClaimedChunk = getNumberOfClaimedChunk();
        double totalUpkeep = numberClaimedChunk * upkeepCost;
        if (totalUpkeep > getBalance()) {
            deletePortionOfChunk();
            TransactionManager.getInstance().register(new TerritoryChunkUpkeepTransaction(getID(), upkeepCost, numberClaimedChunk, false));
        } else {
            removeFromBalance(totalUpkeep);
            TransactionManager.getInstance().register(new TerritoryChunkUpkeepTransaction(getID(), upkeepCost, numberClaimedChunk, true));
        }

    }

    private void deletePortionOfChunk() {
        int minNbOfUnclaimedChunk = Constants.getMinimumNumberOfChunksUnclaimed();
        int nbOfUnclaimedChunk = 0;
        double percentageOfChunkToKeep = Constants.getPercentageOfChunksUnclaimed();


        List<ClaimedChunk> borderChunks = ChunkUtil.getBorderChunks(this);


        for (ClaimedChunk claimedChunk : borderChunks) {
            if (RandomUtil.getRandom().nextDouble() < percentageOfChunkToKeep) {
                NewClaimedChunkStorage.getInstance().unclaimChunkAndUpdate(claimedChunk);
                nbOfUnclaimedChunk++;
            }
        }
        if (nbOfUnclaimedChunk < minNbOfUnclaimedChunk) {
            for (ClaimedChunk claimedChunk : borderChunks) {
                NewClaimedChunkStorage.getInstance().unclaimChunkAndUpdate(claimedChunk);
                nbOfUnclaimedChunk++;
                if (nbOfUnclaimedChunk >= minNbOfUnclaimedChunk) break;
            }
        }
    }


    protected abstract void collectTaxes();

    public double getTaxOnRentingProperty() {
        if (propertyRentTax > 1) propertyRentTax = 1; //Convert to percentage
        return propertyRentTax;
    }

    public void setTaxOnRentingProperty(double amount) {
        propertyRentTax = amount;
    }

    public double getTaxOnBuyingProperty() {
        if (propertyBuyTax > 1) propertyBuyTax = 1; //Convert to percentage
        return propertyBuyTax;
    }

    public void setTaxOnBuyingProperty(double amount) {
        propertyBuyTax = amount;
    }

    public double getTaxOnCreatingProperty() {
        return propertyCreateTax;
    }

    public void setTaxOnCreatingProperty(double amount) {
        propertyCreateTax = amount;
    }

    /**
     * @return true if the territory is involved in at least one attack currently undergoing.
     */
    public boolean attackInProgress() {
        return !getCurrentAttacks().isEmpty();
    }

    /**
     * @return true if the territory is involved in at least one war.
     */
    public boolean isAtWar(){
        return !WarStorage.getInstance().getWarsOfTerritory(this).isEmpty();
    }

    protected RankData getDefaultRank() {
        return getRank(getDefaultRankID());
    }

    protected void registerPlayer(ITanPlayer tanPlayer) {
        getDefaultRank().addPlayer(tanPlayer);
        tanPlayer.setRankID(this, getDefaultRankID());
    }

    protected void unregisterPlayer(ITanPlayer tanPlayer) {
        getRank(tanPlayer).removePlayer(tanPlayer);
        tanPlayer.setRankID(this, null);
    }


    public String getColoredName() {
        if (Constants.displayTerritoryColor()) {
            return getChunkColor() + getName();
        } else {
            return getBaseColoredName();
        }
    }

    /**
     * @return the base-colored name of the territory, without using the territory color settings
     */
    protected abstract String getBaseColoredName();

    public String getLeaderName() {
        if (this.haveNoLeader()) return Lang.NO_LEADER.getDefault();
        return getLeaderData().getNameStored();
    }

    public void registerFort(Vector3D location) {
        Fort fort = FortStorage.getInstance().register(location, this);

        Vector2D flagPosition = fort.getPosition();
        flagPosition.getWorld().getChunkAt(flagPosition.getX(), flagPosition.getZ());

        addOwnedFort(fort);
    }

    public List<String> getOwnedFortIDs() {
        if (fortIds == null) fortIds = new ArrayList<>();
        return fortIds;
    }

    public List<String> getOccupiedFortIds() {
        if (occupiedFortIds == null) occupiedFortIds = new ArrayList<>();
        return occupiedFortIds;
    }

    public void removeOccupiedFort(Fort fort) {
        removeOccupiedFortID(fort.getID());
    }

    public void removeOccupiedFortID(String fortID) {
        getOccupiedFortIds().remove(fortID);
    }

    public void addOccupiedFort(Fort fort) {
        addOccupiedFortID(fort.getID());
    }

    public void addOccupiedFortID(String fortID) {
        getOccupiedFortIds().add(fortID);
    }

    /**
     * @return All forts owned by this territory, should they be occupied or not.
     */
    public List<Fort> getOwnedForts() {
        return FortStorage.getInstance().getOwnedFort(this);
    }

    /**
     * @return All foreign forts occupied by this territory. Not including forts owned by the territory
     */
    public List<Fort> getOccupiedForts() {
        return FortStorage.getInstance().getOccupiedFort(this);
    }

    /**
     * @return All forts occupied by this territory, including forts owned by this territory
     * and excluding owned forts occupied by other territories
     */
    public List<Fort> getAllControlledFort() {
        return FortStorage.getInstance().getAllControlledFort(this);
    }

    public void removeFort(String fortID) {
        getOwnedFortIDs().remove(fortID);
    }

    public Collection<Building> getBuildings() {
        List<Building> buildings = new ArrayList<>(getOwnedForts());

        if (this instanceof TownData townData) {
            buildings.addAll(townData.getPropertiesInternal());
        }
        buildings.removeAll(Collections.singleton(null));
        return buildings;
    }

    public void addOwnedFort(Fort fortToCapture) {
        if (fortToCapture == null) {
            return;
        }
        getOwnedFortIDs().add(fortToCapture.getID());
    }

    public void removeOwnedFort(Fort fortToCapture) {
        if (fortToCapture == null) {
            return;
        }
        getOwnedFortIDs().remove(fortToCapture.getID());
    }

    public void applyToAllOnlinePlayer(Consumer<Player> action) {
        for (Player player : getPlayers()) {
            action.accept(player);
        }
    }

    private List<Player> getPlayers() {
        List<Player> playerList = new ArrayList<>();
        for (UUID playerID : getPlayerIDList()) {
            Player player = Bukkit.getPlayer(playerID);
            if (player != null) {
                playerList.add(player);
            }
        }
        return playerList;
    }

    public TerritoryStats getNewLevel() {
        if (this.upgradesStatus == null) {
            // Migrate old data if exists
            if (this instanceof TownData) {
                this.upgradesStatus = new TerritoryStats(StatsType.TOWN);
            } else if (this instanceof NationData) {
                this.upgradesStatus = new TerritoryStats(StatsType.NATION);
            } else {
                this.upgradesStatus = new TerritoryStats(StatsType.REGION);
            }
        }
        return upgradesStatus;
    }

    public int getNumberOfOccupiedChunk() {
        int count = 0;
        for(TerritoryChunk territoryChunk : NewClaimedChunkStorage.getInstance().getAllChunkFrom(this)){
            if(territoryChunk.isOccupied()){
                count++;
            }
        }
        return count;
    }

    /**
     * Called after a chunk has been captured by a foreign town.
     * If surrender progress is superior to constants, territory will automatically surrender all involved wars
     */
    public void checkIfShouldSurrender(){
        int totalChunk = getNumberOfClaimedChunk();
        int occupiedChunk = getNumberOfOccupiedChunk();

        int ratio;
        if(totalChunk != 0){
            ratio = occupiedChunk * 100 / totalChunk;
        }
        else {
            ratio = 0;
        }

        Optional<TownData> capitalTownOpt = getCapitalTown();
        boolean capitalExist = capitalTownOpt.isPresent() && capitalTownOpt.get().getCapitalLocation().isPresent();
        boolean capitalCaptured = capitalTownOpt.isPresent() && capitalTownOpt.get().isTownCapitalOccupied();

        if(capitalExist && capitalCaptured){
            ratio += Constants.getCaptureCapitalBonusPercentage();
        }

        if(ratio > Constants.getCapturePercentageToSurrender()){
            for(War war : WarStorage.getInstance().getWarsOfTerritory(this)){
                if(war.isMainAttacker(this) || war.isMainDefender(this)){
                    war.territorySurrender(this);
                }
            }
        }

    }

    public void setBanner(Material material, List<Pattern> patterns) {
        this.bannerBuilder = new BannerBuilder(material, patterns);
    }

    public BannerBuilder getBanner() {
        if(bannerBuilder == null) {
            bannerBuilder = new BannerBuilder();
        }
        return bannerBuilder;
    }
}
