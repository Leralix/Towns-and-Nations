package org.leralix.tan.dataclass.chunk;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.lib.position.Vector2D;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.integration.worldguard.WorldGuardManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.Constants;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.utils.TerritoryUtil;

import java.util.Objects;
import java.util.UUID;

public abstract class ClaimedChunk2 {

    @Deprecated(forRemoval = true) //0.16.0
    private final int x;
    @Deprecated(forRemoval = true) //0.16.0
    private final int z;
    @Deprecated(forRemoval = true) //0.16.0
    private final String worldUUID;
    private Vector2D vector2D;
    protected final String ownerID;

    protected ClaimedChunk2(Chunk chunk, String owner) {
        this(chunk.getX(), chunk.getZ(), chunk.getWorld().getUID().toString(), owner);
    }

    protected ClaimedChunk2(int x, int z, String worldUUID, String owner) {
        this.x = x;
        this.z = z;
        this.worldUUID = worldUUID;
        this.vector2D = new Vector2D(x, z, worldUUID);
        this.ownerID = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClaimedChunk2 that)) return false;
        return x == that.x && z == that.z && worldUUID.equals(that.worldUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z, worldUUID);
    }

    public String getOwnerID() {
        return this.ownerID;
    }

    public Vector2D getVector2D(){
        if (vector2D == null){
            this.vector2D = new Vector2D(x, z, worldUUID);
        }
        return vector2D;
    }
    public Vector2D getMiddleVector2D(){
        Vector2D vector2D = getVector2D();
        return new Vector2D(vector2D.getX() * 16 + 8, vector2D.getZ() * 16 + 8, vector2D.getWorldID().toString());
    }

    public int getX() {
        return this.x;
    }

    public int getMiddleX() {
        return this.x * 16 + 8;
    }

    public int getZ() {
        return this.z;
    }

    public int getMiddleZ() {
        return this.z * 16 + 8;
    }

    public String getWorldUUID() {
        return this.worldUUID;
    }

    public boolean canPlayerDo(Player player, ChunkPermissionType permissionType, Location location){


        //If worldguard is enabled and chunk type is checked, add a worldguard check to the default tan's check.
        var worldGuardManager = WorldGuardManager.getInstance();
        if(worldGuardManager.isEnabled()){
            if(Constants.isWorldGuardEnabledFor(getType()) &&
                    worldGuardManager.isHandledByWorldGuard(location)) {
                return worldGuardManager.isActionAllowed(player, location, permissionType) &&
                        canPlayerDoInternal(player, permissionType, location);
            }
        }

        return canPlayerDoInternal(player, permissionType, location);
    }

    protected abstract boolean canPlayerDoInternal(Player player, ChunkPermissionType permissionType, Location location);

    void playerCantPerformAction(Player player) {
        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_ACTION_NO_PERMISSION.get(player));
        player.sendMessage(TanChatUtils.getTANString() + Lang.CHUNK_BELONGS_TO.get(player, getOwner().getName()));
    }

    public abstract void unclaimChunk(Player player);

    public abstract void playerEnterClaimedArea(Player player, boolean displayTerritoryColor);

    public abstract String getName();

    public abstract boolean canEntitySpawn(EntityType entityType);

    public World getWorld() {
        return Bukkit.getWorld(UUID.fromString(this.worldUUID));
    }

    public TerritoryData getOwner() {
        if (ownerID == null) return null;
        return TerritoryUtil.getTerritory(ownerID);
    }

    public TextComponent getMapIcon(Player player) {
        return getMapIcon(PlayerDataStorage.getInstance().get(player));
    }

    public abstract TextComponent getMapIcon(ITanPlayer tanPlayer);

    public abstract boolean canTerritoryClaim(TerritoryData territoryData);

    public boolean canTerritoryClaim(Player player, TerritoryData territoryData) {
        boolean result = canTerritoryClaim(territoryData);
        if (result) {
            return true;
        }
        player.sendMessage(TanChatUtils.getTANString() + Lang.CHUNK_ALREADY_CLAIMED_WARNING.get(player, getOwner().getBaseColoredName()));
        return false;
    }

    public abstract boolean isClaimed();

    public abstract boolean canExplosionGrief();

    public abstract boolean canFireGrief();

    public abstract boolean canPVPHappen();

    public abstract boolean canMobGrief();

    public Chunk getChunk() {
        World world = Bukkit.getWorld(UUID.fromString(this.worldUUID));
        if (world == null) {
            return null;
        }
        return world.getChunkAt(x, z);
    }

    public abstract ChunkType getType();

}