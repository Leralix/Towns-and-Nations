package org.leralix.tan.data.chunk;

import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.permission.ChunkPermissionType;
import org.leralix.tan.lang.LangType;
import org.tan.api.interfaces.chunk.TanClaimedChunk;
import org.tan.api.interfaces.territory.TanTerritory;

import java.util.UUID;

public interface IClaimedChunk extends TanClaimedChunk {

    Vector2D getVector2D();

    Vector2D getMiddleVector2D();

    int getX();

    int getMiddleX();

    int getZ();

    int getMiddleZ();

    UUID getWorldUUID();

    String getWorldID();

    String getworldName();

    boolean canPlayerDo(Player player, ITanPlayer tanPlayer, ChunkPermissionType permissionType, Location location);

    void playerEnterClaimedArea(Player player, ITanPlayer tanPlayer, boolean displayTerritoryColor);

    String getName();

    boolean canEntitySpawn(EntityType entityType);

    World getWorld();

    Component getMapIcon(LangType langType, boolean isMiddleOfMap);

    boolean canTerritoryClaim(Player player, Territory territoryData, LangType langType);

    boolean canTerritoryClaim(Territory territoryData);

    boolean canClaim(TanTerritory territory);

    void claim(TanTerritory tanTerritory);

    boolean canHostileGrief();

    boolean canVillagerGrief();

    boolean canPassiveGrief();

    boolean canUnauthorizedPlayerUseMounts();

    Chunk getChunk();

    ChunkType getType();

    void notifyUpdate();

    boolean containsPosition(Vector3D position);

    void unclaim();

    boolean isClaimed();
}
