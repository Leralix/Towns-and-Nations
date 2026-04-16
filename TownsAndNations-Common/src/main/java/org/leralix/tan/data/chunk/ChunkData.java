package org.leralix.tan.data.chunk;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.api.external.worldguard.WorldGuardManager;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.permission.ChunkPermissionType;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.constants.Constants;
import org.tan.api.enums.EChunkPermission;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.chunk.TanClaimedChunk;
import org.tan.api.interfaces.territory.TanNation;
import org.tan.api.interfaces.territory.TanRegion;
import org.tan.api.interfaces.territory.TanTerritory;
import org.tan.api.interfaces.territory.TanTown;

import java.util.UUID;

/**
 * The Main implementation of a chunk in Towns and Nations.
 * Each minecraft chunk is linked to a ClaimedChunk.
 * <ul>
 *     <li>Claimed by a territory: {@link TerritoryChunkData}</li>
 *     <li>Wilderness chunk: {@link WildernessChunkData}</li>
 *     <li>Landmark chunk {@link LandmarkClaimedChunk}</li>
 * </ul>
 */
public abstract class ChunkData implements IClaimedChunk, TanClaimedChunk {

    private final Vector2D vector2D;

    protected ChunkData(Chunk chunk) {
        this(chunk.getX(), chunk.getZ(), chunk.getWorld().getUID().toString());
    }

    protected ChunkData(int x, int z, String worldUUID) {
        this.vector2D = new Vector2D(x, z, worldUUID);
    }

    @Override
    public Vector2D getVector2D() {
        return vector2D;
    }

    @Override
    public Vector2D getMiddleVector2D() {
        Vector2D vector = getVector2D();
        return new Vector2D(vector.getX() * 16 + 8, vector.getZ() * 16 + 8, vector.getWorldID().toString());
    }
    @Override
    public int getX() {
        return vector2D.getX();
    }

    @Override
    public int getMiddleX() {
        return getX() * 16 + 8;
    }

    @Override
    public int getZ() {
        return vector2D.getZ();
    }

    @Override
    public int getMiddleZ() {
        return getZ() * 16 + 8;
    }

    @Override
    public String getWorldID() {
        return vector2D.getWorldID().toString();
    }

    @Override
    public UUID getWorldUUID() {
        return vector2D.getWorldID();
    }

    @Override
    public String getworldName() {
        World world = getWorld();
        if (world == null) return "";
        return world.getName();
    }

    @Override
    public boolean canPlayerDo(Player player, ITanPlayer tanPlayer, ChunkPermissionType permissionType, Location location) {

        //If worldguard is enabled and a chunk type is ok, add a worldguard check to the default tan's check.
        var worldGuardManager = WorldGuardManager.getInstance();
        if (worldGuardManager.isEnabled() &&
                Constants.isWorldGuardEnabledFor(getType()) &&
                worldGuardManager.isHandledByWorldGuard(location)) {
            return worldGuardManager.isActionAllowed(player, location, permissionType) &&
                    canPlayerDoInternal(player, tanPlayer, permissionType, location);

        }

        return canPlayerDoInternal(player, tanPlayer, permissionType, location);
    }

    protected abstract boolean canPlayerDoInternal(Player player, ITanPlayer tanPlayer, ChunkPermissionType permissionType, Location location);

    protected abstract void playerCantPerformAction(Player player, LangType langType);

    @Override
    public World getWorld() {
        return vector2D.getWorld();
    }

    @Override
    public boolean canClaim(TanTerritory territory) {
        if (!(territory instanceof Territory territoryData)) {
            return false;
        }
        return canTerritoryClaim(territoryData);
    }

    @Override
    public void claim(TanTerritory tanTerritory) {
        if (tanTerritory == null) {
            return;
        }
        if (tanTerritory instanceof TanTown) {
            TownsAndNations.getPlugin().getClaimStorage().claimTownChunk(getChunk(), tanTerritory.getID());
        }
        if (tanTerritory instanceof TanRegion) {
            TownsAndNations.getPlugin().getClaimStorage().claimRegionChunk(getChunk(), tanTerritory.getID());
        }
        if (tanTerritory instanceof TanNation) {
            TownsAndNations.getPlugin().getClaimStorage().claimNationChunk(getChunk(), tanTerritory.getID());
        }
    }

    @Override
    public Chunk getChunk() {
        World world = vector2D.getWorld();
        if (world == null) {
            return null;
        }
        return world.getChunkAt(vector2D.getX(), vector2D.getZ());
    }

    @Override
    public boolean containsPosition(Vector3D position) {
        Chunk chunkToCompare = position.getLocation().getChunk();
        Chunk chunk = getChunk();
        return chunk.getX() == chunkToCompare.getX() && chunk.getZ() == chunkToCompare.getZ() && chunk.getWorld() == chunkToCompare.getWorld();
    }

    @Override
    public boolean canBeGriefByExplosion() {
        return canExplosionGrief();
    }

    @Override
    public boolean canBeGriefByFire() {
        return canFireGrief();
    }

    @Override
    public boolean canPvpHappen() {
        return canPVPHappen();
    }

    @Override
    public boolean canPlayerDoAction(TanPlayer tanPlayer, EChunkPermission permission, Location location) {
        Player player = Bukkit.getPlayer(tanPlayer.getID());
        if (player == null) {
            return false;
        }
        ITanPlayer iTanPlayer = TownsAndNations.getPlugin().getPlayerDataStorage().get(player);
        return canPlayerDo(player, iTanPlayer, ChunkPermissionType.valueOf(permission.name()), location);
    }

    @Override
    public void unclaim() {
        TownsAndNations.getPlugin().getClaimStorage().unclaimChunkAndUpdate(this);
    }
}