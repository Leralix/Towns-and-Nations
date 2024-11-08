package org.leralix.tan.dataclass.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.tan.dataclass.*;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.wars.CurrentAttacks;
import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.enums.SoundEnum;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.newsletter.news.DiplomacyProposalNL;
import org.leralix.tan.newsletter.NewsletterStorage;
import org.leralix.tan.newsletter.news.JoinRegionProposalNL;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlannedAttackStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.*;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import java.util.*;
import java.util.function.Consumer;

import static org.leralix.tan.enums.SoundEnum.*;
import static org.leralix.tan.enums.TownRolePermission.KICK_PLAYER;
import static org.leralix.tan.utils.ChatUtils.getTANString;

public abstract class ITerritoryData {

//    private String ID;
//    private String name;
//    private String description;
//    private String leaderID;
//    private String overlordID;
//    private Long dateTimeCreated;
//    private String iconMaterial;
//    private int balance;
//    private TownRelations relations;
    Integer color;
    Integer defaultRankID;
    private Map<Integer, TownRank> ranks;
    private Collection<String> attackIncomingList;
    private Collection<String> currentAttackList;
    private HashMap<String, Integer> availableClaims;
    private Map<String, DiplomacyProposal> diplomacyProposals;
    List<String> overlordsProposals;

    protected ITerritoryData(){
        ranks = new HashMap<>();
        registerNewRank("default");

        attackIncomingList = new ArrayList<>();
        currentAttackList = new ArrayList<>();
        availableClaims = new HashMap<>();
        diplomacyProposals = new HashMap<>();
        overlordsProposals = new ArrayList<>();
    }

    public abstract String getID();
    public abstract String getName();
    public abstract int getHierarchyRank();
    public abstract String getColoredName();
    public abstract void rename(Player player, int cost, String name);
    public abstract String getLeaderID();
    public abstract PlayerData getLeaderData();
    public abstract void setLeaderID(String leaderID);
    public boolean isLeader(PlayerData playerData){
        return isLeader(playerData.getID());
    }
    public abstract boolean isLeader(String playerID);
    public abstract String getDescription();
    public abstract void setDescription(String newDescription);
    public abstract ItemStack getIconItem();
    public abstract void setIconMaterial(Material material);
    public abstract Collection<String> getPlayerIDList();
    public abstract Collection<PlayerData> getPlayerDataList();
    public abstract ClaimedChunkSettings getChunkSettings();
    public abstract boolean havePlayer(PlayerData playerData);
    public abstract boolean havePlayer(String playerID);
    public abstract TownRelations getRelations();
    public void setRelation(ITerritoryData otherTerritory, TownRelation relation){
        TownRelation actualRelation = getRelationWith(otherTerritory);
        if(relation.isSuperiorTo(actualRelation)){
            broadCastMessageWithSound(Lang.BROADCAST_RELATION_IMPROVE.get(getColoredName(), otherTerritory.getColoredName(),relation.getColoredName()), GOOD);
            otherTerritory.broadCastMessageWithSound(Lang.BROADCAST_RELATION_IMPROVE.get(otherTerritory.getColoredName(), getColoredName(),relation.getColoredName()), BAD);
        }
        else{
            broadCastMessageWithSound(Lang.BROADCAST_RELATION_WORSEN.get(getColoredName(), otherTerritory.getColoredName(),relation.getColoredName()), GOOD);
            otherTerritory.broadCastMessageWithSound(Lang.BROADCAST_RELATION_WORSEN.get(otherTerritory.getColoredName(), getColoredName(),relation.getColoredName()), BAD);
        }

        getRelations().setRelation(relation,otherTerritory);
        otherTerritory.getRelations().setRelation(relation,this);

        TeamUtils.updateAllScoreboardColor();
    }


    private Map<String, DiplomacyProposal> getDiplomacyProposals(){
        if(diplomacyProposals == null)
            diplomacyProposals = new HashMap<>();
        return diplomacyProposals;
    }

    public void removeDiplomaticProposal(ITerritoryData proposingTerritory){
        removeDiplomaticProposal(proposingTerritory.getID());
    }
    public void removeDiplomaticProposal(String proposingTerritoryID){
        getDiplomacyProposals().remove(proposingTerritoryID);
    }
    private void addDiplomaticProposal(ITerritoryData proposingTerritory, TownRelation wantedRelation){
        getDiplomacyProposals().put(proposingTerritory.getID(), new DiplomacyProposal(proposingTerritory.getID(), getID(), wantedRelation));
        NewsletterStorage.registerNewsletter(new DiplomacyProposalNL(proposingTerritory.getID(), getID(), wantedRelation));
    }

    public void receiveDiplomaticProposal(ITerritoryData proposingTerritory, TownRelation wantedRelation) {
        removeDiplomaticProposal(proposingTerritory);
        addDiplomaticProposal(proposingTerritory, wantedRelation);
    }

    public Collection<DiplomacyProposal> getAllDiplomacyProposal(){
        return getDiplomacyProposals().values();
    }

    public TownRelation getRelationWith(ITerritoryData territoryData){
        return getRelationWith(territoryData.getID());
    }
    public TownRelation getRelationWith(String territoryID){
        TownRelation relation = getRelations().getRelationWith(territoryID);

        if(relation != TownRelation.NEUTRAL)
            return relation;

        if(getID().equals(territoryID))
            return TownRelation.SELF;

        if(haveOverlord() && getOverlord().getID().equals(territoryID))
            return TownRelation.OVERLORD;

        if(getVassalsID().contains(territoryID))
            return TownRelation.VASSAL;
        
        return TownRelation.NEUTRAL;
    }

    public abstract void addToBalance(int balance);

    public abstract void removeFromBalance(int balance);

    public abstract void broadCastMessage(String message);

    public abstract void broadCastMessageWithSound(String message, SoundEnum soundEnum, boolean addPrefix);

    public abstract void broadCastMessageWithSound(String message, SoundEnum soundEnum);
    public abstract boolean haveNoLeader();

    public abstract ItemStack getIcon();
    public abstract ItemStack getIconWithInformations();
    public ItemStack getIconWithInformationAndRelation(ITerritoryData territoryData){
        ItemStack icon = getIconWithInformations();

        ItemMeta meta = icon.getItemMeta();
        if(meta != null){
            List<String> lore = meta.getLore();

            if(territoryData != null && lore != null){
                TownRelation relation = getRelationWith(territoryData);
                String relationName;
                if(relation == null){
                    relationName = Lang.GUI_TOWN_RELATION_NEUTRAL.get();
                }
                else {
                    relationName = relation.getColor() + relation.getName();
                }
                lore.add(Lang.GUI_TOWN_INFO_TOWN_RELATION.get(relationName));
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
    public Collection<CurrentAttacks> getCurrentAttacks(){
        Collection<CurrentAttacks> res = new ArrayList<>();
        for(String attackID : getCurrentAttacksID()){
            CurrentAttacks attackInvolved = CurrentAttacksStorage.get(attackID);
            res.add(attackInvolved);
        }
        return res;
    }

    public void addCurrentAttack(CurrentAttacks currentAttacks){
        getAttacksInvolvedID().add(currentAttacks.getId());

    }
    public void removeCurrentAttack(CurrentAttacks currentAttacks){
        getAttacksInvolvedID().remove(currentAttacks.getId());
    }

    public abstract boolean atWarWith(String territoryID);


    public abstract int getBalance();

    public abstract ITerritoryData getOverlord();
    public abstract void removeOverlord();
    public void setOverlord(ITerritoryData overlord){
        getOverlordsProposals().remove(overlord.getID());
        broadCastMessageWithSound(getTANString() + Lang.TOWN_ACCEPTED_REGION_DIPLOMATIC_INVITATION.get(this.getColoredName(), overlord.getColoredName()), GOOD);
        setOverlordPrivate(overlord);
    }
    protected abstract void setOverlordPrivate(ITerritoryData newOverlord);

    public void addVassal(ITerritoryData vassal){
        NewsletterStorage.removeVassalisationProposal(this, vassal);
        broadCastMessageWithSound(getTANString() + Lang.TOWN_ACCEPTED_REGION_DIPLOMATIC_INVITATION.get(vassal.getColoredName(), getColoredName()), GOOD);
        addVassalPrivate(vassal);
    }
    protected abstract void addVassalPrivate (ITerritoryData vassal);
    public void removeVassal(ITerritoryData territoryToRemove){
        removeVassal(territoryToRemove.getID());
    }
    public abstract void removeVassal(String townID);

    public abstract boolean isCapital();

    public ITerritoryData getCapital(){
        return TerritoryUtil.getTerritory(getCapitalID());
    }
    public abstract String getCapitalID();


    public abstract int getChildColorCode();

    public int getChunkColorCode(){
        if(color == null)
            return getChildColorCode();
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

    public abstract boolean haveOverlord();


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
        Chunk chunk = player.getLocation().getChunk();
        claimChunk(player, chunk);
    }

    public abstract void claimChunk(Player player,Chunk chunk);


    public void castActionToAllPlayers(Consumer<Player> action){
        for(PlayerData playerData : getPlayerDataList()){
            Player player = playerData.getPlayer();
            if(player != null)
                action.accept(player);
        }
    }

    public void delete(){
        NewClaimedChunkStorage.unclaimAllChunksFromTerritory(this); //Unclaim all chunk from town

        castActionToAllPlayers(HumanEntity::closeInventory);

        if(haveOverlord())
            getOverlord().removeVassal(this);

        for(ITerritoryData territory : getVassals()){
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

    public void addDonation(Player player, Integer amount) {
        int playerBalance = EconomyUtil.getBalance(player);

        if(playerBalance < amount ){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NOT_ENOUGH_MONEY.get());
            return;
        }
        if(amount <= 0 ){
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_NEED_1_OR_ABOVE.get());
            return;
        }


        EconomyUtil.removeFromBalance(player,amount);
        addToBalance(amount);

        //getDonationHistory().add(player.getName(),player.getUniqueId().toString(),amount); TODO : Add History to region

        player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_SEND_MONEY_SUCCESS.get(amount, getColoredName()));
        SoundUtil.playSound(player, MINOR_LEVEL_UP);
    }

    public abstract void openMainMenu(Player player);

    public abstract boolean canHaveVassals();
    public abstract boolean canHaveOverlord();

    public abstract List<String> getVassalsID();
    public List<ITerritoryData> getVassals(){
        List<ITerritoryData> res = new ArrayList<>();
        for(String vassalID : getVassalsID()){
            ITerritoryData vassal = TerritoryUtil.getTerritory(vassalID);
            if(vassal != null)
                res.add(vassal);
        }
        return res;
    }
    public int getVassalCount(){
        return getVassalsID().size();
    }

    public boolean isVassal(ITerritoryData territoryData) {
        return isVassal(territoryData.getID());
    }
    public abstract boolean isVassal(String territoryID);

    public boolean isCapitalOf(ITerritoryData territoryData) {
        return isCapitalOf(territoryData.getID());
    }
    public abstract boolean isCapitalOf(String territoryID);
    public abstract boolean isLeaderOnline();

    public abstract Collection<ITerritoryData> getPotentialVassals();

    private List<String> getOverlordsProposals(){
        if(overlordsProposals == null)
            overlordsProposals = new ArrayList<>();
        return overlordsProposals;
    }

    public void addVassalisationProposal(ITerritoryData proposal){
        getOverlordsProposals().add(proposal.getID());
        broadCastMessageWithSound(Lang.REGION_DIPLOMATIC_INVITATION_RECEIVED_1.get(proposal.getColoredName(), getColoredName()), MINOR_GOOD);
        NewsletterStorage.registerNewsletter(new JoinRegionProposalNL(proposal, this));
    }

    public void removeVassalisationProposal(ITerritoryData proposal){
        getOverlordsProposals().remove(proposal.getID());
    }

    public boolean containsVassalisationProposal(ITerritoryData proposal){
        return getOverlordsProposals().contains(proposal.getID());
    }

    public int getNumberOfVassalisationProposals(){
        return getOverlordsProposals().size();
    }

    public List<GuiItem> getAllSubjugationProposals(Player player, int page){
        ArrayList<GuiItem> proposals = new ArrayList<>();
        for(String proposalID : getOverlordsProposals()) {
            ITerritoryData proposalOverlord = TerritoryUtil.getTerritory(proposalID);
            if (proposalOverlord == null)
                continue;
            ItemStack territoryItem = proposalOverlord.getIconWithInformations();
            HeadUtils.addLore(territoryItem, Lang.LEFT_CLICK_TO_ACCEPT.get(), Lang.RIGHT_CLICK_TO_REFUSE.get());
            GuiItem acceptInvitation = ItemBuilder.from(territoryItem).asGuiItem(event -> {
                event.setCancelled(true);
                if(event.isLeftClick()){
                    setOverlord(proposalOverlord);
                    proposalOverlord.addVassal(this);
                    broadCastMessageWithSound(getTANString() + Lang.TOWN_ACCEPTED_REGION_DIPLOMATIC_INVITATION.get(this.getColoredName(), proposalOverlord.getName()), GOOD);
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

    protected Map<Integer, TownRank> getRanks(){
        if(ranks == null) {
            ranks = new HashMap<>();
            registerNewRank("default");
        }
        return ranks;
    }
    public Collection<TownRank> getAllRanks(){
        return getRanks().values();
    }

    public TownRank getRank(int rankID){
        return getRanks().get(rankID);
    }
    public abstract TownRank getRank(PlayerData playerData);
    public int getNumberOfRank(){
        return getRanks().size();
    }

    public boolean isRankNameUsed(String message) {
        if(ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("AllowNameDuplication",false))
            return false;

        for (TownRank rank : getAllRanks()) {
            if (rank.getName().equals(message)) {
                return true;
            }
        }
        return false;
    }

    public TownRank registerNewRank(String rankName){
        int nextRankId = 0;
        for(TownRank rank : getAllRanks()){
            if(rank.getID() >= nextRankId)
                nextRankId = rank.getID() + 1;
        }

        TownRank newRank = new TownRank(nextRankId, rankName);
        getRanks().put(nextRankId,newRank);
        return newRank;
    }

    public void removeRank(int key){
        getRanks().remove(key);
    }

    public int getTownDefaultRankID() {
        if(defaultRankID == null){
            for(TownRank rank : getAllRanks()) {
                defaultRankID = rank.getID();
                return defaultRankID;
            }
        }
            defaultRankID = 0;
        return defaultRankID;
    }

    public void setDefaultRank(int rankID) {
        this.defaultRankID = rankID;
    }

    public abstract List<GuiItem> getMemberList(PlayerData playerData);
}
