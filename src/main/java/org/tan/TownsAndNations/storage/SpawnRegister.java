package org.tan.TownsAndNations.storage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.enums.SoundEnum;
import org.tan.TownsAndNations.utils.ConfigUtil;
import org.tan.TownsAndNations.utils.SoundUtil;

import java.util.HashMap;
import java.util.UUID;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class SpawnRegister {

    private static final HashMap<String, String> spawnRegister = new HashMap<>();


    public static void registerSpawn(PlayerData player, TownData town){
        spawnRegister.put(player.getID(), town.getID());
    }
    public static void removePlayer(PlayerData player){
        spawnRegister.remove(player.getID());
    }
    public static boolean isPlayerRegistered(PlayerData player){
        return spawnRegister.containsKey(player.getID());
    }

    public static void teleportPlayerToSpawn(PlayerData playerData, TownData townData){
        int secondBeforeTeleport = ConfigUtil.getCustomConfig("config.yml").getInt("timeBeforeTeleport", 5);

        if(secondBeforeTeleport <= 0){
            confirmTeleportation(playerData, townData);
            return;
        }

        Player player = Bukkit.getPlayer(playerData.getUUID());
        player.sendMessage(Lang.TELEPORTATION_IN_X_SECONDS_NOT_MOVE.get(secondBeforeTeleport));
        registerSpawn(playerData, townData);
        Bukkit.getScheduler().runTaskLater(TownsAndNations.getPlugin(),
                () -> confirmTeleportation(playerData, townData), secondBeforeTeleport * 20L);
    }

    public static void confirmTeleportation(PlayerData playerData, TownData townData){
        if(!spawnRegister.containsKey(playerData.getID()))
            return;
        if(!spawnRegister.get(playerData.getID()).equals(townData.getID()))
            return;
        townData.teleportPlayerToSpawn(playerData);

        Player player = Bukkit.getPlayer(playerData.getUUID());
        if(player == null)
            return;
        SoundUtil.playSound(player, SoundEnum.MINOR_GOOD );
        player.sendMessage(getTANString() + Lang.SPAWN_TELEPORTED.get());

        removePlayer(playerData);
    }


}
