package org.leralix.tan.data.territory;


import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.RandomUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.Building;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.chunk.IClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunk;
import org.leralix.tan.data.chunk.TerritoryChunkData;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.cosmetic.BannerBuilder;
import org.leralix.tan.data.territory.cosmetic.CustomIcon;
import org.leralix.tan.data.territory.cosmetic.ICustomIcon;
import org.leralix.tan.data.territory.cosmetic.PlayerHeadIcon;
import org.leralix.tan.data.territory.economy.Budget;
import org.leralix.tan.data.territory.economy.ChunkUpkeepLine;
import org.leralix.tan.data.territory.economy.SalaryPaymentLine;
import org.leralix.tan.data.territory.permission.ClaimedChunkSettings;
import org.leralix.tan.data.territory.permission.IClaimedChunkSettings;
import org.leralix.tan.data.territory.permission.PermissionGiven;
import org.leralix.tan.data.territory.rank.RankData;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.data.territory.relation.DiplomacyProposal;
import org.leralix.tan.data.territory.relation.RelationData;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.data.territory.teleportation.TeleportationData;
import org.leralix.tan.data.upgrade.TerritoryStats;
import org.leralix.tan.data.upgrade.rewards.StatsType;
import org.leralix.tan.data.upgrade.rewards.list.BiomeStat;
import org.leralix.tan.data.upgrade.rewards.numeric.ChunkCap;
import org.leralix.tan.data.upgrade.rewards.numeric.ChunkCost;
import org.leralix.tan.data.upgrade.rewards.numeric.ChunkUpkeepCost;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.events.EventManager;
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
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.graphic.PrefixUtil;
import org.leralix.tan.utils.territory.ChunkUtil;
import org.leralix.tan.utils.text.NumberUtil;
import org.leralix.tan.utils.text.StringUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.war.War;
import org.leralix.tan.war.attack.CurrentAttack;
import org.leralix.tan.war.info.WarRole;
import org.tan.api.enums.EDiplomacyState;
import org.tan.api.enums.TerritoryPermission;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.buildings.TanProperty;
import org.tan.api.interfaces.chunk.TanClaimedChunk;
import org.tan.api.interfaces.territory.TanTerritory;

import java.util.*;
import java.util.function.Consumer;

public abstract class TerritoryData implements TanTerritory, Territory {

    protected String id;
    protected String name;
    protected String description;
    protected String overlordID;
    private Double treasury;
    private final Long dateTimeCreated;
    private ICustomIcon customIcon;
    private RelationData relations;
    protected Double baseTax;
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

    protected TeleportationData teleportationPosition;

    protected TerritoryData(String id, String name, ITanPlayer owner) {
        this.id = id;
        this.name = name;
        this.description = Lang.DEFAULT_DESCRIPTION.getDefault();
        this.overlordID = null;
        this.dateTimeCreated = System.currentTimeMillis();

        this.customIcon = new PlayerHeadIcon(owner);
        this.relations = new RelationData();
        this.bannerBuilder = new BannerBuilder();

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
        this.teleportationPosition = new TeleportationData();

        color = StringUtil.randomColor();
    }

    @Override
    public Optional<Town> getCapitalTown() {
        if (this instanceof Town townData) {
            return Optional.of(townData);
        }

        Set<String> visited = new HashSet<>();
        Territory current = this;
        while (current != null && visited.add(current.getID())) {
            Territory capital = current.getCapital();
            if (capital == null) {
                return Optional.empty();
            }
            if (capital instanceof Town capitalTown) {
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
        return List.copyOf(TownsAndNations.getPlugin().getClaimStorage().getAllChunkFrom(this));
    }

    @Override
    public boolean canPlayerDoAction(TanPlayer player, TerritoryPermission permission) {
        return doesPlayerHavePermission(
                TownsAndNations.getPlugin().getPlayerDataStorage().get(player.getID()),
                RolePermission.valueOf(permission.name())
        );
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public void setName(String newName) {
        this.name = newName;
    }

    @Override
    public TextComponent getCustomColoredName() {
        TextComponent coloredName = new TextComponent(getName());
        coloredName.setColor(getChunkColor());
        return coloredName;
    }

    @Override
    public String getDescription() {
        if (description == null) description = Lang.DEFAULT_DESCRIPTION.getDefault();
        return description;
    }

    @Override
    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    @Override
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

    @Override
    public void setIcon(ICustomIcon icon) {
        this.customIcon = icon;
    }


    @Override
    public boolean checkPlayerPermission(TanPlayer player, TerritoryPermission rolePermission) {
        return doesPlayerHavePermission(TownsAndNations.getPlugin().getPlayerDataStorage().get(player.getID()), RolePermission.valueOf(rolePermission.name()));
    }

    @Override
    public boolean isPlayerIn(UUID playerID) {
        return getPlayerIDList().contains(playerID);
    }

    @Override
    public Collection<UUID> getOrderedPlayerIDList() {
        List<UUID> sortedList = new ArrayList<>();
        List<ITanPlayer> playersSorted = getITanPlayerList().stream().sorted(Comparator.comparingInt(tanPlayer -> -this.getRank(tanPlayer.getRankID(this)).getLevel())).toList();

        for (ITanPlayer tanPlayer : playersSorted) {
            sortedList.add(tanPlayer.getID());
        }
        return sortedList;
    }

    @Override
    public IClaimedChunkSettings getChunkSettings() {
        if (chunkSettings == null)
            chunkSettings = new ClaimedChunkSettings(PermissionGiven.TOWN);
        return chunkSettings;
    }

    public RelationData getRelations() {
        if (relations == null) relations = new RelationData();
        return relations;
    }

    @Override
    public void removeDiplomaticProposal(String proposingTerritoryID) {
        diplomacyProposals.remove(proposingTerritoryID);
    }

    @Override
    public void addDiplomaticProposal(Territory proposingTerritory, TownRelation wantedRelation) {
        // If another proposal was already present, remove it before adding the new one
        removeDiplomaticProposal(proposingTerritory);

        EventManager.getInstance().callEvent(new DiplomacyProposalInternalEvent(this, proposingTerritory, wantedRelation));
        diplomacyProposals.put(proposingTerritory.getID(), new DiplomacyProposal(proposingTerritory.getID(), getID(), wantedRelation));
    }

    @Override
    public Collection<DiplomacyProposal> getAllDiplomacyProposal() {
        return diplomacyProposals.values();
    }

    /**
     * Get the worst relation a territory may have with a all the territory a player is part of (town, region...)
     *
     * @param player The player to check
     * @return The worst relation
     */
    @Override
    public TownRelation getWorstRelationWith(ITanPlayer player) {
        TownRelation worstRelation = null;
        for (Territory territoryData : player.getAllTerritoriesPlayerIsIn()) {
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

    @Override
    public void setRelation(TownRelation relation, String territoryID) {
        getRelations().setRelation(relation, territoryID);
    }

    @Override
    public List<String> getTerritoriesIDWithRelation(TownRelation relation) {
        return new ArrayList<>(getRelations().getTerritoriesIDWithRelation(relation));
    }

    @Override
    public TownRelation getRelationWith(String territoryID) {
        if (getID().equals(territoryID)) return TownRelation.SELF;

        Optional<Territory> overlord = getOverlordInternal();
        if (overlord.isPresent() && overlord.get().getID().equals(territoryID)) return TownRelation.OVERLORD;

        if (getVassalsID().contains(territoryID)) return TownRelation.VASSAL;

        return getRelations().getRelationWith(territoryID);
    }

    @Override
    public List<Territory> getTerritoriesWithRelation(TownRelation townRelation) {
        return getRelations().getTerritoriesWithRelation(townRelation);
    }

    @Override
    public long getCreationDate() {
        return dateTimeCreated;
    }

    @Override
    public IconBuilder getIconWithInformationAndRelation(Territory territoryData, LangType langType) {
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
    @Override
    public Collection<CurrentAttack> getCurrentAttacks() {
        Collection<CurrentAttack> res = new ArrayList<>();
        for (CurrentAttack currentAttack : CurrentAttacksStorage.getAll()) {
            if (currentAttack.getAttackData().getWar().getTerritoryRole(this) != WarRole.NEUTRAL) {
                res.add(currentAttack);
            }
        }
        return res;
    }

    @Override
    public double getBalance() {
        if (treasury == null) treasury = 0.;
        return NumberUtil.roundWithDigits(treasury);
    }

    @Override
    public void addToBalance(double balance) {
        this.treasury += balance;
    }

    @Override
    public void removeFromBalance(double balance) {
        this.treasury -= balance;
    }

    /**
     * @return All potential overlords of this territory (Nation and region)
     */
    protected abstract Collection<Territory> getOverlords();

    @Override
    public void setOverlord(Territory overlord) {
        getOverlordsProposals().remove(overlord.getID());
        if (overlord instanceof Nation) {
            broadcastMessageWithSound(Lang.NATION_ACCEPTED_VASSALISATION_PROPOSAL_ALL.get(this.getColoredName(), overlord.getColoredName()), SoundEnum.GOOD);
        } else {
            broadcastMessageWithSound(Lang.ACCEPTED_VASSALISATION_PROPOSAL_ALL.get(this.getColoredName(), overlord.getColoredName()), SoundEnum.GOOD);
        }

        this.overlordID = overlord.getID();
        overlord.addVassal(this);
    }

    @Override
    public Optional<Territory> getOverlordInternal() {
        if (overlordID == null) {
            return Optional.empty();
        }
        Territory overlord = TerritoryUtil.getTerritory(overlordID);
        if (overlord == null) {
            overlordID = null;
            return Optional.empty();
        }
        return Optional.of(overlord);

    }

    @Override
    public Optional<TanTerritory> getOverlord(){
        var optOverlord = getOverlordInternal();
        if(optOverlord.isEmpty()){
            return Optional.empty();
        }
        else {
            return Optional.of(optOverlord.get());
        }
    }

    @Override
    public void removeOverlord() {
        getOverlordInternal().ifPresent(overlord -> {
            overlord.removeVassal(this);
            removeOverlordPrivate();
            this.overlordID = null;
        });
    }

    protected abstract void removeOverlordPrivate();

    @Override
    public void addVassal(Territory vassal) {
        EventManager.getInstance().callEvent(new TerritoryVassalAcceptedInternalEvent(vassal, this));

        RankData regionDefaultRank = getDefaultRank();
        for(ITanPlayer player : vassal.getITanPlayerList()){
            player.setRegionRankID(regionDefaultRank.getID());
            regionDefaultRank.addPlayer(player);
        }

        addVassalPrivate(vassal);
    }

    protected abstract void addVassalPrivate(Territory vassal);

    @Override
    public boolean isCapital() {
        Optional<Territory> capital = getOverlordInternal();
        return capital.map(overlord -> {
            Territory overlordCapital = overlord.getCapital();
            return overlordCapital != null && Objects.equals(overlordCapital.getID(), getID());
        }).orElse(false);
    }

    @Override
    public int getChunkColorCode() {
        if (color == null) color = StringUtil.randomColor();
        return color;
    }

    @Override
    public String getChunkColorInHex() {
        return String.format("#%06X", getChunkColorCode());
    }

    @Override
    public ChatColor getChunkColor() {
        return ChatColor.of(getChunkColorInHex());
    }

    @Override
    public void setChunkColor(int color) {
        this.color = color;
        applyToAllOnlinePlayer(PrefixUtil::updatePrefix);
    }

    @Override
    public boolean haveOverlord() {
        return getOverlordInternal().isPresent();
    }

    @Override
    public Map<String, Integer> getAvailableEnemyClaims() {
        if (availableClaims == null) availableClaims = new HashMap<>();
        return availableClaims;
    }

    @Override
    public void addAvailableClaims(String territoryID, int amount) {
        getAvailableEnemyClaims().merge(territoryID, amount, Integer::sum);
    }

    @Override
    public void consumeEnemyClaim(String territoryID) {
        getAvailableEnemyClaims().merge(territoryID, -1, Integer::sum);
        if (getAvailableEnemyClaims().get(territoryID) <= 0) getAvailableEnemyClaims().remove(territoryID);
    }

    @Override
    public boolean claimChunk(Player player, ITanPlayer tanPlayer, Chunk chunk){
        return claimChunk(player, tanPlayer, chunk, Constants.allowNonAdjacentChunksFor(this));
    }

    @Override
    public boolean claimChunk(Player player, ITanPlayer playerData, Chunk chunk, boolean ignoreAdjacent) {
        if (!canClaimChunk(player, playerData, chunk, ignoreAdjacent)) {
            return false;
        }

        abstractClaimChunk(chunk, ignoreAdjacent);

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
     * @param chunk          The chunk to claim
     * @param ignoreAdjacent Defines if the chunk to claim should respect adjacent claiming
     */
    protected abstract void abstractClaimChunk(Chunk chunk, boolean ignoreAdjacent);

    protected boolean canClaimChunk(Player player, ITanPlayer tanPlayer, Chunk chunk, boolean ignoreAdjacent) {

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

        IClaimedChunk chunkData = TownsAndNations.getPlugin().getClaimStorage().get(chunk);
        if (!chunkData.canTerritoryClaim(player, this,  tanPlayer.getLang())) {
            return false;
        }

        if (ignoreAdjacent) {
            return true;
        }
        return isPositionClaimable(player, chunk, chunkData, tanPlayer.getLang());
    }

    /**
     * Check if the chunk can be claimed
     * @return true if the position can be claimed, false otherwise
     */
    protected boolean isPositionClaimable(Player player, Chunk chunk, IClaimedChunk chunkData, LangType langType){

        for(IClaimedChunk claimedChunk : TownsAndNations.getPlugin().getClaimStorage().getFourAjacentChunks(chunkData)) {
            if(claimedChunk instanceof TerritoryChunk territoryChunk){

                String ownerID = territoryChunk.getOwnerID();

                if(ownerID.equals(getID()) || getVassalsID().contains(ownerID)) {
                    return true;
                }
            }
        }

        // The chunk must be adjacent to at least one chunk from the territory of one of its vassals.
        if (!TownsAndNations.getPlugin().getClaimStorage().isOneAdjacentChunkClaimedBySameTerritory(chunk, getID())) {
            TanChatUtils.message(player, Lang.CHUNK_NOT_ADJACENT.get(langType));
            return false;
        }
        return true;
    }

    @Override
    public int getClaimCost() {
        return getNewLevel().getStat(ChunkCost.class).getCost();
    }

    @Override
    public synchronized void delete() {
        TownsAndNations.getPlugin().getClaimStorage().unclaimAllChunksFromTerritory(this); //Unclaim all chunk from town

        applyToAllOnlinePlayer(Player::closeInventory);

        for (Territory territory : getVassalsInternal()) {
            territory.removeOverlord();
        }

        for (Fort occupiedFort : TownsAndNations.getPlugin().getFortStorage().getOccupiedFort(this)) {
            occupiedFort.liberate();
        }

        for (Fort ownedFort : TownsAndNations.getPlugin().getFortStorage().getOwnedFort(this)) {
            TownsAndNations.getPlugin().getFortStorage().delete(ownedFort);
        }

        getRelations().cleanAll(this);   //Cancel all Relation between the deleted territory and other territories
    }

    @Override
    public boolean canConquerChunk(TerritoryChunkData chunk) {
        if (getAvailableEnemyClaims().containsKey(chunk.getOwnerID())) {
            consumeEnemyClaim(chunk.getOwnerID());
            return true;
        }
        return false;
    }

    @Override
    public void addDonation(Player player, double amount) {
        LangType langType = TownsAndNations.getPlugin().getPlayerDataStorage().get(player).getLang();
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

    /**
     * @return The list of vassals as {@link TerritoryData}
     */
    @Override
    public List<Territory> getVassalsInternal() {
        List<Territory> res = new ArrayList<>();
        for (String vassalID : getVassalsID()) {
            Territory vassal = TerritoryUtil.getTerritory(vassalID);
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

    @Override
    public List<String> getOverlordsProposals() {
        return overlordsProposals;
    }

    @Override
    public void addVassalisationProposal(Territory proposal) {
        overlordsProposals.add(proposal.getID());
        if (proposal instanceof Nation) {
            broadcastMessageWithSound(Lang.NATION_DIPLOMATIC_INVITATION_RECEIVED_1.get(proposal.getColoredName(), this.getColoredName()), SoundEnum.MINOR_GOOD);
        } else {
            broadcastMessageWithSound(Lang.REGION_DIPLOMATIC_INVITATION_RECEIVED_1.get(proposal.getColoredName(), this.getColoredName()), SoundEnum.MINOR_GOOD);
        }
        EventManager.getInstance().callEvent(new TerritoryVassalProposalInternalEvent(proposal, this));
    }


    @Override
    public Map<Integer, RankData> getRanks() {
        if (ranks == null) {
            ranks = new HashMap<>();
        }
        return ranks;
    }

    @Override
    public Collection<RankData> getAllRanksSorted() {
        return getRanks().values().stream().sorted(Comparator.comparingInt(p -> -p.getLevel())).toList();
    }

    @Override
    public RankData getRank(int rankID) {
        return getRanks().get(rankID);
    }

    @Override
    public boolean isRankNameUsed(String message) {
        for (RankData rank : getAllRanks()) {
            if (rank.getName().equals(message)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public RankData registerNewRank(String rankName) {
        int nextRankId = 0;
        for (RankData rank : getAllRanks()) {
            if (rank.getID() >= nextRankId) nextRankId = rank.getID() + 1;
        }

        RankData newRank = new RankData(nextRankId, rankName);
        getRanks().put(nextRankId, newRank);
        return newRank;
    }

    @Override
    public void removeRank(int key) {
        getRanks().remove(key);
    }

    @Override
    public int getDefaultRankID() {
        return defaultRankID;
    }

    @Override
    public void setDefaultRank(int rankID) {
        this.defaultRankID = rankID;
    }

    @Override
    public boolean doesPlayerHavePermission(ITanPlayer tanPlayer, RolePermission townRolePermission) {

        if (!this.isPlayerIn(tanPlayer)) {
            return false;
        }

        if (isLeader(tanPlayer)) return true;

        return getRank(tanPlayer).hasPermission(townRolePermission);
    }

    @Override
    public void setPlayerRank(ITanPlayer playerStat, RankData rankData) {
        getRank(playerStat).removePlayer(playerStat);
        rankData.addPlayer(playerStat);
        specificSetPlayerRank(playerStat, rankData.getID());
    }

    protected abstract void specificSetPlayerRank(ITanPlayer playerStat, int rankID);


    @Override
    public Budget getBudget() {
        Budget budget = new Budget();
        budget.addProfitLine(new SalaryPaymentLine(this));
        budget.addProfitLine(new ChunkUpkeepLine(this));
        addSpecificTaxes(budget);
        return budget;
    }

    protected abstract void addSpecificTaxes(Budget budget);

    @Override
    public int getNumberOfClaimedChunk() {
        return TownsAndNations.getPlugin().getClaimStorage().getAllChunkFrom(this).size();
    }

    @Override
    public double getTax() {
        return baseTax;
    }

    @Override
    public void setTax(double newTax) {
        baseTax = newTax;
    }

    @Override
    public void addToTax(double i) {
        setTax(baseTax + i);
    }

    @Override
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
                ITanPlayer tanPlayer = TownsAndNations.getPlugin().getPlayerDataStorage().get(playerId);
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


        List<TerritoryChunk> borderChunks = ChunkUtil.getBorderChunks(this);


        for (TerritoryChunk claimedChunk : borderChunks) {
            if (RandomUtil.getRandom().nextDouble() < percentageOfChunkToKeep) {
                TownsAndNations.getPlugin().getClaimStorage().unclaimChunkAndUpdate(claimedChunk);
                nbOfUnclaimedChunk++;
            }
        }
        if (nbOfUnclaimedChunk < minNbOfUnclaimedChunk) {
            for (TerritoryChunk claimedChunk : borderChunks) {
                TownsAndNations.getPlugin().getClaimStorage().unclaimChunkAndUpdate(claimedChunk);
                nbOfUnclaimedChunk++;
                if (nbOfUnclaimedChunk >= minNbOfUnclaimedChunk) break;
            }
        }
    }

    protected abstract void collectTaxes();

    @Override
    public double getTaxOnRentingProperty() {
        if (propertyRentTax > 1) propertyRentTax = 1; //Convert to percentage
        return propertyRentTax;
    }

    @Override
    public void setTaxOnRentingProperty(double amount) {
        propertyRentTax = amount;
    }

    @Override
    public double getTaxOnBuyingProperty() {
        if (propertyBuyTax > 1) propertyBuyTax = 1; //Convert to percentage
        return propertyBuyTax;
    }

    @Override
    public void setTaxOnBuyingProperty(double amount) {
        propertyBuyTax = amount;
    }

    @Override
    public double getTaxOnCreatingProperty() {
        return propertyCreateTax;
    }

    @Override
    public void setTaxOnCreatingProperty(double amount) {
        propertyCreateTax = amount;
    }

    /**
     * @return true if the territory is involved in at least one attack currently undergoing.
     */
    @Override
    public boolean attackInProgress() {
        return !getCurrentAttacks().isEmpty();
    }

    /**
     * @return true if the territory is involved in at least one war.
     */
    @Override
    public boolean isAtWar(){
        return !TownsAndNations.getPlugin().getWarStorage().getWarsOfTerritory(this).isEmpty();
    }

    @Override
    public RankData getDefaultRank() {
        return getRank(getDefaultRankID());
    }

    @Override
    public void registerPlayer(ITanPlayer tanPlayer) {
        getDefaultRank().addPlayer(tanPlayer);
        tanPlayer.setRankID(this, getDefaultRankID());
    }

    @Override
    public void unregisterPlayer(ITanPlayer tanPlayer) {
        getRank(tanPlayer).removePlayer(tanPlayer);
        tanPlayer.setRankID(this, null);
    }

    @Override
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

    @Override
    public String getLeaderName() {
        if (this.haveNoLeader()) return Lang.NO_LEADER.getDefault();
        return getLeaderData().getNameStored();
    }

    @Override
    public List<String> getOwnedFortIDs() {
        if (fortIds == null) fortIds = new ArrayList<>();
        return fortIds;
    }

    @Override
    public List<String> getOccupiedFortIds() {
        if (occupiedFortIds == null) occupiedFortIds = new ArrayList<>();
        return occupiedFortIds;
    }

    @Override
    public Collection<Building> getBuildings() {
        List<Building> buildings = new ArrayList<>(TownsAndNations.getPlugin().getFortStorage().getOwnedFort(this));

        if (this instanceof Town townData) {
            buildings.addAll(townData.getPropertiesInternal());
        }
        buildings.removeAll(Collections.singleton(null));
        return buildings;
    }

    @Override
    public void addOwnedFort(Fort fortToCapture) {
        if (fortToCapture == null) {
            return;
        }
        getOwnedFortIDs().add(fortToCapture.getID());
    }

    @Override
    public void removeOwnedFort(Fort fortToCapture) {
        if (fortToCapture == null) {
            return;
        }
        getOwnedFortIDs().remove(fortToCapture.getID());
    }

    @Override
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

    @Override
    public TerritoryStats getNewLevel() {
        if (this.upgradesStatus == null) {
            // Migrate old data if exists
            if (this instanceof Town) {
                this.upgradesStatus = new TerritoryStats(StatsType.TOWN);
            } else if (this instanceof Nation) {
                this.upgradesStatus = new TerritoryStats(StatsType.NATION);
            } else {
                this.upgradesStatus = new TerritoryStats(StatsType.REGION);
            }
        }
        return upgradesStatus;
    }

    @Override
    public int getNumberOfOccupiedChunk() {
        int count = 0;
        for(TerritoryChunk territoryChunk : TownsAndNations.getPlugin().getClaimStorage().getAllChunkFrom(this)){
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
    @Override
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

        Optional<Town> capitalTownOpt = getCapitalTown();
        boolean capitalExist = capitalTownOpt.isPresent() && capitalTownOpt.get().getCapitalLocation().isPresent();
        boolean capitalCaptured = capitalTownOpt.isPresent() && capitalTownOpt.get().isTownCapitalOccupied();

        if(capitalExist && capitalCaptured){
            ratio += Constants.getCaptureCapitalBonusPercentage();
        }

        if(ratio > Constants.getCapturePercentageToSurrender()){
            for(War war : TownsAndNations.getPlugin().getWarStorage().getWarsOfTerritory(this)){
                if(war.isMainAttacker(this) || war.isMainDefender(this)){
                    war.territorySurrender(this);
                }
            }
        }

    }

    @Override
    public void setBanner(Material material, List<Pattern> patterns) {
        this.bannerBuilder = new BannerBuilder(material, patterns);
    }

    @Override
    public BannerBuilder getBanner() {
        if(bannerBuilder == null) {
            bannerBuilder = new BannerBuilder();
        }
        return bannerBuilder;
    }

    @Override
    public int getLevel(){
        return getNewLevel().getMainLevel();
    }

    @Override
    public EDiplomacyState getRelationWith(TanPlayer playerData){
        return getWorstRelationWith(TownsAndNations.getPlugin().getPlayerDataStorage().get(playerData.getID())).toAPI();
    }

    @Override
    public Collection<TanProperty> getProperties() {
        return Collections.emptyList();
    }

    @Override
    public TeleportationData getTeleportationData() {
        if(this.teleportationPosition == null){
            this.teleportationPosition = new TeleportationData();
        }
        return this.teleportationPosition;
    }

    @Override
    public void broadCastBarMessage(FilledLang filledLang) {

        for(Player player : getPlayers()){
            LangType langType = TownsAndNations.getPlugin().getPlayerDataStorage().get(player).getLang();
            TextComponent message = new TextComponent(filledLang.get(langType));
            message.setColor(ChatColor.GRAY);
            message.setItalic(true);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
        }
    }

    @Override
    public boolean authorizeTeleportation(Territory territoryData) {
        return getTeleportationData().isTeleportationAllowed(getRelationWith(territoryData));
    }
}
