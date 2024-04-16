package org.tan.TownsAndNations.DataClass.newChunkData;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.ChunkPermissionType;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import java.util.Objects;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public abstract class ClaimedChunk2 {

    private final int x, z;
    private final String worldUUID;
    protected final String ownerID;

    public ClaimedChunk2(Chunk chunk, String owner) {
        this.x = chunk.getX();
        this.z = chunk.getZ();
        this.worldUUID = chunk.getWorld().getUID().toString();
        this.ownerID = owner;
    }
    public ClaimedChunk2(int x, int z, String worldUUID , String owner) {
        this.x = x;
        this.z = z;
        this.worldUUID = worldUUID;
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

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public String getWorldUUID() {
        return this.worldUUID;
    }

    public abstract boolean canPlayerDo(Player player, ChunkPermissionType permissionType, Location location);

    void playerCantPerformAction(Player player){
        player.sendMessage(getTANString() + Lang.PLAYER_ACTION_NO_PERMISSION.get());
        player.sendMessage(getTANString() + Lang.CHUNK_BELONGS_TO.get(TownDataStorage.get(getOwnerID()).getName()));
    }

    public abstract void unclaimChunk(Player player, Chunk chunk);

    public abstract void playerEnterClaimedArea(Player player);

    public abstract String getName();

    public abstract boolean canEntitySpawn(EntityType entityType);
}