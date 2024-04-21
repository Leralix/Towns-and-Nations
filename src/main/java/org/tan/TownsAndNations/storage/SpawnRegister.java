package org.tan.TownsAndNations.storage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TeleportationData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.enums.SoundEnum;
import org.tan.TownsAndNations.utils.ConfigUtil;
import org.tan.TownsAndNations.utils.SoundUtil;

import java.util.HashMap;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class SpawnRegister {

    private static final HashMap<String, TeleportationData> spawnRegister = new HashMap<>();


    public static void registerSpawn(PlayerData player, TownData town){
        spawnRegister.put(player.getID(), new TeleportationData(town));
    }
    public static void removePlayer(PlayerData player){
        spawnRegister.remove(player.getID());
    }
    public static boolean isPlayerRegistered(String playerID){
        return spawnRegister.containsKey(playerID);
    }
    public static boolean isPlayerRegistered(PlayerData player){
        return isPlayerRegistered(player.getID());
    }
    public static TeleportationData getTeleportationData(String playerID){
        return spawnRegister.get(playerID);
    }
    public static TeleportationData getTeleportationData(PlayerData playerData){
        return getTeleportationData(playerData.getID());
    }
    public static TeleportationData getTeleportationData(Player player){
        return getTeleportationData(player.getUniqueId().toString());
    }

    public static void teleportPlayerToSpawn(PlayerData playerData, TownData townData){
        int secondBeforeTeleport = ConfigUtil.getCustomConfig("config.yml").getInt("timeBeforeTeleport", 5);

        if(secondBeforeTeleport <= 0){ //Instant teleportation
            confirmTeleportation(playerData, townData);
            return;
        }

        Player player = Bukkit.getPlayer(playerData.getUUID());
        if(player == null)
            return;

        if(isPlayerRegistered(playerData.getID())){
            player.sendMessage(getTANString() + Lang.WAIT_BEFORE_ANOTHER_TELEPORTATION.get());
            return;
        }

        player.sendMessage(getTANString() +Lang.TELEPORTATION_IN_X_SECONDS_NOT_MOVE.get(secondBeforeTeleport));
        registerSpawn(playerData, townData);
        Bukkit.getScheduler().runTaskLater(TownsAndNations.getPlugin(),
                () -> confirmTeleportation(playerData, townData), secondBeforeTeleport * 20L);
    }
    public static void confirmTeleportation(PlayerData playerData, TownData townData){

        if(!spawnRegister.containsKey(playerData.getID())){
            return;
        }
        if(spawnRegister.get(playerData.getID()).isCancelled()){
            removePlayer(playerData);
            return;
        }
        removePlayer(playerData);
        townData.teleportPlayerToSpawn(playerData);

        Player player = Bukkit.getPlayer(playerData.getUUID());
        if(player != null){
            SoundUtil.playSound(player, SoundEnum.MINOR_GOOD );
            player.sendMessage(getTANString() + Lang.SPAWN_TELEPORTED.get());
        }

    }

}
