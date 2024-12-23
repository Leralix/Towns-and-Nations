package org.leralix.tan.dataclass.wars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.territory.StrongholdData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.wars.wargoals.WarGoal;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import java.util.Collection;
import java.util.UUID;

public class CurrentAttack {

    private String id;
    private int score = 500;
    private static final int MIN_SCORE = 0;

    private static final int MAX_SCORE = 1000;
    private long remainingTime;
    private final Collection<TerritoryData> attackers;
    private final Collection<TerritoryData> defenders;
    private BossBar bossBar;
    private String originalTitle;
    private final WarGoal warGoal;
    private final StrongholdData defenderStronghold;
    StrongholdListener strongholdListener;

    public CurrentAttack(String id, Collection<TerritoryData> attackers, Collection<TerritoryData> defenders, WarGoal warGoal, StrongholdData defenderStronghold) {
        this.id = id;
        this.attackers = attackers;
        this.defenders = defenders;
        this.originalTitle = "War";
        long warDuration = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("WarDuration");
        this.remainingTime = warDuration * 60 * 20;
        this.warGoal = warGoal;

        this.bossBar = Bukkit.createBossBar(this.originalTitle, BarColor.RED, BarStyle.SOLID);
        this.defenderStronghold = defenderStronghold;
        this.defenderStronghold.setHolderSide(AttackSide.DEFENDER);
        this.strongholdListener = new StrongholdListener(this, defenderStronghold);

        for(TerritoryData territoryData : this.attackers) {
            for(PlayerData playerData : territoryData.getPlayerDataList()) {
                playerData.addWar(this);
                Player player = playerData.getPlayer();
                if(player != null){
                    bossBar.addPlayer(player);
                }
            }
        }
        for(TerritoryData territoryData : this.defenders) {
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


    public CurrentAttack(String newID, PlannedAttack plannedAttack) {
        this(newID,
                plannedAttack.getAttackingTerritories(),
                plannedAttack.getDefendingTerritories(),
                plannedAttack.getWarGoal(),
                plannedAttack.getDefenderStronghold());
    }

    public String getId() {
        return id;
    }

    private void updateBossBar() {
        long hours = remainingTime / 72000;
        long minutes = (remainingTime % 72000) / 1200;
        long seconds = (remainingTime % 1200) / 20;
        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        bossBar.setTitle(Lang.TITLE_ATTACK.get(originalTitle, timeString));
        bossBar.setProgress((double) score / MAX_SCORE);
    }


    public void playerKilled(PlayerData playerData, Player killer) {
        AttackSide attackSide = getSideOfPlayer(playerData);

        if(attackSide == AttackSide.ATTACKER){
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
        if(attackSide == AttackSide.DEFENDER && killer != null){
            defendingLoss();
        }

    }

    public void attackingLoss() {
        int nbAttackers = getNumberOfOnlineAttackers();
        if(nbAttackers == 0){
            return;
        }
        double multiplier = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getDouble("warScoreMultiplier");
        int deltaScore = (int) (multiplier / nbAttackers * 500);
        addScore(-deltaScore);
        setTemporaryBossBarTitle("Attacking player killed!");
    }

    public void defendingLoss() {
        int nbDefenders = getNumberOfOnlineDefenders();
        if (nbDefenders == 0) {
            return;
        }
        double multiplier = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getDouble("warScoreMultiplier");
        int deltaScore = (int) (multiplier / nbDefenders * 500);
        addScore(deltaScore);
        setTemporaryBossBarTitle("Defensive player killed!");
    }

    private int getNumberOfOnlineDefenders() {
        int sum = 0;
        for(TerritoryData territoryData : this.defenders) {
            for(String playerID : territoryData.getPlayerIDList()) {
                if(Bukkit.getPlayer(UUID.fromString(playerID)) != null)
                    sum++;
            }
        }
        return sum;
    }

    private int getNumberOfOnlineAttackers() {
        int sum = 0;
        for(TerritoryData territoryData : this.attackers) {
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
        }.runTaskLater(TownsAndNations.getPlugin(), 5L * 20);
    }

    private void addScore(int score) {
        this.score += score;
        if (this.score >= MAX_SCORE) {
            this.score = MAX_SCORE;
        }
        if(this.score <= MIN_SCORE){
            this.score = MIN_SCORE;
        }
        updateBossBar();
    }

    private void attackerWin() {

        for(TerritoryData territoryData : this.attackers) {
            for(PlayerData playerData : territoryData.getPlayerDataList()) {
                Player player = playerData.getPlayer();
                if(player != null){
                    warGoal.sendAttackSuccessToAttackers(player);
                }
            }
        }
        for(TerritoryData territoryData : this.defenders) {
            for(PlayerData playerData : territoryData.getPlayerDataList()) {
                Player player = playerData.getPlayer();
                if(player != null){
                    warGoal.sendAttackSuccessToDefenders(player);
                }
            }
        }
        warGoal.applyWarGoal();
        bossBar.setTitle(Lang.WAR_ATTACKER_WON_ANNOUNCEMENT.get());
        endWar();
    }

    private void defenderWin(){

        for(TerritoryData territoryData : this.attackers) {
            for(PlayerData playerData : territoryData.getPlayerDataList()) {
                Player player = playerData.getPlayer();
                if(player != null){
                    warGoal.sendAttackFailedToDefender(player);
                }
            }
        }
        for(TerritoryData territoryData : this.defenders) {
            for(PlayerData playerData : territoryData.getPlayerDataList()) {
                Player player = playerData.getPlayer();
                if(player != null) {
                    warGoal.sendAttackFailedToAttacker(player);
                }
            }
        }

        bossBar.setTitle(Lang.WAR_DEFENDER_WON_ANNOUNCEMENT.get());
        endWar();
    }

    public void addPlayer(PlayerData playerData) {
        Player player = playerData.getPlayer();
        if(player != null && remainingTime > 0 && score > 0 && score < MAX_SCORE){
            bossBar.addPlayer(player);
        }
    }

    private void startTimer() {
        BukkitRunnable timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (remainingTime > 0 && score > 0 && score < MAX_SCORE) {
                    remainingTime--;
                    updateBossBar();
                } else {
                    if(score >= MAX_SCORE)
                        attackerWin();
                    else
                        defenderWin();
                    this.cancel();
                    strongholdListener.stop();
                }
            }
        };
        timerTask.runTaskTimer(TownsAndNations.getPlugin(), 0, 1); // Exécute toutes les secondes
    }


    private void endWar() {

        new BukkitRunnable() {
            @Override
            public void run() {
                for(TerritoryData territoryData : attackers) {
                    for(PlayerData playerData : territoryData.getPlayerDataList()) {
                        playerData.removeWar(CurrentAttack.this);
                    }
                }
                for(TerritoryData territoryData : defenders) {
                    for(PlayerData playerData : territoryData.getPlayerDataList()) {
                        playerData.removeWar(CurrentAttack.this);
                    }
                }

                attackers.clear();
                defenders.clear();
                bossBar.removeAll();
                bossBar = null;
                id = null;
                CurrentAttacksStorage.remove(CurrentAttack.this);

                for(TerritoryData territoryData : attackers) {
                    territoryData.removeCurrentAttack(CurrentAttack.this);
                }
                for(TerritoryData territoryData : defenders) {
                    territoryData.removeCurrentAttack(CurrentAttack.this);
                }

            }
        }.runTaskLater(TownsAndNations.getPlugin(),20L * 20); //Still showing the boss bar for 20s
    }

    public boolean containsPlayer(PlayerData playerData) {
        for(TerritoryData territoryData : attackers) {
            if(territoryData.isPlayerIn(playerData)){
                return true;
            }
        }
        for(TerritoryData territoryData : defenders) {
            if(territoryData.isPlayerIn(playerData)){
                return true;
            }
        }
        return false;
    }


    public Collection<TerritoryData> getDefenders() {
        return defenders;
    }

    public void strongholdHeldBy(AttackSide attackSide) {
        if(attackSide == AttackSide.ATTACKER){
            setTemporaryBossBarTitle("Stronghold held by attackers");
        }
        if(attackSide == AttackSide.DEFENDER){
            setTemporaryBossBarTitle("Stronghold held by defenders");
        }
    }

    public AttackSide getSideOfPlayer(PlayerData playerData) {
        for (TerritoryData territoryData : attackers) {
            if (territoryData.isPlayerIn(playerData)) {
                return AttackSide.ATTACKER;
            }
        }
        for (TerritoryData territoryData : defenders) {
            if (territoryData.isPlayerIn(playerData)){
                return AttackSide.DEFENDER;
            }
        }
        return AttackSide.NOT_PARTICIPATING;
    }

    public void addScoreOfStronghold() {
        AttackSide attackSide = this.defenderStronghold.getHolderSide();

        if(attackSide == AttackSide.ATTACKER){
            addScore(5);
        }
        if(attackSide == AttackSide.DEFENDER){
            addScore(-5);
        }
    }
}
