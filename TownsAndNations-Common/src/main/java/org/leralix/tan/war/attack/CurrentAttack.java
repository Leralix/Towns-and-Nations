package org.leralix.tan.war.attack;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.AttackEndedInternalEvent;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.utils.gameplay.CommandExecutor;
import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.info.AttackResultCompleted;
import org.leralix.tan.war.info.AttackResultCounter;

/**
 * Represents an ongoing attack between territories.
 * A currentAttack is created when a planned attack starts and is removed when it ends.
 * Data will be transferred to the PlannedAttack object when the attack ends to keep a record of the attack.
 */
public abstract class CurrentAttack {

    /**
     *  Data of the planned attack related to this current attack
     */
    protected final PlannedAttack attackData;

    /**
     * Indicates if the attack has ended
     */
    protected boolean end;


    /**
     * Boss bar showing the remaining time
     */
    protected final BossBar bossBar;

    protected final AttackResultCounter attackResultCounter;

    /**
     * Constructor of CurrentAttack
     * @param plannedAttack the planned attack data
     */
    public CurrentAttack(PlannedAttack plannedAttack) {

        this.attackData = plannedAttack;
        this.attackResultCounter = new AttackResultCounter();
        this.end = false;

        this.bossBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
        applyBossBar(plannedAttack);

        CommandExecutor.applyStartAttackCommands(getAttackData());
        start();
    }

    public AttackResultCounter getAttackResultCounter() {
        return attackResultCounter;
    }

    private void applyBossBar(PlannedAttack plannedAttack) {
        for (TerritoryData territoryData : plannedAttack.getWar().getAttackingTerritories()) {
            for (ITanPlayer tanPlayer : territoryData.getITanPlayerList()) {
                Player player = tanPlayer.getPlayer();
                if (player != null) {
                    bossBar.addPlayer(player);
                }
            }
        }
        for (TerritoryData territoryData : plannedAttack.getWar().getDefendingTerritories()) {
            for (ITanPlayer tanPlayer : territoryData.getITanPlayerList()) {
                Player player = tanPlayer.getPlayer();
                if (player != null) {
                    bossBar.addPlayer(player);
                }
            }
        }
    }

    protected abstract void updateBossBar();

    public void addPlayer(ITanPlayer tanPlayer) {
        Player player = tanPlayer.getPlayer();
        if (player != null) {
            bossBar.addPlayer(player);
        }
    }

    private void start() {
        BukkitRunnable timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (shouldContinue() && !end) {
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

    protected abstract boolean shouldContinue();


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
                for (TerritoryData territoryData : attackData.getWar().getAttackingTerritories()) {
                    for (ITanPlayer tanPlayer : territoryData.getITanPlayerList()) {
                        tanPlayer.removeWar(CurrentAttack.this);
                    }
                }
                for (TerritoryData territoryData : attackData.getWar().getDefendingTerritories()) {
                    for (ITanPlayer tanPlayer : territoryData.getITanPlayerList()) {
                        tanPlayer.removeWar(CurrentAttack.this);
                    }
                }

                bossBar.removeAll();
                CurrentAttacksStorage.remove(CurrentAttack.this);
            }
        }.runTaskLater(TownsAndNations.getPlugin(), 20L * 20); //Still showing the boss bar for 20s
    }

    public boolean containsPlayer(ITanPlayer tanPlayer) {
        for (TerritoryData territoryData : attackData.getWar().getAttackingTerritories()) {
            if (territoryData.isPlayerIn(tanPlayer)) {
                return true;
            }
        }
        for (TerritoryData territoryData : attackData.getWar().getDefendingTerritories()) {
            if (territoryData.isPlayerIn(tanPlayer)) {
                return true;
            }
        }
        return false;
    }


    public PlannedAttack getAttackData() {
        return attackData;
    }

    public void attackerKilled() {
        attackResultCounter.incrementAttackersKilled();
    }

    public void defenderKilled() {
        attackResultCounter.incrementDefendersKilled();
    }
}
