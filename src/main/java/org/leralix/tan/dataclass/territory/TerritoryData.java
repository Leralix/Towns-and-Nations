package org.leralix.tan.dataclass.territory;


import dev.triumphteam.gui.guis.GuiItem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.position.Vector3D;
import org.leralix.lib.utils.RandomUtil;
import org.leralix.tan.building.Building;
import org.leralix.tan.dataclass.*;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.dataclass.territory.cosmetic.CustomIcon;
import org.leralix.tan.dataclass.territory.cosmetic.ICustomIcon;
import org.leralix.tan.dataclass.territory.cosmetic.PlayerHeadIcon;
import org.leralix.tan.dataclass.territory.economy.Budget;
import org.leralix.tan.dataclass.territory.economy.ChunkUpkeepLine;
import org.leralix.tan.dataclass.territory.economy.SalaryPaymentLine;
import org.leralix.tan.dataclass.territory.permission.PermissionGiven;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.DiplomacyProposalAcceptedInternalEvent;
import org.leralix.tan.events.events.DiplomacyProposalInternalEvent;
import org.leralix.tan.events.events.TerritoryVassalAcceptedInternalEvent;
import org.leralix.tan.events.events.TerritoryVassalProposalInternalEvent;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.gui.legacy.PlayerGUI;
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
import org.leralix.tan.upgrade.TerritoryStats;
import org.leralix.tan.upgrade.rewards.StatsType;
import org.leralix.tan.upgrade.rewards.list.BiomeStat;
import org.leralix.tan.upgrade.rewards.numeric.ChunkCap;
import org.leralix.tan.upgrade.rewards.numeric.ChunkCost;
import org.leralix.tan.upgrade.rewards.numeric.ChunkUpkeepCost;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.graphic.PrefixUtil;
import org.leralix.tan.utils.graphic.TeamUtils;
import org.leralix.tan.utils.territory.ChunkUtil;
import org.leralix.tan.utils.text.StringUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.fort.Fort;
import org.leralix.tan.war.legacy.CurrentAttack;

import java.util.*;
import java.util.function.Consumer;

public abstract class TerritoryData {

    protected String id;
    protected String name;
    protected String description;
    protected String overlordID;
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
    private Collection<String> attackIncomingList;
    private HashMap<String, Integer> availableClaims;
    private Map<String, DiplomacyProposal> diplomacyProposals;
    private List<String> overlordsProposals;
    private ClaimedChunkSettings chunkSettings;
    private List<String> fortIds;
    private List<String> occupiedFortIds;
    protected TerritoryStats upgradesStatus;

    protected TerritoryData(String id, String name, ITanPlayer owner) {
        this.id = id;
        this.name = name;
        this.description = Lang.DEFAULT_DESCRIPTION.getDefault();
        this.dateTimeCreated = new Date().getTime();

        this.customIcon = new PlayerHeadIcon(owner);

        this.treasury = 0.0;
        this.baseTax = 1.0;
        this.propertyRentTax = 0.1;
        this.propertyBuyTax = 0.1;
        this.propertyCreateTax = 0.5;

        ranks = new HashMap<>();
        RankData defaultRank = registerNewRank("default");
        setDefaultRank(defaultRank);

        attackIncomingList = new ArrayList<>();
        availableClaims = new HashMap<>();
        diplomacyProposals = new HashMap<>();
        overlordsProposals = new ArrayList<>();

        chunkSettings = new ClaimedChunkSettings(PermissionGiven.ofTerritory(this));

        color = StringUtil.randomColor();
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void rename(Player player, int cost, String newName) {
        if (getBalance() < cost) {
            TanChatUtils.message(player, Lang.TERRITORY_NOT_ENOUGH_MONEY.get(player, getColoredName(), Double.toString(cost - getBalance())));
            return;
        }

        removeFromBalance(cost);
        FileUtil.addLineToHistory(Lang.HISTORY_TOWN_NAME_CHANGED.get(player.getName(), name, newName));

        TanChatUtils.message(player, Lang.CHANGE_MESSAGE_SUCCESS.get(player, name, newName), SoundEnum.GOOD);
        rename(newName);
    }

    public void rename(String newName) {
        this.name = newName;
    }

    public abstract int getHierarchyRank();

    public abstract String getBaseColoredName();

    public TextComponent getCustomColoredName() {
        TextComponent coloredName = new TextComponent(getName());
        coloredName.setColor(getChunkColor());
        return coloredName;
    }

    public abstract String getLeaderID();

    public abstract ITanPlayer getLeaderData();

    public abstract void setLeaderID(String leaderID);

    public boolean isLeader(ITanPlayer tanPlayer) {
        return isLeader(tanPlayer.getID());
    }

    public abstract boolean isLeader(String playerID);

    public boolean isLeader(Player player) {
        return isLeader(player.getUniqueId().toString());
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

    public abstract Collection<String> getPlayerIDList();

    public boolean isPlayerIn(ITanPlayer tanPlayer) {
        return isPlayerIn(tanPlayer.getID());
    }

    public boolean isPlayerIn(Player player) {
        return isPlayerIn(player.getUniqueId().toString());
    }

    public boolean isPlayerIn(String playerID) {
        return getPlayerIDList().contains(playerID);
    }

    public Collection<String> getOrderedPlayerIDList() {
        List<String> sortedList = new ArrayList<>();
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

    @SuppressWarnings("unused")
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

        TownRelation relation = getRelationWith(territoryData);
        return icon.addDescription(Lang.GUI_TOWN_INFO_TOWN_RELATION.get(relation.getColoredName(langType)));
    }

    public Collection<String> getAttacksInvolvedID() {
        if (attackIncomingList == null) this.attackIncomingList = new ArrayList<>();
        return attackIncomingList;
    }

    public void addPlannedAttack(PlannedAttack war) {
        getAttacksInvolvedID().add(war.getID());
    }

    public void removePlannedAttack(PlannedAttack war) {
        getAttacksInvolvedID().remove(war.getID());
    }

    public Collection<CurrentAttack> getCurrentAttacks() {
        Collection<CurrentAttack> res = new ArrayList<>();
        for (String attackID : getAttacksInvolvedID()) {
            CurrentAttack attackInvolved = CurrentAttacksStorage.get(attackID);
            if (attackInvolved != null) {
                res.add(attackInvolved);
            }
        }
        return res;
    }

    public void removeCurrentAttack(CurrentAttack currentAttacks) {
        getAttacksInvolvedID().remove(currentAttacks.getAttackData().getID());
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
        broadcastMessageWithSound(Lang.ACCEPTED_VASSALISATION_PROPOSAL_ALL.get(this.getBaseColoredName(), overlord.getBaseColoredName()), SoundEnum.GOOD);

        this.overlordID = overlord.getID();
        overlord.addVassal(this);
    }

    public Optional<TerritoryData> getOverlord() {
        if (overlordID == null) return Optional.empty();
        TerritoryData overlord = TerritoryUtil.getTerritory(overlordID);
        if (overlord == null) {
            overlordID = null;
            return Optional.empty();
        }
        return Optional.of(overlord);

    }

    /**
     * @return All potential overlords of this territory (Kingdom and region)
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
        return capital.map(overlord -> Objects.equals(overlord.getCapital().getID(), getID())).orElse(false);
    }

    public abstract TerritoryData getCapital();


    public int getChunkColorCode() {
        if (color == null) color = StringUtil.randomColor();
        return color;
    }

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
            TanChatUtils.message(player, Lang.CHUNK_IS_BLACKLISTED.get(player));
            return false;
        }

        if (!doesPlayerHavePermission(tanPlayer, RolePermission.CLAIM_CHUNK)) {
            TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(player));
            return false;
        }

        TerritoryStats territoryStats = getNewLevel();
        int nbOfClaimedChunks = getNumberOfClaimedChunk();

        if (!territoryStats.getStat(BiomeStat.class).canClaimBiome(chunk)) {
            TanChatUtils.message(player, Lang.CHUNK_BIOME_NOT_ALLOWED.get(player));
            return false;
        }

        if (!territoryStats.getStat(ChunkCap.class).canDoAction(nbOfClaimedChunks)) {
            TanChatUtils.message(player, Lang.MAX_CHUNK_LIMIT_REACHED.get(player));
            return false;
        }

        int cost = getClaimCost();
        if (getBalance() < cost) {
            TanChatUtils.message(player, Lang.TERRITORY_NOT_ENOUGH_MONEY.get(player, getColoredName(), Double.toString(cost - getBalance())));
            return false;
        }

        ClaimedChunk2 chunkData = NewClaimedChunkStorage.getInstance().get(chunk);
        if (!chunkData.canTerritoryClaim(player, this)) {
            return false;
        }

        if (ignoreAdjacent) {
            return true;
        }

        // If first claim of the territory and in a buffer zone of another territory, deny the claim
        if (getNumberOfClaimedChunk() == 0) {

            if (ChunkUtil.isInBufferZone(chunkData, this)) {
                TanChatUtils.message(player, Lang.CHUNK_IN_BUFFER_ZONE.get(player, Integer.toString(Constants.territoryClaimBufferZone())));
                return false;
            }
            return true;
        }


        if (!NewClaimedChunkStorage.getInstance().isOneAdjacentChunkClaimedBySameTerritory(chunk, getID())) {
            TanChatUtils.message(player, Lang.CHUNK_NOT_ADJACENT.get(player));
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

        for (TerritoryData territory : getVassals()) {
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

    public boolean canConquerChunk(ClaimedChunk2 chunk) {
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
        TanChatUtils.message(player, Lang.PLAYER_SEND_MONEY_SUCCESS.get(langType, Double.toString(amount), getBaseColoredName()), SoundEnum.MINOR_GOOD);
    }

    public abstract void openMainMenu(Player player);

    public abstract boolean canHaveVassals();

    public abstract boolean canHaveOverlord();

    public abstract List<String> getVassalsID();

    public List<TerritoryData> getVassals() {
        List<TerritoryData> res = new ArrayList<>();
        for (String vassalID : getVassalsID()) {
            TerritoryData vassal = TerritoryUtil.getTerritory(vassalID);
            if (vassal != null) res.add(vassal);
        }
        return res;
    }

    public int getVassalCount() {
        return getVassalsID().size();
    }

    public boolean isVassal(TerritoryData territoryData) {
        return isVassal(territoryData.getID());
    }

    public abstract boolean isVassal(String territoryID);


    public abstract Collection<TerritoryData> getPotentialVassals();

    private List<String> getOverlordsProposals() {
        if (overlordsProposals == null) overlordsProposals = new ArrayList<>();
        return overlordsProposals;
    }

    public void addVassalisationProposal(TerritoryData proposal) {
        getOverlordsProposals().add(proposal.getID());
        broadcastMessageWithSound(Lang.REGION_DIPLOMATIC_INVITATION_RECEIVED_1.get(proposal.getBaseColoredName(), getBaseColoredName()), SoundEnum.MINOR_GOOD);
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

    public List<GuiItem> getAllSubjugationProposals(Player player, int page) {
        ArrayList<GuiItem> proposals = new ArrayList<>();
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        LangType langType = tanPlayer.getLang();

        for (String proposalID : getOverlordsProposals()) {
            TerritoryData proposalOverlord = TerritoryUtil.getTerritory(proposalID);
            if (proposalOverlord == null) continue;

            proposals.add(proposalOverlord
                    .getIconWithInformations(langType)
                    .setClickToAcceptMessage(
                            Lang.GUI_GENERIC_LEFT_CLICK_TO_ACCEPT,
                            Lang.RIGHT_CLICK_TO_REFUSE
                    )
                    .setAction(action -> {
                        if (action.isLeftClick()) {
                            if (haveOverlord()) {
                                TanChatUtils.message(player, Lang.TOWN_ALREADY_HAVE_OVERLORD.get(langType), SoundEnum.NOT_ALLOWED);
                                return;
                            }

                            setOverlord(proposalOverlord);
                            broadcastMessageWithSound(Lang.ACCEPTED_VASSALISATION_PROPOSAL_ALL.get(this.getBaseColoredName(), proposalOverlord.getName()), SoundEnum.GOOD);
                            PlayerGUI.openHierarchyMenu(player, this);
                        } else if (action.isRightClick()) {
                            getOverlordsProposals().remove(proposalID);
                            PlayerGUI.openChooseOverlordMenu(player, this, page);
                        }
                    })
                    .asGuiItem(player, langType)
            );
        }
        return proposals;
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

    public boolean doesPlayerHavePermission(ITanPlayer tanPlayer, RolePermission townRolePermission) {

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
            List<String> playerIdList = rank.getPlayersID();
            double costOfSalary = (double) playerIdList.size() * rankSalary;

            if (rankSalary == 0 || costOfSalary > getBalance()) {
                continue;
            }
            removeFromBalance(costOfSalary);
            for (String playerId : playerIdList) {
                ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(playerId);
                EconomyUtil.addFromBalance(tanPlayer, rankSalary);
                TransactionManager.getInstance().register(new SalaryTransaction(getID(), playerId, costOfSalary));
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


        List<ClaimedChunk2> borderChunks = ChunkUtil.getBorderChunks(this);


        for (ClaimedChunk2 claimedChunk2 : borderChunks) {
            if (RandomUtil.getRandom().nextDouble() < percentageOfChunkToKeep) {
                NewClaimedChunkStorage.getInstance().unclaimChunkAndUpdate(claimedChunk2);
                nbOfUnclaimedChunk++;
            }
        }
        if (nbOfUnclaimedChunk < minNbOfUnclaimedChunk) {
            for (ClaimedChunk2 claimedChunk2 : borderChunks) {
                NewClaimedChunkStorage.getInstance().unclaimChunkAndUpdate(claimedChunk2);
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

    public boolean isAtWar() {
        return !getCurrentAttacks().isEmpty();
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
            return getCustomColoredName().getText();
        } else {
            return getBaseColoredName();
        }
    }

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
            buildings.addAll(townData.getProperties());
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
        for (String playerID : getPlayerIDList()) {
            Player player = Bukkit.getPlayer(UUID.fromString(playerID));
            if (player != null) {
                playerList.add(player);
            }
        }
        return playerList;
    }

    /**
     * Defines if a territory can claim next to an already claimed chunk.
     * If the chunk is owned by the territory itself or by its overlord, it can claim
     *
     * @param territoryChunk The chunk to check
     * @return True if the territory can claim next to the chunk, false otherwise
     */
    public boolean canAccessBufferZone(TerritoryChunk territoryChunk) {
        String ownerID = territoryChunk.getOwnerID();
        if (ownerID.equals(id)) {
            return true;
        }
        Optional<TerritoryData> optCapital = getOverlord();

        if (optCapital.isPresent()) {
            TerritoryData capital = optCapital.get();
            return ownerID.equals(capital.getID());
        }
        return false;
    }

    public TerritoryStats getNewLevel() {
        if (this.upgradesStatus == null) {
            // Migrate old data if exists
            if (this instanceof TownData) {
                this.upgradesStatus = new TerritoryStats(StatsType.TOWN);
            } else {
                this.upgradesStatus = new TerritoryStats(StatsType.REGION);
            }
        }
        return upgradesStatus;
    }
}
