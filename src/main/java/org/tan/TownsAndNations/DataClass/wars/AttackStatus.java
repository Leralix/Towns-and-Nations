package org.tan.TownsAndNations.DataClass.wars;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;
import org.tan.TownsAndNations.DataClass.AttackData;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.AttackStatusStorage;

import java.util.Collection;

public class AttackStatus {

    String ID;
    private int score = 500;
    private int maxScore = 1000;
    Collection<PlayerData> attackersID;
    Collection<PlayerData> defendersID;
    BossBar bossBar;

    public AttackStatus(String ID, Collection<PlayerData> attackers, Collection<PlayerData> defenders) {
        this.ID = ID;
        this.attackersID = attackers;
        this.defendersID = defenders;

        bossBar = Bukkit.createBossBar("war", BarColor.RED, BarStyle.SOLID);
        updateBossBar();

        for(PlayerData playerData : attackersID) {
            bossBar.addPlayer(playerData.getPlayer());
            playerData.getPlayer().sendMessage("Attack start");
            playerData.addWar(this);
        }
        for(PlayerData playerData : defendersID) {
            bossBar.addPlayer(playerData.getPlayer());
            playerData.getPlayer().sendMessage("Attack start");
            playerData.addWar(this);
        }
    }

    public AttackStatus(String newID, AttackData attackData) {
        this(newID, attackData.getAttackersPlayers(), attackData.getDefendingPlayers());
    }

    public String getID() {
        return ID;
    }

    private void updateBossBar() {
        System.out.println("BossbarScore : " + (double) (score / maxScore));
        bossBar.setProgress((double) score / maxScore);
    }

    public void playerKilled(PlayerData playerData) {
        System.out.println("Player killed ! " + playerData.getName());
        if(attackersID.contains(playerData)){
            attackPlayerKilled();
        } else if (defendersID.contains(playerData)){
            defensivePlayerKilled();
        }
    }

    public void defensivePlayerKilled(){
        int nbDefenders = defendersID.size();
        int score = 1/nbDefenders * 500;

        addScore(score);
    }
    public void attackPlayerKilled(){
        int nbAttackers = attackersID.size();
        int score = 1/nbAttackers * 500;

        addScore(-score);
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
        Collection<PlayerData> attackers = attackersID;
        for(PlayerData playerData : attackers) {
            playerData.getPlayer().sendMessage("You have won the war!");
        }
        Collection<PlayerData> defenders  = defendersID;
        for (PlayerData playerData : defenders) {
            playerData.getPlayer().sendMessage("You have lost the war!");
        }
        bossBar.setTitle("Attackers win !");
        endWar();
    }

    private void defenderWin(){
        Collection<PlayerData> attackers = attackersID;
        for(PlayerData playerData : attackers) {
            playerData.getPlayer().sendMessage("You have lost the war!");
        }
        Collection<PlayerData> defenders  = defendersID;
        for (PlayerData playerData : defenders) {
            playerData.getPlayer().sendMessage("You have won the war!");
        }
        bossBar.setTitle("Defenders win !");

        endWar();
    }

    private void endWar() {

        new BukkitRunnable() {
            @Override
            public void run() {
                for(PlayerData playerData : attackersID) {
                    playerData.removeWar(AttackStatus.this);
                }
                for(PlayerData playerData : defendersID) {
                    playerData.removeWar(AttackStatus.this);
                }
                bossBar.removeAll();
                AttackStatusStorage.remove(AttackStatus.this);
            }
        }.runTaskLater(TownsAndNations.getPlugin(),30 * 20); //30 seconds
    }

}
