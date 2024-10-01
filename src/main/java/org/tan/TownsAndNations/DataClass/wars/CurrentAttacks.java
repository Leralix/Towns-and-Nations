package org.tan.TownsAndNations.DataClass.wars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.DataClass.wars.wargoals.WarGoal;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.CurrentAttacksStorage;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;

import java.util.Collection;
import java.util.UUID;

public class CurrentAttacks {

    String ID;
    private int score = 500;
    private final int maxScore = 1000;
    private long remainingTime;
    final Collection<ITerritoryData> attackers;
    final Collection<ITerritoryData> defenders;
    BossBar bossBar;
    String originalTitle;
    WarGoal warGoal;

    public CurrentAttacks(String ID, Collection<ITerritoryData> attackers, Collection<ITerritoryData> defenders, WarGoal warGoal) {
        this.ID = ID;
        this.attackers = attackers;
        this.defenders = defenders;
        this.originalTitle = "War";
        long warDuration = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("WarDuration");
        this.remainingTime = warDuration * 60 * 20;
        this.warGoal = warGoal;

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


    public CurrentAttacks(String newID, PlannedAttack plannedAttack) {
        this(newID, plannedAttack.getAttackingTerritories(), plannedAttack.getDefendingTerritories(), plannedAttack.getWarGoal());
    }

    public String getID() {
        return ID;
    }

    private void updateBossBar() {
        long hours = remainingTime / 72000;
        long minutes = (remainingTime % 72000) / 1200;
        long seconds = (remainingTime % 1200) / 20;
        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        bossBar.setTitle(Lang.TITLE_ATTACK.get(originalTitle, timeString));
        bossBar.setProgress((double) score / maxScore);
    }


    public void playerKilled(PlayerData playerData, Player killer) {

        for (ITerritoryData territoryData : attackers) {
            if (territoryData.havePlayer(playerData)) {
                if(killer != null){
                    attackingLoss();
                }
                else {
                    Location playerLocation = playerData.getPlayer().getLocation();
                    ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.get(playerLocation.getChunk());
                    if (defenders.contains(claimedChunk.getOwner())) {
                        attackingLoss();
                    }

                }

            }
        }
        for (ITerritoryData territoryData : defenders) {
            if (territoryData.havePlayer(playerData)) {
                if(killer != null){
                    defendingLoss();
                }
            }
        }
    }

    public void attackingLoss() {
        int nbAttackers = getNumberOfOnlineAttackers();
        double multiplier = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getDouble("warScoreMultiplier");
        int score = (int) (multiplier / nbAttackers * 500);
        addScore(-score);
        setTemporaryBossBarTitle("Attacking player killed!");
    }

    public void defendingLoss() {
        int nbDefenders = getNumberOfOnlineDefenders();
        double multiplier = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getDouble("warScoreMultiplier");
        int score = (int) (multiplier / nbDefenders * 500);
        addScore(score);
        setTemporaryBossBarTitle("Defensive player killed!");
    }

    private int getNumberOfOnlineDefenders() {
        int sum = 0;
        for(ITerritoryData territoryData : this.defenders) {
            for(String playerID : territoryData.getPlayerIDList()) {
                if(Bukkit.getPlayer(UUID.fromString(playerID)) != null)
                    sum++;
            }
        }
        return sum;
    }

    private int getNumberOfOnlineAttackers() {
        int sum = 0;
        for(ITerritoryData territoryData : this.attackers) {
            for(String playerID : territoryData.getPlayerIDList()) {
                if(Bukkit.getPlayer(UUID.fromString(playerID)) != null)
                    sum++;
            }
        }
        return sum;
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
        }
        if(this.score <= 0){
            this.score = 0;
        }
        updateBossBar();
    }

    private void attackerWin() {

        for(ITerritoryData territoryData : this.attackers) {
            for(PlayerData playerData : territoryData.getPlayerDataList()) {
                Player player = playerData.getPlayer();
                if(player != null){
                    warGoal.sendWinMessageForWinner(player);
                }
            }
        }
        for(ITerritoryData territoryData : this.defenders) {
            for(PlayerData playerData : territoryData.getPlayerDataList()) {
                Player player = playerData.getPlayer();
                if(player != null){
                    warGoal.sendWinMessageForLooser(player);
                }
            }
        }
        warGoal.applyWarGoal();
        bossBar.setTitle(Lang.WAR_ATTACKER_WON_ANNOUNCEMENT.get());
        endWar();
    }

    private void defenderWin(){

        for(ITerritoryData territoryData : this.attackers) {
            for(PlayerData playerData : territoryData.getPlayerDataList()) {
                Player player = playerData.getPlayer();
                if(player != null){
                    warGoal.sendFailMessageForLooser(player);
                }
            }
        }
        for(ITerritoryData territoryData : this.defenders) {
            for(PlayerData playerData : territoryData.getPlayerDataList()) {
                Player player = playerData.getPlayer();
                if(player != null) {
                    warGoal.sendFailMessageForWinner(player);
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
                    if(score >= maxScore)
                        attackerWin();
                    else
                        defenderWin();
                    this.cancel();
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
        }.runTaskLater(TownsAndNations.getPlugin(),20 * 20); //Still showing the boss bar for 30s
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


    public Collection<ITerritoryData> getDefenders() {
        return defenders;
    }
}
