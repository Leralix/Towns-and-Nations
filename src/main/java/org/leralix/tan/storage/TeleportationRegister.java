package org.leralix.tan.storage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.TeleportationData;
import org.leralix.tan.dataclass.TeleportationPosition;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.HashMap;

/**
 * This class is used to register players that are teleporting to a location.
 */
public class TeleportationRegister {

    private TeleportationRegister() {
        throw new IllegalStateException("Utility class");
    }
    /**
     * This HashMap contains the player's ID and the TeleportationData object.
     */
    private static final HashMap<String, TeleportationData> spawnRegister = new HashMap<>();

    /**
     * This method is used to register a player to teleport to a town.
     * @param player    The player that is teleporting.
     * @param town      The town the player is teleporting to.
     */
    public static void registerSpawn(ITanPlayer player, TownData town){
        spawnRegister.put(player.getID(), new TeleportationData(town.getSpawn()));
    }
    public static void removePlayer(ITanPlayer player){
        spawnRegister.remove(player.getID());
    }
    public static boolean isPlayerRegistered(String playerID){
        return spawnRegister.containsKey(playerID);
    }
    public static boolean isPlayerRegistered(ITanPlayer player){
        return isPlayerRegistered(player.getID());
    }
    public static TeleportationData getTeleportationData(String playerID){
        return spawnRegister.get(playerID);
    }
    public static TeleportationData getTeleportationData(ITanPlayer tanPlayer){
        return getTeleportationData(tanPlayer.getID());
    }
    public static TeleportationData getTeleportationData(Player player){
        return getTeleportationData(player.getUniqueId().toString());
    }

    public static void teleportToTownSpawn(ITanPlayer tanPlayer, TownData townData){
        int secondBeforeTeleport = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("timeBeforeTeleport", 5);

        Player player = Bukkit.getPlayer(tanPlayer.getUUID());
        if(player == null)
            return;

        if(isPlayerRegistered(tanPlayer.getID())){
            player.sendMessage(TanChatUtils.getTANString() + Lang.WAIT_BEFORE_ANOTHER_TELEPORTATION.get());
            return;
        }
        if(secondBeforeTeleport > 0) {
            if (ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("cancelTeleportOnMovePosition", true)) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.TELEPORTATION_IN_X_SECONDS_NOT_MOVE.get(secondBeforeTeleport));
            } else {
                player.sendMessage(TanChatUtils.getTANString() + Lang.TELEPORTATION_IN_X_SECONDS.get(secondBeforeTeleport));
            }

            registerSpawn(tanPlayer, townData);
        }
        Bukkit.getScheduler().runTaskLater(TownsAndNations.getPlugin(),
                () -> confirmTeleportation(tanPlayer), secondBeforeTeleport * 20L);
    }
    public static void confirmTeleportation(ITanPlayer tanPlayer){

        if(!spawnRegister.containsKey(tanPlayer.getID())){
            return;
        }
        if(spawnRegister.get(tanPlayer.getID()).isCancelled()){
            removePlayer(tanPlayer);
            return;
        }

        TeleportationPosition teleportationPosition = spawnRegister.get(tanPlayer.getID()).getTeleportationPosition();

        Player player = Bukkit.getPlayer(tanPlayer.getUUID());
        if(player != null){
            teleportationPosition.teleport(player);
            SoundUtil.playSound(player, SoundEnum.MINOR_GOOD );
            player.sendMessage(TanChatUtils.getTANString() + Lang.SPAWN_TELEPORTED.get());
        }
        removePlayer(tanPlayer);


    }

}
