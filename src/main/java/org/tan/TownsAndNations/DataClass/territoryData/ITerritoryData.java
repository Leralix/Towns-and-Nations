package org.tan.TownsAndNations.DataClass.territoryData;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.DataClass.wars.PlannedAttack;
import org.tan.TownsAndNations.DataClass.ClaimedChunkSettings;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownRelations;
import org.tan.TownsAndNations.DataClass.wars.CurrentAttacks;
import org.tan.TownsAndNations.enums.SoundEnum;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.CurrentAttacksStorage;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.PlannedAttackStorage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

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
    private Collection<String> attackIncomingList = new ArrayList<>();
    private Collection<String> currentAttackList = new ArrayList<>();
    private HashMap<String, Integer> availableClaims;

    public ITerritoryData(){
        availableClaims = new HashMap<>();
    }

    public abstract String getID();
    public abstract String getName();
    public abstract int getRank();
    public abstract String getColoredName();
    public abstract void rename(Player player, int cost, String name);
    public abstract String getLeaderID();
    public abstract PlayerData getLeaderData();
    public abstract void setLeaderID(String leaderID);
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
    public abstract void addRelation(TownRelation relation, ITerritoryData territoryData);
    public abstract void addRelation(TownRelation relation, String territoryID);
    public  abstract void removeRelation(TownRelation relation, ITerritoryData territoryData);
    public abstract void removeRelation(TownRelation relation, String territoryID);
    public abstract TownRelation getRelationWith(ITerritoryData iRelation);
    public abstract TownRelation getRelationWith(String territoryID);

    public abstract void addToBalance(int balance);

    public abstract void removeFromBalance(int balance);

    public abstract void broadCastMessage(String message);

    public abstract void broadCastMessageWithSound(String message, SoundEnum soundEnum, boolean addPrefix);

    public abstract void broadCastMessageWithSound(String message, SoundEnum soundEnum);
    public abstract boolean haveNoLeader();

    public abstract ItemStack getIcon();
    public abstract ItemStack getIconWithInformations();
    public abstract ItemStack getIconWithInformationAndRelation(ITerritoryData territoryData);

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
        getAttacksInvolvedID().add(currentAttacks.getID());

    }
    public void removeCurrentAttack(CurrentAttacks currentAttacks){
        getAttacksInvolvedID().remove(currentAttacks.getID());
    }

    public abstract boolean atWarWith(String territoryID);


    public abstract int getBalance();

    public abstract ITerritoryData getOverlord();
    public abstract void removeOverlord();
    public abstract void setOverlord(ITerritoryData overlord);

    public abstract void addSubject(ITerritoryData territoryToAdd);
    public abstract void removeSubject(ITerritoryData territoryToRemove);
    public abstract void removeSubject(String townID);
    public abstract List<String> getSubjectsID();
    public abstract List<ITerritoryData> getSubjects();

    public abstract boolean isCapital();

    public abstract ITerritoryData getCapital();

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


    public HashMap<String, Integer> getAvailableEnemyClaims() {
        if(availableClaims == null)
            return availableClaims = new HashMap<>();
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
            getOverlord().removeSubject(this);

        for(ITerritoryData territory : getSubjects()){
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
}
