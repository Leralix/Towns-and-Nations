package org.tan.TownsAndNations.DataClass.wars;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.tan.TownsAndNations.DataClass.AttackData;
import org.tan.TownsAndNations.DataClass.PlayerData;
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

        bossBar = Bukkit.createBossBar("war", BarColor.RED, BarStyle.SEGMENTED_10);
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
        addScore(-100);
    }
    public void attackPlayerKilled(){
        addScore(100);
    }

    private void addScore(int score) {
        this.score += score;
        updateBossBar();
        if (this.score >= maxScore) {
            attackerWin();
        }
        if(this.score <= 0){
            defenderWin();
        }
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
    }


}
