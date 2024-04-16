package org.tan.TownsAndNations.storage;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.DataClass.Vector3D;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.Legacy.ClaimedChunkStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerSelectPropertyPositionStorage {

    private static final Map<String, List<Vector3D>> playerList = new HashMap<>();

    public static boolean contains(Player player){
        return contains(player.getUniqueId().toString());
    }
    public static boolean contains(PlayerData playerData){
        return contains(playerData.getID());
    }
    public static boolean contains(String playerID){
        return playerList.containsKey(playerID);
    }
    public static void addPlayer(String playerID){
        playerList.put(playerID, new ArrayList<>());
    }

    public static void addPlayer(PlayerData playerData){
        addPlayer(playerData.getID());
    }
    public static void removePlayer(String playerID){
        playerList.remove(playerID);
    }

    public static void addPoint(Player player, Block block){
        String playerID = player.getUniqueId().toString();
        PlayerData playerData = PlayerDataStorage.get(playerID);
        TownData playerTown = playerData.getTown();

        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.get(block.getChunk());
        if(claimedChunk == null){
            player.sendMessage("Not in town claimed chunks");
        }

        List<Vector3D> vList = playerList.get(playerID);
        if(vList.isEmpty()){
            Vector3D vector3D = new Vector3D(block.getX(), block.getY(), block.getZ());
            vList.add(vector3D);
            player.sendMessage("First point set in " + vector3D);
        }
        else if(vList.size() == 1){
            Vector3D vector3D = new Vector3D(block.getX(), block.getY(), block.getZ());
            vList.add(vector3D);
            player.sendMessage("Second point set in " + vector3D);
            player.sendMessage("Property register");

            playerTown.registerNewProperty(vList.get(0),vList.get(1),playerData);
        }
    }


}
