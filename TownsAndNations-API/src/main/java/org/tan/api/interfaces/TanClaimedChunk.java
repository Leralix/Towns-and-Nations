package org.tan.api.interfaces;

import org.bukkit.Location;
import org.tan.api.enums.EChunkPermission;

import java.util.Optional;
import java.util.UUID;

public interface TanClaimedChunk {


    /**
     * Return the x position of the chunk.
     * <br>
     * It is the chunk x position, not the block x position (divided by 16).
     * @return The x position of the chunk.
     */
    int getX();

    /**
     * Return the z position of the chunk.
     * <br>
     * It is the chunk z position, not the block z position (divided by 16).
     * @return The z position of the chunk.
     */
    int getZ();

    /**
     * Return the world UUID of the chunk.
     * <br>
     * UUID is different from the world name.
     * Use {@link #getworldName()} for the name.
     * @return The world UUID of the chunk.
     */
    UUID getWorldUUID();

    /**
     * Return the world name of the chunk.
     * <br>
     * @return The world name of the chunk.
     */
    String getworldName();

    /**
     * Check if the chunk is currently being claimed by a territory.
     * @return True if the chunk is claimed by a territory or a landmark, false otherwise
     */
    Boolean isClaimed();

    /**
     * Return the ID of the owner of the chunk.
     * <br>
     * Return {@link Optional#empty()} if the chunk is wilderness
     * or claimed by a landmark.
     * @return The UUID of the territory owning the chunk if claimed.
     */
    Optional<UUID> getOwnerID();

    /**
     * Unclaim the chunk.
     * <br>
     * Chunk will now be wilderness if previously claimed by a territory.
     * If the chunk is claimed by a landmark, it will not be unclaimed.
     */
    void unclaim();

    /**
     * Check if the territory can claim the chunk.
     * <br>
     * It will return false if it is exceeding the chunk limit or if the chunk is already claimed.
     * @param territory The territory to check.
     * @return True if the territory can claim the chunk, false otherwise
     */
    boolean canClaim(TanTerritory territory);
    /**
     * Claim the chunk by a territory.
     * <br>
     * This does not check if the territory can claim the chunk,
     * nor if the territory can claim more chunks. Use {@link #canClaim(TanTerritory)} for that.
     * @param territory The territory to claim the chunk.
     */
    void claim(TanTerritory territory);

    /**
     * Check if the chunk can be griefed by explosions.
     * @return True if the chunk can be griefed by explosions, false otherwise
     */
    boolean canBeGriefByExplosion();

    /**
     * Check if the chunk can be griefed by fire.
     * @return True if the chunk can be griefed by fire, false otherwise
     */
    boolean canBeGriefByFire();

    /**
     * Check if pvp can happen in the chunk.
     * @return True if pvp can happen in the chunk, false otherwise
     */
    boolean canPvpHappen();

    /**
     * Check if the player can do the action in the chunk.
     * <br>
     * @param player        The player to check
     * @param permission    The specific permission to check
     * @return              True if the player has the permission, false otherwise
     */
    boolean canPlayerDoAction(TanPlayer player, EChunkPermission permission, Location location);

}
