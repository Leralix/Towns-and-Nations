package org.tan.TownsAndNations.DataClass.wars;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.CurrentAttacksStorage;

import java.util.Collection;

public class CurrentAttacks {

    String ID;
    private int score = 500;
    private final int maxScore = 1000;
    private long remainingTime;
    Collection<ITerritoryData> attackers;
    Collection<ITerritoryData> defenders;
    BossBar bossBar;
    String originalTitle;

    public CurrentAttacks(String ID, Collection<ITerritoryData> attackers, Collection<ITerritoryData> defenders) {
        this.ID = ID;
        this.attackers = attackers;
        this.defenders = defenders;
        this.originalTitle = "War";
        this.remainingTime = 120; // 6 minutes

        bossBar = Bukkit.createBossBar(this.originalTitle, BarColor.RED, BarStyle.SOLID);
        for(ITerritoryData territoryData : this.attackers) {
            for(PlayerData playerData : territoryData.getPlayerDataList()) {
                playerData.addWar(this);
                Player player = playerData.getPlayer();
                if(player != null){
                    bossBar.addPlayer(player);
                }
            }
        }
        for(ITerritoryData territoryData : this.defenders) {
            for(PlayerData playerData : territoryData.getPlayerDataList()) {
                playerData.addWar(this);
                Player player = playerData.getPlayer();
                if(player != null){
                    bossBar.addPlayer(player);
                }
            }
        }
        startTimer();
    }


    public CurrentAttacks(String newID, AttackInvolved attackInvolved) {
        this(newID, attackInvolved.getAttackingTerritory(), attackInvolved.getDefendingTerritory());
    }

    public String getID() {
        return ID;
    }

    private void updateBossBar() {
        long hours = remainingTime / 72000;
        long minutes = (remainingTime % 72000) / 1200;
        long seconds = (remainingTime % 1200) / 20;
        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        bossBar.setTitle(Lang.TITLE_WAR.get(originalTitle, timeString));
        bossBar.setProgress((double) score / maxScore);
    }


    public void playerKilled(PlayerData playerData) {

        for (ITerritoryData territoryData : attackers) {
            if (territoryData.havePlayer(playerData)) {
                int nbAttackers = attackers.size();
                int score = 200 / nbAttackers;
                addScore(-score);
                setTemporaryBossBarTitle("Attacking player killed!");
            }
        }
        for (ITerritoryData territoryData : defenders) {
            if (territoryData.havePlayer(playerData)) {
                int nbDefenders = defenders.size();
                int score = 200 / nbDefenders;
                addScore(score);
                setTemporaryBossBarTitle("Defensive player killed!");
            }
        }
    }

    private void setTemporaryBossBarTitle(String title) {
        originalTitle = "War - " + title;
        new BukkitRunnable() {
            @Override
            public void run() {
                originalTitle = "War";
            }
        }.runTaskLater(TownsAndNations.getPlugin(), 5 * 20);
    }

    private void addScore(int score) {
        this.score += score;
        if (this.score >= maxScore) {
            this.score = maxScore;
            attackerWin();
        }
        if(this.score <= 0){
            this.score = 0;
            defenderWin();
        }
        updateBossBar();
    }

    private void attackerWin() {

        for(ITerritoryData territoryData : this.attackers) {
            for(PlayerData playerData : territoryData.getPlayerDataList()) {
                Player player = playerData.getPlayer();
                if(player != null){
                    player.sendMessage(Lang.PLAYER_WON_WAR.get());
                }
            }
        }
        for(ITerritoryData territoryData : this.defenders) {
            for(PlayerData playerData : territoryData.getPlayerDataList()) {
                Player player = playerData.getPlayer();
                if(player != null){
                    player.sendMessage(Lang.PLAYER_LOST_WAR.get());
                }
            }
        }

        bossBar.setTitle(Lang.WAR_ATTACKER_WON_ANNOUNCEMENT.get());
        endWar();
    }

    private void defenderWin(){

        for(ITerritoryData territoryData : this.attackers) {
            for(PlayerData playerData : territoryData.getPlayerDataList()) {
                Player player = playerData.getPlayer();
                if(player != null){
                    player.sendMessage("You have lost the war!");
                }
            }
        }
        for(ITerritoryData territoryData : this.defenders) {
            for(PlayerData playerData : territoryData.getPlayerDataList()) {
                Player player = playerData.getPlayer();
                if(player != null) {
                    playerData.getPlayer().sendMessage("You have won the war!");
                }
            }
        }

        bossBar.setTitle(Lang.WAR_DEFENDER_WON_ANNOUNCEMENT.get());
        endWar();
    }

    public void addPlayer(PlayerData playerData) {
        Player player = playerData.getPlayer();
        if(player != null && remainingTime > 0 && score > 0 && score < maxScore){
            bossBar.addPlayer(player);
        }
    }

    private void startTimer() {
        BukkitRunnable timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (remainingTime > 0 && score > 0 && score < maxScore) {
                    remainingTime--;
                    updateBossBar();
                } else {
                    this.cancel();
                    defenderWin();
                }
            }
        };
        timerTask.runTaskTimer(TownsAndNations.getPlugin(), 0, 1); // Exécute toutes les secondes
    }


    private void endWar() {

        new BukkitRunnable() {
            @Override
            public void run() {
                for(ITerritoryData territoryData : attackers) {
                    for(PlayerData playerData : territoryData.getPlayerDataList()) {
                        playerData.removeWar(CurrentAttacks.this);
                    }
                }
                for(ITerritoryData territoryData : defenders) {
                    for(PlayerData playerData : territoryData.getPlayerDataList()) {
                        playerData.removeWar(CurrentAttacks.this);
                    }
                }

                attackers.clear();
                defenders.clear();
                bossBar.removeAll();
                bossBar = null;
                ID = null;
                CurrentAttacksStorage.remove(CurrentAttacks.this);

                for(ITerritoryData territoryData : attackers) {
                    territoryData.removeCurrentAttack(CurrentAttacks.this);
                }
                for(ITerritoryData territoryData : defenders) {
                    territoryData.removeCurrentAttack(CurrentAttacks.this);
                }

            }
        }.runTaskLater(TownsAndNations.getPlugin(),20 * 20); //30 seconds
    }

    public boolean containsPlayer(PlayerData playerData) {
        for(ITerritoryData territoryData : attackers) {
            if(territoryData.havePlayer(playerData)){
                return true;
            }
        }
        for(ITerritoryData territoryData : defenders) {
            if(territoryData.havePlayer(playerData)){
                return true;
            }
        }
        return false;
    }
}
