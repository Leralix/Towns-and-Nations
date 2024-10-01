package org.tan.TownsAndNations.storage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TeleportationData;
import org.tan.TownsAndNations.DataClass.TeleportationPosition;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.enums.SoundEnum;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;
import org.tan.TownsAndNations.utils.SoundUtil;

import java.util.HashMap;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

/**
 * This class is used to register players that are teleporting to a location.
 */
public class TeleportationRegister {
    /**
     * This HashMap contains the player's ID and the TeleportationData object.
     */
    private static final HashMap<String, TeleportationData> spawnRegister = new HashMap<>();

    /**
     * This method is used to register a player to teleport to a town.
     * @param player    The player that is teleporting.
     * @param town      The town the player is teleporting to.
     */
    public static void registerSpawn(PlayerData player, TownData town){
        spawnRegister.put(player.getID(), new TeleportationData(town.getSpawn()));
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

    public static void teleportToTownSpawn(PlayerData playerData, TownData townData){
        int secondBeforeTeleport = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("timeBeforeTeleport", 5);

        Player player = Bukkit.getPlayer(playerData.getUUID());
        if(player == null)
            return;

        if(isPlayerRegistered(playerData.getID())){
            player.sendMessage(getTANString() + Lang.WAIT_BEFORE_ANOTHER_TELEPORTATION.get());
            return;
        }
        if(secondBeforeTeleport > 0)
            player.sendMessage(getTANString() +Lang.TELEPORTATION_IN_X_SECONDS_NOT_MOVE.get(secondBeforeTeleport));

        registerSpawn(playerData, townData);


        Bukkit.getScheduler().runTaskLater(TownsAndNations.getPlugin(),
                () -> confirmTeleportation(playerData), secondBeforeTeleport * 20L);
    }
    public static void confirmTeleportation(PlayerData playerData){

        if(!spawnRegister.containsKey(playerData.getID())){
            return;
        }
        if(spawnRegister.get(playerData.getID()).isCancelled()){
            removePlayer(playerData);
            return;
        }

        TeleportationPosition teleportationPosition = spawnRegister.get(playerData.getID()).getTeleportationPosition();

        Player player = Bukkit.getPlayer(playerData.getUUID());
        if(player != null){
            teleportationPosition.teleport(player);
            SoundUtil.playSound(player, SoundEnum.MINOR_GOOD );
            player.sendMessage(getTANString() + Lang.SPAWN_TELEPORTED.get());
        }
        removePlayer(playerData);


    }

}
