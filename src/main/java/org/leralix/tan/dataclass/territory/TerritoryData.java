package org.leralix.tan.dataclass.territory;


import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.position.Vector3D;
import org.leralix.lib.utils.RandomUtil;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.building.Building;
import org.leralix.tan.dataclass.*;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.dataclass.newhistory.ChunkPaymentHistory;
import org.leralix.tan.dataclass.newhistory.MiscellaneousHistory;
import org.leralix.tan.dataclass.newhistory.PlayerDonationHistory;
import org.leralix.tan.dataclass.newhistory.SalaryPaymentHistory;
import org.leralix.tan.dataclass.territory.cosmetic.CustomIcon;
import org.leralix.tan.dataclass.territory.cosmetic.ICustomIcon;
import org.leralix.tan.dataclass.territory.cosmetic.PlayerHeadIcon;
import org.leralix.tan.dataclass.territory.economy.Budget;
import org.leralix.tan.dataclass.territory.economy.ChunkUpkeepLine;
import org.leralix.tan.dataclass.territory.economy.SalaryPaymentLine;
import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.dataclass.wars.CurrentAttack;
import org.leralix.tan.dataclass.wars.CurrentWar;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.DiplomacyProposalAcceptedInternalEvent;
import org.leralix.tan.events.events.DiplomacyProposalInternalEvent;
import org.leralix.tan.events.events.TerritoryVassalAcceptedInternalEvent;
import org.leralix.tan.events.events.TerritoryVassalProposalInternalEvent;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.stored.CurrentWarStorage;
import org.leralix.tan.storage.stored.FortStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.*;
import org.leralix.tan.war.fort.Fort;

import java.util.*;
import java.util.function.Consumer;

public abstract class TerritoryData {

    private String id;
    private String name;
    private String description;
    protected String overlordID;
    private Double treasury;
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
    private List<String> fortIds;
    private List<String> occupiedFortIds;

    protected TerritoryData(String id, String name, ITanPlayer owner){
        this.id = id;
        this.name = name;
        this.description = Lang.DEFAULT_DESCRIPTION.get();
        this.dateTimeCreated = new Date().getTime();

        this.customIcon = new PlayerHeadIcon(owner);

        this.treasury = 0.0;
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

    public abstract String getBaseColoredName();

    public TextComponent getCustomColoredName(){
        TextComponent coloredName = new TextComponent(getName());
        coloredName.setColor(getChunkColor());
        return coloredName;
    }
    public abstract String getLeaderID();
    public abstract ITanPlayer getLeaderData();
    public abstract void setLeaderID(String leaderID);
    public boolean isLeader(ITanPlayer tanPlayer){
        return isLeader(tanPlayer.getID());
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
            if(haveNoLeader()){
                customIcon = new CustomIcon(new ItemStack(Material.BARRIER));
            }
            else {
                customIcon = new PlayerHeadIcon(getLeaderID());
            }
        }
        return customIcon.getIcon();
    }
    public void setIcon(ICustomIcon icon){
        this.customIcon = icon;
    }

    public abstract Collection<String> getPlayerIDList();

    public boolean isPlayerIn(ITanPlayer tanPlayer){
        return isPlayerIn(tanPlayer.getID());
    }

    public boolean isPlayerIn(Player player){
        return isPlayerIn(player.getUniqueId().toString());
    }

    public boolean isPlayerIn(String playerID){
        return getPlayerIDList().contains(playerID);
    }

    public Collection<String> getOrderedPlayerIDList(){
        List<String> sortedList = new ArrayList<>();
        List<ITanPlayer> ITanPlayerSorted = getITanPlayerList().stream()
                .sorted(Comparator.comparingInt(tanPlayer -> -this.getRank(tanPlayer.getRankID(this)).getLevel()))
                .toList();

        for(ITanPlayer tanPlayer : ITanPlayerSorted){
            sortedList.add(tanPlayer.getID());
        }
        return sortedList;
    }
    public abstract Collection<ITanPlayer> getITanPlayerList();

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

        EventManager.getInstance().callEvent(new DiplomacyProposalAcceptedInternalEvent(otherTerritory, this, actualRelation, relation));

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
        EventManager.getInstance().callEvent(new DiplomacyProposalInternalEvent(this, proposingTerritory, wantedRelation));
        getDiplomacyProposals().put(proposingTerritory.getID(), new DiplomacyProposal(proposingTerritory.getID(), getID(), wantedRelation));
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

        Optional<TerritoryData> overlord = getOverlord();
        if(overlord.isPresent() && overlord.get().getID().equals(territoryID))
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
    public Collection<CurrentWar> getAttacksInvolved(){
        Collection<CurrentWar> res = new ArrayList<>();
        for(String attackID : getAttacksInvolvedID()){
            CurrentWar plannedAttack = CurrentWarStorage.get(attackID);
            res.add(plannedAttack);
        }
        return res;
    }
    public void addPlannedAttack(CurrentWar war){
        getAttacksInvolvedID().add(war.getID());

    }
    public void removePlannedAttack(CurrentWar war){
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
        getAttacksInvolvedID().add(currentAttacks.getAttackData().getID());

    }
    public void removeCurrentAttack(CurrentAttack currentAttacks){
        getAttacksInvolvedID().remove(currentAttacks.getAttackData().getID());
    }

    public abstract boolean atWarWith(String territoryID);



    public double getBalance(){
        if(treasury == null)
            treasury = getOldBalance();
        return treasury;
    }

    public void addToBalance(double balance) {
        this.treasury += balance;
    }

    public void removeFromBalance(double balance) {
        this.treasury -= balance;
    }

    protected abstract double getOldBalance();


    public void setOverlord(TerritoryData overlord){
        getOverlordsProposals().remove(overlord.getID());
        broadcastMessageWithSound(Lang.ACCEPTED_VASSALISATION_PROPOSAL_ALL.get(this.getBaseColoredName(), overlord.getBaseColoredName()), SoundEnum.GOOD);

        this.overlordID = overlord.getID();
        overlord.addVassal(this);
    }

    public Optional<TerritoryData> getOverlord(){
        if(overlordID == null)
            return Optional.empty();
        TerritoryData overlord = TerritoryUtil.getTerritory(overlordID);
        if(overlord == null){
            overlordID = null;
            return Optional.empty();
        }
        return Optional.of(overlord);

    }

    /**
     * @return All potential overlords of this territory (Kingdom and region)
     */
    protected abstract Collection<TerritoryData> getOverlords();

    public void removeOverlord(){
        getOverlord().ifPresent(overlord -> {
            overlord.removeVassal(this);
            removeOverlordPrivate();
            this.overlordID = null;
        });
    }
    public abstract void removeOverlordPrivate();


    public void addVassal(TerritoryData vassal){

        EventManager.getInstance().callEvent(new TerritoryVassalAcceptedInternalEvent(vassal, this));
        addVassalPrivate(vassal);
    }
    protected abstract void addVassalPrivate (TerritoryData vassal);

    protected abstract void removeVassal(TerritoryData vassalID);

    public boolean isCapital(){
        Optional<TerritoryData> capital = getOverlord();
        return capital
                .map(overlord -> Objects.equals(overlord.getCapitalID(), getID()))
                .orElse(false);
    }

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
        return getOverlord().isPresent();
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

    public abstract void claimChunk(Player player, Chunk chunk);


    public void castActionToAllPlayers(Consumer<Player> action){
        for(ITanPlayer tanPlayer : getITanPlayerList()){
            Player player = tanPlayer.getPlayer();
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

        for(Fort occupiedFort : getOccupiedForts()){
            occupiedFort.liberate();
        }

        for(Fort ownedFort : getOwnedForts()){
            FortStorage.getInstance().delete(ownedFort);
        }

        getRelations().cleanAll(this);   //Cancel all Relation between the deleted territory and other territories
        CurrentWarStorage.territoryDeleted(this);
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
        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_SEND_MONEY_SUCCESS.get(amount, getBaseColoredName()));
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
        return territoryData.getOverlord()
                .map(overlord -> Objects.equals(overlord.getCapitalID(), getID()))
                .orElse(false);
    }


    public abstract Collection<TerritoryData> getPotentialVassals();

    private List<String> getOverlordsProposals(){
        if(overlordsProposals == null)
            overlordsProposals = new ArrayList<>();
        return overlordsProposals;
    }

    public void addVassalisationProposal(TerritoryData proposal){
        getOverlordsProposals().add(proposal.getID());
        broadcastMessageWithSound(Lang.REGION_DIPLOMATIC_INVITATION_RECEIVED_1.get(proposal.getBaseColoredName(), getBaseColoredName()), SoundEnum.MINOR_GOOD);
        EventManager.getInstance().callEvent(new TerritoryVassalProposalInternalEvent(proposal, this));
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
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

        for(String proposalID : getOverlordsProposals()) {
            TerritoryData proposalOverlord = TerritoryUtil.getTerritory(proposalID);
            if (proposalOverlord == null)
                continue;
            ItemStack territoryItem = proposalOverlord.getIconWithInformations(tanPlayer.getLang());
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
                    broadcastMessageWithSound(Lang.ACCEPTED_VASSALISATION_PROPOSAL_ALL.get(this.getBaseColoredName(), proposalOverlord.getName()), SoundEnum.GOOD);
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
    public abstract RankData getRank(ITanPlayer tanPlayer);

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

    public abstract List<GuiItem> getOrderedMemberList(ITanPlayer tanPlayer);


    public boolean doesPlayerHavePermission(Player player, RolePermission townRolePermission) {
        return doesPlayerHavePermission(PlayerDataStorage.getInstance().get(player), townRolePermission);
    }
    public boolean doesPlayerHavePermission(ITanPlayer tanPlayer, RolePermission townRolePermission) {

        if(!this.isPlayerIn(tanPlayer)){
            return false;
        }

        if(isLeader(tanPlayer))
            return true;

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
                ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(playerId);
                EconomyUtil.addFromBalance(tanPlayer, rankSalary);
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


        Collection<TerritoryChunk> allChunkFrom = NewClaimedChunkStorage.getInstance().getAllChunkFrom(this);
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

    protected void registerPlayer(ITanPlayer tanPlayer) {
        getDefaultRank().addPlayer(tanPlayer);
        tanPlayer.setRankID(this, getDefaultRankID());
    }

    protected void unregisterPlayer(ITanPlayer tanPlayer) {
        getRank(tanPlayer).removePlayer(tanPlayer);
        tanPlayer.setRankID(this, null);
    }


    public String getColoredName() {
        if(Constants.displayTerritoryColor()){
            return getCustomColoredName().getText();
        }
        else {
            return getBaseColoredName();
        }
    }

    public String getLeaderName() {
        if (this.haveNoLeader())
            return Lang.NO_LEADER.get();
        return getLeaderData().getNameStored();
    }

    public void registerFort(Vector3D location) {
        Fort fort = FortStorage.getInstance().register(location,this);
        getOwnedFortIDs().add(fort.getID());
    }

    public List<String> getOwnedFortIDs() {
        if(fortIds == null)
            fortIds = new ArrayList<>();
        return fortIds;
    }

    public List<String> getOccupiedFortIds() {
        if(occupiedFortIds == null)
            occupiedFortIds = new ArrayList<>();
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
        List<Building> buildings = new ArrayList<>();
        buildings.addAll(getOwnedForts());

        buildings.removeAll(Collections.singleton(null));
        return buildings;
    }
}
