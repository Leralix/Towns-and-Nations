package org.leralix.tan.storage;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.chunk.IClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunk;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.teleportation.PlannedTeleportation;
import org.leralix.tan.data.territory.teleportation.TeleportationData;
import org.leralix.tan.data.upgrade.rewards.bool.EnableTownSpawn;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.HashMap;
import java.util.UUID;

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
    private static final HashMap<UUID, PlannedTeleportation> spawnRegister = new HashMap<>();

    /**
     * This method is used to register a player to teleport to a town.
     *
     * @param player        The player that is teleporting.
     * @param position      The location the player is teleporting to.
     */
    public static void registerSpawn(ITanPlayer player, Location position) {
        spawnRegister.put(player.getID(), new PlannedTeleportation(position));
    }

    public static void removePlayer(ITanPlayer player) {
        spawnRegister.remove(player.getID());
    }

    public static boolean isPlayerRegistered(UUID playerID) {
        return spawnRegister.containsKey(playerID);
    }

    public static PlannedTeleportation getTeleportationData(UUID playerID) {
        return spawnRegister.get(playerID);
    }

    public static PlannedTeleportation getTeleportationData(ITanPlayer tanPlayer) {
        return getTeleportationData(tanPlayer.getID());
    }

    public static PlannedTeleportation getTeleportationData(Player player) {
        return getTeleportationData(player.getUniqueId());
    }

    public static void teleportToTerritory(Player player, ITanPlayer tanPlayer, Territory territoryData) {
        LangType langType = tanPlayer.getLang();

        EnableTownSpawn enableTownSpawn = territoryData.getNewLevel().getStat(EnableTownSpawn.class);
        //Spawn Unlocked
        if (!enableTownSpawn.isEnabled()) {
            TanChatUtils.message(player, Lang.SPAWN_NOT_UNLOCKED.get(langType));
            return;
        }

        int secondBeforeTeleport = Constants.getTimeBeforeTeleport();
        if (isPlayerRegistered(tanPlayer.getID())) {
            TanChatUtils.message(player, Lang.WAIT_BEFORE_ANOTHER_TELEPORTATION.get(langType));
            return;
        }

        TeleportationData teleportationData = territoryData.getTeleportationData();
        if (!teleportationData.isSpawnSet()) {
            TanChatUtils.message(player, Lang.SPAWN_NOT_SET.get(langType));
            return;
        }

        Chunk chunk = teleportationData.getPosition().getLocation().getChunk();
        IClaimedChunk claimedChunk = TownsAndNations.getPlugin().getClaimStorage().get(chunk);

        if (!(claimedChunk instanceof TerritoryChunk territoryChunk &&
                territoryChunk.getOwner().equals(territoryData) &&
                !territoryChunk.isOccupied())
        ) {
            TanChatUtils.message(player, Lang.SPAWN_INVALID.get(langType));
            return;
        }

        if (secondBeforeTeleport > 0) {
            if (Constants.isCancelTeleportOnMovePosition()) {
                TanChatUtils.message(player, Lang.TELEPORTATION_IN_X_SECONDS_NOT_MOVE.get(langType, Integer.toString(secondBeforeTeleport)));
            } else {
                TanChatUtils.message(player, Lang.TELEPORTATION_IN_X_SECONDS.get(langType, Integer.toString(secondBeforeTeleport)));
            }

            registerSpawn(tanPlayer, territoryData.getTeleportationData().getPosition().getLocation());
        }
        Bukkit.getScheduler().runTaskLater(TownsAndNations.getPlugin(), () -> confirmTeleportation(tanPlayer), secondBeforeTeleport * 20L);
    }

    public static void teleportToFort(Player player, ITanPlayer tanPlayer, Fort fortData) {
        LangType langType = tanPlayer.getLang();

        Territory territory = fortData.getOwner();
        EnableTownSpawn enableTownSpawn = territory.getNewLevel().getStat(EnableTownSpawn.class);
        //Spawn Unlocked
        if (!enableTownSpawn.isEnabled()) {
            TanChatUtils.message(player, Lang.SPAWN_NOT_UNLOCKED.get(langType));
            return;
        }

        int secondBeforeTeleport = Constants.getTimeBeforeTeleport();


        if (isPlayerRegistered(tanPlayer.getID())) {
            TanChatUtils.message(player, Lang.WAIT_BEFORE_ANOTHER_TELEPORTATION.get(langType));
            return;
        }

        if (fortData.isOccupied()) {
            TanChatUtils.message(player, Lang.SPAWN_INVALID.get(langType));
            return;
        }

        if(!Constants.allowFortTeleport()){
            TanChatUtils.message(player, Lang.TERRITORY_NOT_FOUND);
            return;
        }

        if(!Constants.allowFortTeleportDuringWar() && territory.isAtWar()){
            TanChatUtils.message(player, Lang.CANNOT_TELEPORT_TO_FORT_WHILE_AT_WAR.get());
            return;
        }

        if (secondBeforeTeleport > 0) {
            if (Constants.isCancelTeleportOnMovePosition()) {
                TanChatUtils.message(player, Lang.TELEPORTATION_IN_X_SECONDS_NOT_MOVE.get(langType, Integer.toString(secondBeforeTeleport)));
            } else {
                TanChatUtils.message(player, Lang.TELEPORTATION_IN_X_SECONDS.get(langType, Integer.toString(secondBeforeTeleport)));
            }

            Location location = fortData.getPosition().getLocation();
            location.add(0,1,0);
            location.setPitch(player.getPitch());
            location.setYaw(player.getYaw());
            registerSpawn(tanPlayer, location);
        }
        Bukkit.getScheduler().runTaskLater(TownsAndNations.getPlugin(), () -> confirmTeleportation(tanPlayer), secondBeforeTeleport * 20L);
    }

    public static void confirmTeleportation(ITanPlayer tanPlayer) {

        if (!spawnRegister.containsKey(tanPlayer.getID())) {
            return;
        }
        PlannedTeleportation plannedTeleportation = spawnRegister.get(tanPlayer.getID());
        if (plannedTeleportation == null || plannedTeleportation.isCancelled()) {
            removePlayer(tanPlayer);
            return;
        }

        Location teleportationPosition = plannedTeleportation.getTeleportationPosition();

        Player player = Bukkit.getPlayer(tanPlayer.getID());
        if (player != null) {
            player.teleport(teleportationPosition);
            TanChatUtils.message(player, Lang.SPAWN_TELEPORTED.get(tanPlayer), SoundEnum.MINOR_GOOD);
        }
        removePlayer(tanPlayer);
    }

}
