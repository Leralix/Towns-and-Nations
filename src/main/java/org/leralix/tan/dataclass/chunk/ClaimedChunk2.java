package org.leralix.tan.dataclass.chunk;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.utils.TerritoryUtil;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.lang.Lang;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public abstract class ClaimedChunk2 {

    private final int x;
    private final int z;
    private final String worldUUID;
    protected final String ownerID;

    protected ClaimedChunk2(Chunk chunk, String owner) {
        this.x = chunk.getX();
        this.z = chunk.getZ();
        this.worldUUID = chunk.getWorld().getUID().toString();
        this.ownerID = owner;
    }

    protected ClaimedChunk2(int x, int z, String worldUUID, String owner) {
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

    void playerCantPerformAction(Player player) {
        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_ACTION_NO_PERMISSION.get(player));
        player.sendMessage(TanChatUtils.getTANString() + Lang.CHUNK_BELONGS_TO.get(player, getOwner().getName()));
    }

    public abstract void unclaimChunk(Player player);

    public abstract void playerEnterClaimedArea(Player player);

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

    public abstract TextComponent getMapIcon(PlayerData playerData);

    public abstract boolean canTerritoryClaim(Optional<Player> player, TerritoryData territoryData);


    public abstract boolean isClaimed();

    public abstract boolean canExplosionGrief();

    public abstract boolean canFireGrief();

    public abstract boolean canPVPHappen();

    public Chunk getChunk() {
        World world = Bukkit.getWorld(UUID.fromString(this.worldUUID));
        if (world == null) {
            return null;
        }
        return world.getChunkAt(x, z);
    }


}