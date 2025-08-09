package org.leralix.tan.war.legacy;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.lib.position.Vector3D;
import org.leralix.lib.utils.ParticleUtils;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.war.CurrentWar;

public class CurrentAttack {

    private final CurrentWar attackData;
    private boolean end;


    private final long totalTime;
    private long remaining;
    private final BossBar bossBar;


    public CurrentAttack(CurrentWar plannedAttack, long startTime, long endTime) {

        this.attackData = plannedAttack;

        this.end = false;

        this.totalTime = endTime - startTime;
        this.remaining = totalTime;

        this.bossBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);

        for (TerritoryData territoryData : plannedAttack.getAttackingTerritories()) {
            for (ITanPlayer tanPlayer : territoryData.getITanPlayerList()) {
                tanPlayer.addWar(this);
                Player player = tanPlayer.getPlayer();
                if (player != null) {
                    bossBar.addPlayer(player);
                }
            }
        }
        for (TerritoryData territoryData : plannedAttack.getDefendingTerritories()) {
            for (ITanPlayer tanPlayer : territoryData.getITanPlayerList()) {
                tanPlayer.addWar(this);
                Player player = tanPlayer.getPlayer();
                if (player != null) {
                    bossBar.addPlayer(player);
                }
            }
        }
        start();
    }

    private void updateBossBar() {
        long hours = remaining / 72000;
        long minutes = (remaining % 72000) / 1200;
        long seconds = (remaining % 1200) / 20;
        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        bossBar.setTitle(timeString);
        bossBar.setProgress((double) (totalTime - remaining) / totalTime);
    }

    public void addPlayer(ITanPlayer tanPlayer) {
        Player player = tanPlayer.getPlayer();
        if (player != null && remaining > 0) {
            bossBar.addPlayer(player);
        }
    }

    private void start() {
        BukkitRunnable timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (remaining > 0 && !end) {
                    remaining--;
                    updateBossBar();
                }
                else {
                    end();
                    cancel();
                }
            }
        };
        timerTask.runTaskTimer(TownsAndNations.getPlugin(), 0, 1); // Exécute toutes les secondes
    }


    public void end() {

        end = true;

        new BukkitRunnable() {
            @Override
            public void run() {
                for (TerritoryData territoryData : attackData.getAttackingTerritories()) {
                    for (ITanPlayer tanPlayer : territoryData.getITanPlayerList()) {
                        tanPlayer.removeWar(CurrentAttack.this);
                    }
                }
                for (TerritoryData territoryData : attackData.getDefendingTerritories()) {
                    for (ITanPlayer tanPlayer : territoryData.getITanPlayerList()) {
                        tanPlayer.removeWar(CurrentAttack.this);
                    }
                }

                bossBar.removeAll();
                CurrentAttacksStorage.remove(CurrentAttack.this);

                for (TerritoryData territoryData : attackData.getAttackingTerritories()) {
                    territoryData.removeCurrentAttack(CurrentAttack.this);
                }
                for (TerritoryData territoryData : attackData.getDefendingTerritories()) {
                    territoryData.removeCurrentAttack(CurrentAttack.this);
                }

            }
        }.runTaskLater(TownsAndNations.getPlugin(), 20L * 20); //Still showing the boss bar for 20s
    }

    public boolean containsPlayer(ITanPlayer tanPlayer) {
        for (TerritoryData territoryData : attackData.getAttackingTerritories()) {
            if (territoryData.isPlayerIn(tanPlayer)) {
                return true;
            }
        }
        for (TerritoryData territoryData : attackData.getDefendingTerritories()) {
            if (territoryData.isPlayerIn(tanPlayer)) {
                return true;
            }
        }
        return false;
    }


    public CurrentWar getAttackData() {
        return attackData;
    }

    public void displayBoundaries() {
        for (Player player : attackData.getAllOnlinePlayers()) {
            if(player != null){
                displayBoundaries(player);
            }
        }
    }

    public void displayBoundaries(Player player) {
        Chunk centerChunk = player.getLocation().getChunk();
        World world = centerChunk.getWorld();
        String worldID = world.getUID().toString();
        int centerChunkX = centerChunk.getX();
        int centerChunkZ = centerChunk.getZ();
        int viewDistance = 4;

        for (int dx = -viewDistance; dx <= viewDistance; dx++) {
            for (int dz = -viewDistance; dz <= viewDistance; dz++) {
                int chunkX = centerChunkX + dx;
                int chunkZ = centerChunkZ + dz;

                if (!isOwnedByDefensiveSide(chunkX, chunkZ, worldID)) {
                    continue;
                }

                int x0 = chunkX * 16;
                int z0 = chunkZ * 16;
                int y = (int) player.getLocation().getY() + 1;

                // NORTH
                if (!isOwnedByDefensiveSide(chunkX, chunkZ - 1, worldID)) {
                    ParticleUtils.drawLine(TownsAndNations.getPlugin(), player,
                            new Vector3D(x0, y, z0, worldID),
                            new Vector3D(x0 + 16, y, z0, worldID),
                            1);
                }

                // SOUTH
                if (!isOwnedByDefensiveSide(chunkX, chunkZ + 1, worldID)) {
                    ParticleUtils.drawLine(TownsAndNations.getPlugin(), player,
                            new Vector3D(x0, y, z0 + 16, worldID),
                            new Vector3D(x0 + 16, y, z0 + 16, worldID),
                            1);
                }

                // WEST
                if (!isOwnedByDefensiveSide(chunkX - 1, chunkZ, worldID)) {
                    ParticleUtils.drawLine(TownsAndNations.getPlugin(), player,
                            new Vector3D(x0, y, z0, worldID),
                            new Vector3D(x0, y, z0 + 16, worldID),
                            1);
                }

                // EAST
                if (!isOwnedByDefensiveSide(chunkX + 1, chunkZ, worldID)) {
                    ParticleUtils.drawLine(TownsAndNations.getPlugin(), player,
                            new Vector3D(x0 + 16, y, z0, worldID),
                            new Vector3D(x0 + 16, y, z0 + 16, worldID),
                            1);
                }
            }
        }
    }

    private boolean isOwnedByDefensiveSide(int x, int z, String worldID) {
        ClaimedChunk2 claimedChunk2 = NewClaimedChunkStorage.getInstance().get(x, z, worldID);

        return claimedChunk2 instanceof TerritoryChunk territoryChunk &&
                territoryChunk.getOwnerID().equals(attackData.getMainDefender().getID()) &&
                !territoryChunk.isOccupied();
    }
}
