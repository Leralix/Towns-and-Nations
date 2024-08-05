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

import java.util.Collection;

public interface ITerritoryData {

    String getID();
    String getName();
    String getColoredName();
    void rename(Player player, int cost, String name);
    String getLeaderID();
    PlayerData getLeaderData();
    void setLeaderID(String leaderID);
    boolean isLeader(String playerID);
    String getDescription();
    void setDescription(String newDescription);
    ItemStack getIconItem();
    void setIconMaterial(Material material);
    Collection<String> getPlayerIDList();
    Collection<PlayerData> getPlayerDataList();
    ClaimedChunkSettings getChunkSettings();
    boolean havePlayer(PlayerData playerData);
    boolean havePlayer(String playerID);
    TownRelations getRelations();
    void addRelation(TownRelation relation, ITerritoryData territoryData);
    void addRelation(TownRelation relation, String territoryID);
    void removeRelation(TownRelation relation, ITerritoryData territoryData);
    void removeRelation(TownRelation relation, String territoryID);
    TownRelation getRelationWith(ITerritoryData iRelation);
    TownRelation getRelationWith(String territoryID);

    void broadCastMessage(String message);

    void broadCastMessageWithSound(String message, SoundEnum soundEnum, boolean addPrefix);

    void broadCastMessageWithSound(String message, SoundEnum soundEnum);
    boolean haveNoLeader();

    ItemStack getIcon();
    ItemStack getIconWithInformations();
    ItemStack getIconWithInformationAndRelation(ITerritoryData territoryData);

    Collection<String> getAttacksInvolvedID();
    Collection<AttackInvolved> getAttacksInvolved();
    void addPlannedAttack(AttackInvolved war);
    void removePlannedAttack(AttackInvolved war);


    Collection<String> getCurrentAttacksID();
    Collection<CurrentAttacks> getCurrentAttacks();

    void addCurrentAttack(CurrentAttacks currentAttacks);
    void removeCurrentAttack(CurrentAttacks currentAttacks);

    boolean atWarWith(String territoryID);
}
