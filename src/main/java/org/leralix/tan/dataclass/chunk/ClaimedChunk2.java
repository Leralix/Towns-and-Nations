package org.leralix.tan.dataclass.chunk;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.api.external.worldguard.WorldGuardManager;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public abstract class ClaimedChunk2 {

    private Vector2D vector2D;
    protected final String ownerID;

    protected ClaimedChunk2(Chunk chunk, String owner) {
        this(chunk.getX(), chunk.getZ(), chunk.getWorld().getUID().toString(), owner);
    }

    protected ClaimedChunk2(int x, int z, String worldUUID, String owner) {
        this.vector2D = new Vector2D(x, z, worldUUID);
        this.ownerID = owner;
    }

    public String getOwnerID() {
        return this.ownerID;
    }

    public Vector2D getVector2D() {
        return vector2D;
    }

    public Vector2D getMiddleVector2D() {
        Vector2D vector = getVector2D();
        return new Vector2D(vector.getX() * 16 + 8, vector.getZ() * 16 + 8, vector.getWorldID().toString());
    }

    public int getX() {
        return vector2D.getX();
    }

    public int getMiddleX() {
        return getX() * 16 + 8;
    }

    public int getZ() {
        return vector2D.getZ();
    }

    public int getMiddleZ() {
        return getZ() * 16 + 8;
    }

    public String getWorldUUID() {
        return vector2D.getWorldID().toString();
    }

    public boolean canPlayerDo(Player player, ChunkPermissionType permissionType, Location location) {


        //If worldguard is enabled and a chunk type is ok, add a worldguard check to the default tan's check.
        var worldGuardManager = WorldGuardManager.getInstance();
        if (worldGuardManager.isEnabled() &&
                Constants.isWorldGuardEnabledFor(getType()) &&
                worldGuardManager.isHandledByWorldGuard(location))
        {
            return worldGuardManager.isActionAllowed(player, location, permissionType) &&
                    canPlayerDoInternal(player, permissionType, location);

        }

        return canPlayerDoInternal(player, permissionType, location);
    }

    protected abstract boolean canPlayerDoInternal(Player player, ChunkPermissionType permissionType, Location location);

    void playerCantPerformAction(Player player) {
        TanChatUtils.message(player, Lang.PLAYER_ACTION_NO_PERMISSION.get(player));
        TanChatUtils.message(player, Lang.CHUNK_BELONGS_TO.get(player, getOwner().getName()));
    }

    public abstract void unclaimChunk(Player player);

    public abstract void playerEnterClaimedArea(Player player, boolean displayTerritoryColor);

    public abstract String getName();

    public abstract boolean canEntitySpawn(EntityType entityType);

    public World getWorld() {
        return vector2D.getWorld();
    }

    public TerritoryData getOwner() {
        if (ownerID == null) return null;
        return TerritoryUtil.getTerritory(ownerID);
    }

    public abstract TextComponent getMapIcon(LangType langType);

    public abstract boolean canTerritoryClaim(TerritoryData territoryData);

    public boolean canTerritoryClaim(Player player, TerritoryData territoryData) {
        if (canTerritoryClaim(territoryData)) {
            return true;
        }
        TanChatUtils.message(player, Lang.CHUNK_ALREADY_CLAIMED_WARNING.get(player, getOwner().getBaseColoredName()));
        return false;
    }

    public abstract boolean isClaimed();

    public abstract boolean canExplosionGrief();

    public abstract boolean canFireGrief();

    public abstract boolean canPVPHappen();

    public abstract boolean canMobGrief();

    public Chunk getChunk() {
        World world = vector2D.getWorld();
        if (world == null) {
            return null;
        }
        return world.getChunkAt(vector2D.getX(), vector2D.getZ());
    }

    public abstract ChunkType getType();

    public abstract void notifyUpdate();

    public boolean containsPosition(Vector3D position) {
        Chunk chunkToCompare = position.getLocation().getChunk();
        Chunk chunk = getChunk();
        return chunk.getX() == chunkToCompare.getX() && chunk.getZ() == chunkToCompare.getZ() && chunk.getWorld() == chunkToCompare.getWorld();
    }
}