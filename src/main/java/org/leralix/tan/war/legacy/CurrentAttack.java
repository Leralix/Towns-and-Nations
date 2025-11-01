package org.leralix.tan.war.legacy;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.utils.FoliaScheduler;
import org.leralix.tan.utils.gameplay.CommandExecutor;
import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.cosmetic.ShowBoundaries;

public class CurrentAttack {

    private final PlannedAttack attackData;
    private boolean end;

    /**
     * total time, in tick
     */
    private final long totalTime;
    /**
     * Remaning time, in tick
     */
    private long remaining;
    private final BossBar bossBar;
    private ScheduledTask timerTask;

    public CurrentAttack(PlannedAttack plannedAttack, long startTime, long endTime) {

        this.attackData = plannedAttack;

        this.end = false;

        //Conversion from ms to ticks
        this.totalTime = (endTime - startTime) / 50;
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
        CommandExecutor.applyStartWarCommands(getAttackData());
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
        FoliaScheduler.runTaskTimer(TownsAndNations.getPlugin(), () -> {
            if (remaining > 0 && !end) {
                remaining--;
                updateBossBar();
            }
            else {
                end();
                if (timerTask != null) {
                    timerTask.cancel();
                }
            }
        }, 1, 1); // Execute every tick (20/s)
    }


    public void end() {

        CommandExecutor.applyEndWarCommands(getAttackData());
        end = true;

        FoliaScheduler.runTaskLater(TownsAndNations.getPlugin(), () -> {
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
        }, 20L * 20); //Still showing the boss bar for 20s
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


    public PlannedAttack getAttackData() {
        return attackData;
    }

    public void displayBoundaries() {
        for (Player player : attackData.getAllOnlinePlayers()) {
            if(player != null){
                ShowBoundaries.display(player);
            }
        }
    }


}
