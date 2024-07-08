package org.tan.TownsAndNations.DataClass.territoryData;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.DataClass.ClaimedChunkSettings;
import org.tan.TownsAndNations.DataClass.PlayerData;

import java.util.Collection;

public interface ITerritoryData {

    String getID();
    String getName();
    void rename(Player player, int cost, String name);
    String getLeaderID();
    PlayerData getLeaderData();
    void setLeaderID(String leaderID);
    boolean isLeader(String playerID);
    String getDescription();
    void setDescription(String newDescription);
    ItemStack getIconItem();
    void setIconMaterial(Material material);
    Collection<String> getPlayerList();
    ClaimedChunkSettings getChunkSettings();
    boolean havePlayer(PlayerData playerData);
    boolean havePlayer(String playerID);

}
