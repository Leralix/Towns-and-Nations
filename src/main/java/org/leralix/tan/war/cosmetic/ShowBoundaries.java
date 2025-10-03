package org.leralix.tan.war.cosmetic;

import org.bukkit.entity.Player;
import org.leralix.lib.position.CardinalPoint;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.position.Vector3D;
import org.leralix.lib.utils.ParticleUtils;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.territory.ChunkUtil;
import org.leralix.tan.war.legacy.CurrentAttack;

import java.util.ArrayList;
import java.util.List;

public class ShowBoundaries {


    public static void display(Player player) {

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

        List<CurrentAttack> attacks = tanPlayer.getCurrentAttacks();
        if (attacks.isEmpty()) {
            return;
        }
        double radius = Constants.getWarBoundaryRadius();
        List<ClaimedChunk2> chunkInRange = ChunkUtil.getChunksInRadius(player.getChunk(), radius);

        List<ChunkLine> lines = sortChunkLines(chunkInRange, attacks);

        drawLines(player, lines);
    }

    static void drawLines(Player player, List<ChunkLine> lines) {

        int y = (int) player.getLocation().getY() + 1;

        for (ChunkLine line : lines) {
            ParticleUtils.drawLine(TownsAndNations.getPlugin(), player,
                    new Vector3D(line.getPoint1().getX(), y, line.getPoint1().getZ(), line.getPoint1().getWorldID().toString()),
                    new Vector3D(line.getPoint2().getX(), y, line.getPoint2().getZ(), line.getPoint2().getWorldID().toString()),
                    1);
        }
    }

    static List<ChunkLine> sortChunkLines(List<ClaimedChunk2> chunkInRange, List<CurrentAttack> attacks) {
        List<ChunkLine> res = new ArrayList<>();

        for (ClaimedChunk2 centerChunk : chunkInRange) {
            if (centerChunk instanceof TerritoryChunk territoryChunk) {
                Vector2D centerChunkPosition = centerChunk.getVector2D();

                // NORTH
                ClaimedChunk2 northChunk = NewClaimedChunkStorage.getInstance()
                        .get(centerChunkPosition.getX(), centerChunkPosition.getZ() - 1, centerChunkPosition.getWorldID().toString());
                if (isFrontline(territoryChunk, northChunk, attacks)) {
                    res.add(getChunkLine(centerChunkPosition, CardinalPoint.NORTH));
                }

                // SOUTH
                ClaimedChunk2 southChunk = NewClaimedChunkStorage.getInstance()
                        .get(centerChunkPosition.getX(), centerChunkPosition.getZ() + 1, centerChunkPosition.getWorldID().toString());
                if (isFrontline(territoryChunk, southChunk, attacks)) {
                    res.add(getChunkLine(centerChunkPosition, CardinalPoint.SOUTH));
                }

                // EAST
                ClaimedChunk2 eastChunk = NewClaimedChunkStorage.getInstance()
                        .get(centerChunkPosition.getX() + 1, centerChunkPosition.getZ(), centerChunkPosition.getWorldID().toString());
                if (isFrontline(territoryChunk, eastChunk, attacks)) {
                    res.add(getChunkLine(centerChunkPosition, CardinalPoint.EAST));
                }

// WEST
                ClaimedChunk2 westChunk = NewClaimedChunkStorage.getInstance()
                        .get(centerChunkPosition.getX() - 1, centerChunkPosition.getZ(), centerChunkPosition.getWorldID().toString());
                if (isFrontline(territoryChunk, westChunk, attacks)) {
                    res.add(getChunkLine(centerChunkPosition, CardinalPoint.WEST));
                }
            }
        }
        return res;
    }

    static ChunkLine getChunkLine(Vector2D centerChunk, CardinalPoint dir) {

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


        return new ChunkLine(start, end);
    }


    private static boolean isFrontline(TerritoryChunk centerChunk, ClaimedChunk2 chunkToCompare, List<CurrentAttack> attacks) {

        if (chunkToCompare == null) {
            return false;
        }

        TerritoryData occupier = centerChunk.getOccupier();

        for (CurrentAttack attackData : attacks) {
            // If chunk is at war, a frontline apprears if the other chunk is not occupied by the same town
            if (attackData.getAttackData().getWar().isMainDefender(occupier)) {
                if (chunkToCompare instanceof TerritoryChunk territoryChunk &&
                        territoryChunk.getOccupierID().equals(occupier.getID())) {
                    return false;
                }
                return true;
            }
        }

        return false;
    }

}
