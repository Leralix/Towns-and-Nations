package org.tan.TownsAndNations.DataClass.territoryData;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.DataClass.wars.AttackInvolved;
import org.tan.TownsAndNations.DataClass.ClaimedChunkSettings;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownRelations;
import org.tan.TownsAndNations.DataClass.wars.CurrentAttacks;
import org.tan.TownsAndNations.enums.SoundEnum;
import org.tan.TownsAndNations.enums.TownRelation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
//    private final Collection<String> attackIncomingList = new ArrayList<>();
//    private final Collection<String> currentAttackList = new ArrayList<>();


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

    public abstract Collection<String> getAttacksInvolvedID();
    public abstract Collection<AttackInvolved> getAttacksInvolved();
    public abstract void addPlannedAttack(AttackInvolved war);
    public abstract void removePlannedAttack(AttackInvolved war);


    public abstract Collection<String> getCurrentAttacksID();
    public abstract Collection<CurrentAttacks> getCurrentAttacks();

    public abstract void addCurrentAttack(CurrentAttacks currentAttacks);
    public abstract void removeCurrentAttack(CurrentAttacks currentAttacks);

    public abstract boolean atWarWith(String territoryID);


    public abstract int getBalance();

    public abstract ITerritoryData getOverlord();
    public abstract void removeOverlord();

    public abstract void removeSubject(ITerritoryData territoryToRemove);
    public abstract void removeSubject(String townID);
    public abstract List<String> getSubjectsID();
    public abstract List<ITerritoryData> getSubjects();

    public abstract boolean isCapital();

    public abstract ITerritoryData getCapital();

    public abstract int getChunkColor();

    public abstract String getChunkColorInHex();

    public abstract void setChunkColor(int color);

    public abstract boolean haveOverlord();
}
