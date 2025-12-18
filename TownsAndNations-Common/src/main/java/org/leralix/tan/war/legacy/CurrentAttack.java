package org.leralix.tan.war.legacy;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.AttackEndedInternalEvent;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.utils.gameplay.CommandExecutor;
import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.cosmetic.ShowBoundaries;
import org.leralix.tan.war.info.AttackResultCompleted;
import org.leralix.tan.war.info.AttackResultCounter;

/**
 * Represents an ongoing attack between territories.
 * A currentAttack is created when a planned attack starts and is removed when it ends.
 * Data will be transferred to the PlannedAttack object when the attack ends to keep a record of the attack.
 */
public class CurrentAttack {

    /**
     *  Data of the planned attack related to this current attack
     */
    private final PlannedAttack attackData;

    /**
     * Indicates if the attack has ended
     */
    private boolean end;

    /**
     * total time, in tick
     */
    private final long totalTime;
    /**
     * Remaining time, in tick
     */
    private long remaining;
    /**
     * Boss bar showing the remaining time
     */
    private final BossBar bossBar;

    private final AttackResultCounter attackResultCounter;

    /**
     * Constructor of CurrentAttack
     * @param plannedAttack the planned attack data
     * @param startTime     the start time in epoch milliseconds
     * @param endTime       the end time in epoch milliseconds
     */
    public CurrentAttack(PlannedAttack plannedAttack, long startTime, long endTime) {

        this.attackData = plannedAttack;
        this.attackResultCounter = new AttackResultCounter();
        this.end = false;

        //Conversion from ms to ticks
        this.totalTime = (endTime - startTime) / 50;
        this.remaining = totalTime;

        this.bossBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
        applyBossBar(plannedAttack);

        CommandExecutor.applyStartAttackCommands(getAttackData());
        start();
    }

    public AttackResultCounter getAttackResultCounter() {
        return attackResultCounter;
    }

    private void applyBossBar(PlannedAttack plannedAttack) {
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
        timerTask.runTaskTimer(TownsAndNations.getPlugin(), 0, 1); // Execute every tick (20/s)
    }


    public void end() {

        //If the current attack already ended, do not repeat end logic.
        if(end){
            return;
        }

        CommandExecutor.applyEndWarCommands(getAttackData());
        end = true;
        AttackResultCompleted result = attackResultCounter.buildResult();
        attackData.end(result);

        EventManager.getInstance().callEvent(
                new AttackEndedInternalEvent(
                        getAttackData().getWar().getMainAttacker(),
                        getAttackData().getWar().getMainDefender(),
                        result
                )
        );

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

    public void attackerKilled() {
        attackResultCounter.incrementAttackersKilled();
    }

    public void defenderKilled() {
        attackResultCounter.incrementDefendersKilled();
    }
}
