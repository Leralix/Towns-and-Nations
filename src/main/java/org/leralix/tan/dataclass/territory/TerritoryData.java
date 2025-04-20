package org.leralix.tan.dataclass.territory;


import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.RandomUtil;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.*;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.newhistory.ChunkPaymentHistory;
import org.leralix.tan.dataclass.newhistory.MiscellaneousHistory;
import org.leralix.tan.dataclass.newhistory.PlayerDonationHistory;
import org.leralix.tan.dataclass.newhistory.SalaryPaymentHistory;
import org.leralix.tan.dataclass.territory.cosmetic.ICustomIcon;
import org.leralix.tan.dataclass.territory.cosmetic.PlayerHeadIcon;
import org.leralix.tan.dataclass.territory.economy.Budget;
import org.leralix.tan.dataclass.territory.economy.ChunkUpkeepLine;
import org.leralix.tan.dataclass.territory.economy.SalaryPaymentLine;
import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.dataclass.wars.CurrentAttack;
import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.newsletter.NewsletterStorage;
import org.leralix.tan.newsletter.news.DiplomacyProposalNL;
import org.leralix.tan.newsletter.news.JoinRegionProposalNL;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlannedAttackStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.*;

import java.util.*;
import java.util.function.Consumer;

public abstract class TerritoryData {

    private String id;
    private String name;
    private String description;
    protected String overlordID;
    private Long dateTimeCreated;
    private ICustomIcon customIcon;
    private RelationData relations;
    private Double baseTax;
    private double propertyRentTax;
    private double propertyBuyTax;
    protected Integer color;
    protected Integer defaultRankID;
    protected Map<Integer, RankData> ranks;
    private Collection<String> attackIncomingList;
    private Collection<String> currentAttackList;
    private HashMap<String, Integer> availableClaims;
    private Map<String, DiplomacyProposal> diplomacyProposals;
    private List<String> overlordsProposals;
    private ClaimedChunkSettings chunkSettings;
    private StrongholdData stronghold;

    protected TerritoryData(String id, String name, String ownerID){
        this.id = id;
        this.name = name;
        this.description = Lang.DEFAULT_DESCRIPTION.get();
        this.dateTimeCreated = new Date().getTime();

        this.customIcon = new PlayerHeadIcon(ownerID);

        this.baseTax = 1.0;
        this.propertyRentTax = 0.1;
        this.propertyBuyTax = 0.1;

        ranks = new HashMap<>();
        RankData defaultRank = registerNewRank("default");
        setDefaultRank(defaultRank);

        attackIncomingList = new ArrayList<>();
        currentAttackList = new ArrayList<>();
        availableClaims = new HashMap<>();
        diplomacyProposals = new HashMap<>();
        overlordsProposals = new ArrayList<>();

        color = StringUtil.randomColor();
    }

    protected abstract String getOldID();
    public String getID(){
        if(id == null)
            id = getOldID();
        return id;
    }
    protected abstract String getOldName();
    public String getName(){
        if(name == null)
            name = getOldName();
        return name;
    }
    public void rename(Player player, int cost, String newName){
        if(getBalance() < cost){
            player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get());
            return;
        }

        TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new MiscellaneousHistory(this, cost));

        removeFromBalance(cost);
        FileUtil.addLineToHistory(Lang.HISTORY_TOWN_NAME_CHANGED.get(player.getName(),this.getName(),newName));

        player.sendMessage(TanChatUtils.getTANString() + Lang.CHANGE_MESSAGE_SUCCESS.get(this.getName(),newName));
        SoundUtil.playSound(player, SoundEnum.GOOD);
        rename(newName);
    }

    public void rename(String newName){
        this.name = newName;
    }

    public abstract int getHierarchyRank();
    public abstract String getColoredName();
    public abstract String getLeaderID();
    public abstract PlayerData getLeaderData();
    public abstract void setLeaderID(String leaderID);
    public boolean isLeader(PlayerData playerData){
        return isLeader(playerData.getID());
    }
    public abstract boolean isLeader(String playerID);
    public boolean isLeader(Player player){
        return isLeader(player.getUniqueId().toString());
    }

    public String getDescription(){
        if(description == null)
            description = Lang.DEFAULT_DESCRIPTION.get();
        return description;
    }
    public void setDescription(String newDescription){
        this.description = newDescription;
    }
    public ItemStack getIcon(){
        if(this.customIcon == null){
            customIcon = new PlayerHeadIcon(getLeaderID());
        }
        return customIcon.getIcon();
    }
    public void setIcon(ICustomIcon icon){
        this.customIcon = icon;
    }
    public abstract Collection<String> getPlayerIDList();
    public boolean isPlayerIn(PlayerData playerData){
        return isPlayerIn(playerData.getID());
    }
    public boolean isPlayerIn(String playerID){
        return getPlayerIDList().contains(playerID);
    }

    public Collection<String> getOrderedPlayerIDList(){
        List<String> sortedList = new ArrayList<>();
        List<PlayerData> playerDataSorted = getPlayerDataList().stream()
                .sorted(Comparator.comparingInt(playerData -> -this.getRank(playerData.getRankID(this)).getLevel()))
                .toList();

        for(PlayerData playerData : playerDataSorted){
            sortedList.add(playerData.getID());
        }
        return sortedList;
    }
    public abstract Collection<PlayerData> getPlayerDataList();

    public ClaimedChunkSettings getChunkSettings(){
        if(chunkSettings == null)
            chunkSettings = new ClaimedChunkSettings();
        return chunkSettings;
    }

    public RelationData getRelations(){
        if(relations == null)
            relations = new RelationData();
        return relations;
    }

    public void setRelation(TerritoryData otherTerritory, TownRelation relation){
        TownRelation actualRelation = getRelationWith(otherTerritory);
        if(relation.isSuperiorTo(actualRelation)){
            broadcastMessageWithSound(Lang.BROADCAST_RELATION_IMPROVE.get(getColoredName(), otherTerritory.getColoredName(),relation.getColoredName()), SoundEnum.GOOD);
            otherTerritory.broadcastMessageWithSound(Lang.BROADCAST_RELATION_IMPROVE.get(otherTerritory.getColoredName(), getColoredName(),relation.getColoredName()), SoundEnum.GOOD);
        }
        else{
            broadcastMessageWithSound(Lang.BROADCAST_RELATION_WORSEN.get(getColoredName(), otherTerritory.getColoredName(),relation.getColoredName()), SoundEnum.BAD);
            otherTerritory.broadcastMessageWithSound(Lang.BROADCAST_RELATION_WORSEN.get(otherTerritory.getColoredName(), getColoredName(),relation.getColoredName()), SoundEnum.BAD);
        }

        this.getRelations().setRelation(relation,otherTerritory);
        otherTerritory.getRelations().setRelation(relation,this);

        TeamUtils.updateAllScoreboardColor();
    }


    private Map<String, DiplomacyProposal> getDiplomacyProposals(){
        if(diplomacyProposals == null)
            diplomacyProposals = new HashMap<>();
        return diplomacyProposals;
    }

    public void removeDiplomaticProposal(TerritoryData proposingTerritory){
        removeDiplomaticProposal(proposingTerritory.getID());
    }
    public void removeDiplomaticProposal(String proposingTerritoryID){
        getDiplomacyProposals().remove(proposingTerritoryID);
    }
    private void addDiplomaticProposal(TerritoryData proposingTerritory, TownRelation wantedRelation){
        getDiplomacyProposals().put(proposingTerritory.getID(), new DiplomacyProposal(proposingTerritory.getID(), getID(), wantedRelation));
        NewsletterStorage.registerNewsletter(new DiplomacyProposalNL(proposingTerritory.getID(), getID(), wantedRelation));
    }

    public void receiveDiplomaticProposal(TerritoryData proposingTerritory, TownRelation wantedRelation) {
        removeDiplomaticProposal(proposingTerritory);
        addDiplomaticProposal(proposingTerritory, wantedRelation);
    }

    public Collection<DiplomacyProposal> getAllDiplomacyProposal(){
        return getDiplomacyProposals().values();
    }

    public TownRelation getRelationWith(TerritoryData territoryData){
        return getRelationWith(territoryData.getID());
    }
    public TownRelation getRelationWith(String territoryID){
        if(getID().equals(territoryID))
            return TownRelation.SELF;

        if(haveOverlord() && getOverlord().getID().equals(territoryID))
            return TownRelation.OVERLORD;

        if(getVassalsID().contains(territoryID))
            return TownRelation.VASSAL;

        return getRelations().getRelationWith(territoryID);
    }
    @SuppressWarnings("unused")
    public long getCreationDate(){
        if(dateTimeCreated == null){
            dateTimeCreated = getOldDateTime();
        }
        return dateTimeCreated;
    }

    protected abstract long getOldDateTime();

    public abstract void addToBalance(double balance);

    public abstract void removeFromBalance(double balance);

    public abstract void broadCastMessage(String message);

    public abstract void broadcastMessageWithSound(String message, SoundEnum soundEnum, boolean addPrefix);

    public abstract void broadcastMessageWithSound(String message, SoundEnum soundEnum);
    public abstract boolean haveNoLeader();

    protected abstract ItemStack getIconWithName();
    public abstract ItemStack getIconWithInformations(LangType langType);
    public ItemStack getIconWithInformationAndRelation(TerritoryData territoryData, LangType langType){
        ItemStack icon = getIconWithInformations(langType);

        ItemMeta meta = icon.getItemMeta();
        if(meta != null){
            List<String> lore = meta.getLore();

            if(territoryData != null && lore != null){
                TownRelation relation = getRelationWith(territoryData);
                lore.add(Lang.GUI_TOWN_INFO_TOWN_RELATION.get(relation.getColor() + relation.getName()));
            }

            meta.setLore(lore);
            icon.setItemMeta(meta);
        }
        return icon;
    }

    public Collection<String> getAttacksInvolvedID(){
        if(attackIncomingList == null)
            this.attackIncomingList = new ArrayList<>();
        return attackIncomingList;
    }
    public Collection<PlannedAttack> getAttacksInvolved(){
        Collection<PlannedAttack> res = new ArrayList<>();
        for(String attackID : getAttacksInvolvedID()){
            PlannedAttack plannedAttack = PlannedAttackStorage.get(attackID);
            res.add(plannedAttack);
        }
        return res;
    }
    public void addPlannedAttack(PlannedAttack war){
        getAttacksInvolvedID().add(war.getID());

    }
    public void removePlannedAttack(PlannedAttack war){
        getAttacksInvolvedID().remove(war.getID());

    }


    public Collection<String> getCurrentAttacksID(){
        if(currentAttackList == null)
            this.currentAttackList = new ArrayList<>();
        return currentAttackList;
    }
    public Collection<CurrentAttack> getCurrentAttacks(){
        Collection<CurrentAttack> res = new ArrayList<>();
        for(String attackID : getCurrentAttacksID()){
            CurrentAttack attackInvolved = CurrentAttacksStorage.get(attackID);
            res.add(attackInvolved);
        }
        return res;
    }

    public void addCurrentAttack(CurrentAttack currentAttacks){
        getAttacksInvolvedID().add(currentAttacks.getId());

    }
    public void removeCurrentAttack(CurrentAttack currentAttacks){
        getAttacksInvolvedID().remove(currentAttacks.getId());
    }

    public abstract boolean atWarWith(String territoryID);


    public abstract double getBalance();



    public void setOverlord(TerritoryData overlord){
        getOverlordsProposals().remove(overlord.getID());
        broadcastMessageWithSound(Lang.ACCEPTED_VASSALISATION_PROPOSAL_ALL.get(this.getColoredName(), overlord.getColoredName()), SoundEnum.GOOD);

        this.overlordID = overlord.getID();
        overlord.addVassal(this);
    }

    public TerritoryData getOverlord(){
        return TerritoryUtil.getTerritory(overlordID);
    }

    /**
     * @return All potential overlords of this territory (Kingdom and region)
     */
    protected abstract Collection<TerritoryData> getOverlords();

    public void removeOverlord(){
        getOverlord().removeVassal(this);
        removeOverlordPrivate();
        this.overlordID = null;
    }
    public abstract void removeOverlordPrivate();


    public void addVassal(TerritoryData vassal){
        NewsletterStorage.removeVassalisationProposal(this, vassal);
        addVassalPrivate(vassal);
    }
    protected abstract void addVassalPrivate (TerritoryData vassal);

    protected void removeVassal(TerritoryData vassal){
        removeVassal(vassal.getID());
    }
    protected abstract void removeVassal(String vassalID);

    public abstract boolean isCapital();

    public TerritoryData getCapital(){
        return TerritoryUtil.getTerritory(getCapitalID());
    }

    public abstract String getCapitalID();

    public int getChunkColorCode(){
        if(color == null)
            color = StringUtil.randomColor();
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
    }

    public boolean haveOverlord(){
        return this.overlordID != null;
    }


    public Map<String, Integer> getAvailableEnemyClaims() {
        if(availableClaims == null)
            availableClaims = new HashMap<>();
        return availableClaims;
    }

    public void addAvailableClaims(String territoryID, int amount){
        getAvailableEnemyClaims().merge(territoryID, amount, Integer::sum);
    }
    public void consumeEnemyClaim(String territoryID){
        getAvailableEnemyClaims().merge(territoryID, -1, Integer::sum);
        if(getAvailableEnemyClaims().get(territoryID) <= 0)
            getAvailableEnemyClaims().remove(territoryID);
    }

    public void claimChunk(Player player){
        claimChunk(player, player.getLocation().getChunk());
    }

    public void claimChunk(Player player, Chunk chunk){
        Optional<ClaimedChunk2> claimedChunk2 = claimChunkInternal(player, chunk);
        if(claimedChunk2.isPresent() && getNumberOfClaimedChunk() == 1){
            stronghold = new StrongholdData(claimedChunk2.get());
        }
    }

    public void setStrongholdPosition(Chunk newChunk){
        this.stronghold.setPosition(newChunk);
    }

    public StrongholdData getStronghold(){
        if(getNumberOfClaimedChunk() == 0)
            return null;
        if(stronghold == null){
            ClaimedChunk2 claimedChunk2 = NewClaimedChunkStorage.getInstance().getAllChunkFrom(this).iterator().next();
            stronghold = new StrongholdData(claimedChunk2);
        }
        return stronghold;
    }

    protected abstract Optional<ClaimedChunk2> claimChunkInternal(Player player, Chunk chunk);


    public void castActionToAllPlayers(Consumer<Player> action){
        for(PlayerData playerData : getPlayerDataList()){
            Player player = playerData.getPlayer();
            if(player != null)
                action.accept(player);
        }
    }

    public void delete(){
        NewClaimedChunkStorage.getInstance().unclaimAllChunksFromTerritory(this); //Unclaim all chunk from town

        castActionToAllPlayers(HumanEntity::closeInventory);


        for(TerritoryData territory : getVassals()){
            territory.removeOverlord();
        }

        getRelations().cleanAll(this);   //Cancel all Relation between the deleted territory and other territories
        PlannedAttackStorage.territoryDeleted(this);
    }

    public boolean canConquerChunk(ClaimedChunk2 chunk) {
        if(getAvailableEnemyClaims().containsKey(chunk.getOwnerID())){
            consumeEnemyClaim(chunk.getOwnerID());
            return true;
        }
        return false;
    }

    public void addDonation(Player player, double amount) {
        double playerBalance = EconomyUtil.getBalance(player);

        if(playerBalance < amount ){
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NOT_ENOUGH_MONEY.get());
            return;
        }
        if(amount <= 0 ){
            player.sendMessage(TanChatUtils.getTANString() + Lang.PAY_MINIMUM_REQUIRED.get());
            return;
        }


        EconomyUtil.removeFromBalance(player,amount);
        addToBalance(amount);

        TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new PlayerDonationHistory(this, player, amount));
        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_SEND_MONEY_SUCCESS.get(amount, getColoredName()));
        SoundUtil.playSound(player, SoundEnum.MINOR_LEVEL_UP);
    }

    public abstract void openMainMenu(Player player);

    public abstract boolean canHaveVassals();
    public abstract boolean canHaveOverlord();

    public abstract List<String> getVassalsID();
    public List<TerritoryData> getVassals(){
        List<TerritoryData> res = new ArrayList<>();
        for(String vassalID : getVassalsID()){
            TerritoryData vassal = TerritoryUtil.getTerritory(vassalID);
            if(vassal != null)
                res.add(vassal);
        }
        return res;
    }
    public int getVassalCount(){
        return getVassalsID().size();
    }

    public boolean isVassal(TerritoryData territoryData) {
        return isVassal(territoryData.getID());
    }
    public abstract boolean isVassal(String territoryID);

    public boolean isCapitalOf(TerritoryData territoryData) {
        return isCapitalOf(territoryData.getID());
    }
    public abstract boolean isCapitalOf(String territoryID);

    public abstract Collection<TerritoryData> getPotentialVassals();

    private List<String> getOverlordsProposals(){
        if(overlordsProposals == null)
            overlordsProposals = new ArrayList<>();
        return overlordsProposals;
    }

    public void addVassalisationProposal(TerritoryData proposal){
        getOverlordsProposals().add(proposal.getID());
        broadcastMessageWithSound(Lang.REGION_DIPLOMATIC_INVITATION_RECEIVED_1.get(proposal.getColoredName(), getColoredName()), SoundEnum.MINOR_GOOD);
        NewsletterStorage.registerNewsletter(new JoinRegionProposalNL(proposal, this));
    }

    public void removeVassalisationProposal(TerritoryData proposal){
        getOverlordsProposals().remove(proposal.getID());
    }

    public boolean containsVassalisationProposal(TerritoryData proposal){
        return getOverlordsProposals().contains(proposal.getID());
    }

    public int getNumberOfVassalisationProposals(){
        return getOverlordsProposals().size();
    }

    public List<GuiItem> getAllSubjugationProposals(Player player, int page){
        ArrayList<GuiItem> proposals = new ArrayList<>();
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);

        for(String proposalID : getOverlordsProposals()) {
            TerritoryData proposalOverlord = TerritoryUtil.getTerritory(proposalID);
            if (proposalOverlord == null)
                continue;
            ItemStack territoryItem = proposalOverlord.getIconWithInformations(playerData.getLang());
            HeadUtils.addLore(territoryItem, Lang.LEFT_CLICK_TO_ACCEPT.get(), Lang.RIGHT_CLICK_TO_REFUSE.get());
            GuiItem acceptInvitation = ItemBuilder.from(territoryItem).asGuiItem(event -> {
                event.setCancelled(true);
                if(event.isLeftClick()){
                    if(haveOverlord()){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_ALREADY_HAVE_OVERLORD.get());
                        SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                        return;
                    }

                    setOverlord(proposalOverlord);
                    broadcastMessageWithSound(Lang.ACCEPTED_VASSALISATION_PROPOSAL_ALL.get(this.getColoredName(), proposalOverlord.getName()), SoundEnum.GOOD);
                    PlayerGUI.openHierarchyMenu(player, this);
                }
                if(event.isRightClick()){
                    getOverlordsProposals().remove(proposalID);
                    PlayerGUI.openChooseOverlordMenu(player, this, page);
                }


            });
            proposals.add(acceptInvitation);
        }
        return proposals;
    }

    protected Map<Integer, RankData> getRanks(){
        if(ranks == null) {
            if(this instanceof TownData townData){
                ranks = townData.getOldRanks();
            }
            else {
                ranks = new HashMap<>();
            }
        } else if (ranks.isEmpty() && this instanceof TownData townData) {
            ranks = townData.getOldRanks();
        }
        return ranks;
    }
    public Collection<RankData> getAllRanks(){
        return getRanks().values();
    }

    public Collection<RankData> getAllRanksSorted(){
        return getRanks().values().stream()
                .sorted(Comparator.comparingInt(p -> -p.getLevel()))
                .toList();
    }

    public RankData getRank(int rankID){
        return getRanks().get(rankID);
    }
    public abstract RankData getRank(PlayerData playerData);

    public RankData getRank(Player player){
        return getRank(PlayerDataStorage.getInstance().get(player));
    }
    public int getNumberOfRank(){
        return getRanks().size();
    }

    public boolean isRankNameUsed(String message) {
        if(ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("AllowNameDuplication",false))
            return false;

        for (RankData rank : getAllRanks()) {
            if (rank.getName().equals(message)) {
                return true;
            }
        }
        return false;
    }

    public RankData registerNewRank(String rankName){
        int nextRankId = 0;
        for(RankData rank : getAllRanks()){
            if(rank.getID() >= nextRankId)
                nextRankId = rank.getID() + 1;
        }

        RankData newRank = new RankData(nextRankId, rankName);
        getRanks().put(nextRankId,newRank);
        return newRank;
    }

    public void removeRank(int key){
        getRanks().remove(key);
    }

    public int getDefaultRankID() {
        if(defaultRankID == null){
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

    public abstract List<GuiItem> getOrderedMemberList(PlayerData playerData);


    public boolean doesPlayerHavePermission(Player player, RolePermission townRolePermission) {
        return doesPlayerHavePermission(PlayerDataStorage.getInstance().get(player), townRolePermission);
    }
    public boolean doesPlayerHavePermission(PlayerData playerData, RolePermission townRolePermission) {

        if(!this.isPlayerIn(playerData)){
            return false;
        }

        if(isLeader(playerData))
            return true;

        return getRank(playerData).hasPermission(townRolePermission);
    }

    public void setPlayerRank(PlayerData playerStat, RankData rankData) {
        getRank(playerStat).removePlayer(playerStat);
        rankData.addPlayer(playerStat);
        specificSetPlayerRank(playerStat, rankData.getID());
    }

    protected abstract void specificSetPlayerRank(PlayerData playerStat, int rankID);

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

    public int getNumberOfClaimedChunk(){
        return NewClaimedChunkStorage.getInstance().getAllChunkFrom(this).size();
    }

    public abstract double getChunkUpkeepCost();

    public double getTax(){
        if(baseTax == null)
            setTax(0.0);
        return baseTax;
    }
    public void setTax(double newTax){
        baseTax = newTax;
    }

    public void addToTax(double i){
        setTax(getTax() + i);
    }

    public void executeTasks(){
        collectTaxes();
        paySalaries();
        payChunkUpkeep();
    }

    private void paySalaries() {
        for (RankData rank : getAllRanks()){
            int rankSalary = rank.getSalary();
            List<String> playerIdList = rank.getPlayersID();
            double costOfSalary = (double) playerIdList.size() * rankSalary;

            if(rankSalary == 0 || costOfSalary > getBalance() ){
                continue;
            }
            removeFromBalance(costOfSalary);
            for(String playerId : playerIdList){
                PlayerData playerData = PlayerDataStorage.getInstance().get(playerId);
                EconomyUtil.addFromBalance(playerData, rankSalary);
                TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new SalaryPaymentHistory(this, String.valueOf(rank.getID()), costOfSalary));
            }
        }
    }

    private void payChunkUpkeep() {
        double upkeepCost = this.getChunkUpkeepCost();

        int numberClaimedChunk = getNumberOfClaimedChunk();
        double totalUpkeep = numberClaimedChunk * upkeepCost;
        if (totalUpkeep > getBalance()){
            deletePortionOfChunk();
            TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new ChunkPaymentHistory(this,-1));
        }
        else{
            removeFromBalance(totalUpkeep);
            TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new ChunkPaymentHistory(this,totalUpkeep));
        }

    }

    private void deletePortionOfChunk() {
        int minNbOfUnclaimedChunk = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("minimumNumberOfChunksUnclaimed",5);
        int nbOfUnclaimedChunk = 0;
        double minPercentageOfChunkToKeep = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getDouble("percentageOfChunksUnclaimed",10) / 100;


        Collection<ClaimedChunk2> allChunkFrom = NewClaimedChunkStorage.getInstance().getAllChunkFrom(this);
        for(ClaimedChunk2 claimedChunk2 : allChunkFrom){
            if(RandomUtil.getRandom().nextDouble() < minPercentageOfChunkToKeep){
                NewClaimedChunkStorage.getInstance().unclaimChunk(claimedChunk2);
                nbOfUnclaimedChunk++;
            }
        }
        if(nbOfUnclaimedChunk < minNbOfUnclaimedChunk){
            for(ClaimedChunk2 claimedChunk2 : allChunkFrom){
                NewClaimedChunkStorage.getInstance().unclaimChunk(claimedChunk2);
                nbOfUnclaimedChunk++;
                if(nbOfUnclaimedChunk >= minNbOfUnclaimedChunk)
                    break;
            }
        }


    }


    protected abstract void collectTaxes();

    public double getTaxOnRentingProperty() {
        if(propertyRentTax > 1)
            propertyRentTax = 1; //Convert to percentage
        return propertyRentTax;
    }

    public void addToRentTax(double value) {
        propertyRentTax += value;
    }
    public void setRentRate(double amount) {
        propertyRentTax = amount;
    }

    public double getTaxOnBuyingProperty() {
        if(propertyBuyTax > 1)
            propertyBuyTax = 1; //Convert to percentage
        return propertyBuyTax;
    }

    public void addToBuyTax(double value) {
        propertyBuyTax += value;
    }

    public void setBuyRate(double amount) {
        propertyBuyTax = amount;
    }


    public boolean isAtWar() {
        return !getCurrentAttacks().isEmpty();
    }



    public ChunkPermission getPermission(ChunkPermissionType type) {
        return getChunkSettings().getPermission(type);
    }

    public void nextPermission(ChunkPermissionType type) {
        getChunkSettings().nextPermission(type);
    }

    public boolean canTradeWith(TownData town) {
        return getRelationWith(town) != TownRelation.EMBARGO && getRelationWith(town) != TownRelation.WAR;
    }

    protected RankData getDefaultRank() {
        return getRank(getDefaultRankID());
    }

    protected void registerPlayer(PlayerData playerData) {
        getDefaultRank().addPlayer(playerData);
        playerData.setRankID(this, getDefaultRankID());
    }

    protected void unregisterPlayer(PlayerData playerData) {
        getRank(playerData).removePlayer(playerData);
        playerData.setRankID(this, null);
    }
}
