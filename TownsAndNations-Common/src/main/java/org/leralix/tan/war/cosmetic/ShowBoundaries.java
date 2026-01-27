package org.leralix.tan.war.cosmetic;

import org.bukkit.entity.Player;
import org.leralix.lib.position.CardinalPoint;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.position.Vector3D;
import org.leralix.lib.utils.particles.ParticleUtils;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.chunk.ClaimedChunk;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.territory.ChunkUtil;
import org.leralix.tan.war.info.BoundaryType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShowBoundaries {


    public static void display(Player player) {

        double radius = Constants.getWarBoundaryRadius();
        List<ClaimedChunk> chunkInRange = ChunkUtil.getChunksInRadius(player.getChunk(), radius);

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        List<ChunkLine> lines = sortChunkLines(chunkInRange, tanPlayer);

        drawLines(player, lines);
    }

    static void drawLines(Player player, List<ChunkLine> lines) {

        int y = (int) player.getLocation().getY() + 1;

        for (ChunkLine line : lines) {
            ParticleUtils.drawPane(TownsAndNations.getPlugin(), player,
                    new Vector3D(line.getPoint1().getX(), y, line.getPoint1().getZ(), line.getPoint1().getWorldID().toString()),
                    new Vector3D(line.getPoint2().getX(), y - 1, line.getPoint2().getZ(), line.getPoint2().getWorldID().toString()),
                    1,
                    Constants.getBoundaryParticles().getParticle(line.getBoundaryType()));
        }
    }

    static List<ChunkLine> sortChunkLines(List<ClaimedChunk> chunkInRange, ITanPlayer tanPlayer) {
        List<ChunkLine> res = new ArrayList<>();

        for (ClaimedChunk centerChunk : chunkInRange) {
            if (centerChunk instanceof TerritoryChunk centerTerritoryChunk) {

                TownRelation townRelation = centerTerritoryChunk.getOccupier().getWorstRelationWith(tanPlayer);
                BoundaryType type = townRelation.getBoundaryType();
                Vector2D centerChunkPosition = centerChunk.getVector2D();

                var claimStorage = NewClaimedChunkStorage.getInstance();

                // NORTH
                ClaimedChunk northChunk = claimStorage
                        .get(
                                centerChunkPosition.getX(),
                                centerChunkPosition.getZ() - 1,
                                centerChunkPosition.getWorldID().toString()
                        );

                if(isDifferentTerritory(centerTerritoryChunk, northChunk)){
                    res.add(getChunkLine(centerChunkPosition, CardinalPoint.NORTH, type));
                }

                // SOUTH
                ClaimedChunk southChunk = claimStorage
                        .get(
                                centerChunkPosition.getX(),
                                centerChunkPosition.getZ() + 1,
                                centerChunkPosition.getWorldID().toString()
                        );
                if(isDifferentTerritory(centerTerritoryChunk, southChunk)){
                    res.add(getChunkLine(centerChunkPosition, CardinalPoint.SOUTH, type));
                }

                // EAST
                ClaimedChunk eastChunk = NewClaimedChunkStorage.getInstance()
                        .get(
                                centerChunkPosition.getX() + 1,
                                centerChunkPosition.getZ(),
                                centerChunkPosition.getWorldID().toString()
                        );
                if(isDifferentTerritory(centerTerritoryChunk, eastChunk)){
                    res.add(getChunkLine(centerChunkPosition, CardinalPoint.EAST, type));
                }

                // WEST
                ClaimedChunk westChunk = NewClaimedChunkStorage.getInstance()
                        .get(
                                centerChunkPosition.getX() - 1,
                                centerChunkPosition.getZ(),
                                centerChunkPosition.getWorldID().toString()
                        );
                if(isDifferentTerritory(centerTerritoryChunk, westChunk)){
                    res.add(getChunkLine(centerChunkPosition, CardinalPoint.WEST, type));
                }
            }
        }
        return res;
    }

    private static boolean isDifferentTerritory(TerritoryChunk centerChunk, ClaimedChunk otherChunk) {

        if(otherChunk instanceof TerritoryChunk otherTerritoryChunk){
            var centerTerritory = centerChunk.getOccupier();
            var otherTerritory = otherTerritoryChunk.getOccupier();

            if (centerTerritory == null || otherTerritory == null) {
                return true;
            }

            if (Objects.equals(centerTerritory.getID(), otherTerritory.getID())) {
                return false;
            }

            return centerTerritory.getRelationWith(otherTerritory) != TownRelation.SELF;
        }
        return true;
    }

    static ChunkLine getChunkLine(Vector2D centerChunk, CardinalPoint dir, BoundaryType boundaryType) {

        int baseX = centerChunk.getX() * 16;
        int baseZ = centerChunk.getZ() * 16;

        String worldID = centerChunk.getWorldID().toString();

        Vector2D start, end;
        switch (dir) {
            case NORTH -> {
                start = new Vector2D(baseX, baseZ, worldID);
                end = new Vector2D(baseX + 16, baseZ, worldID);
            }
            case SOUTH -> {
                start = new Vector2D(baseX, baseZ + 16, worldID);
                end = new Vector2D(baseX + 16, baseZ + 16, worldID);
            }
            case EAST -> {
                start = new Vector2D(baseX + 16, baseZ, worldID);
                end = new Vector2D(baseX + 16, baseZ + 16, worldID);
            }
            case WEST -> {
                start = new Vector2D(baseX, baseZ, worldID);
                end = new Vector2D(baseX, baseZ + 16, worldID);
            }
            default -> throw new IllegalStateException("Unexpected value: " + dir);
        }


        return new ChunkLine(start, end, boundaryType);
    }
}
