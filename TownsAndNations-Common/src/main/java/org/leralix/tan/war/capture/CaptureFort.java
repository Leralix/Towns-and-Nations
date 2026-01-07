package org.leralix.tan.war.capture;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.war.fort.Fort;
import org.leralix.tan.war.legacy.CurrentAttack;

import java.util.ArrayList;
import java.util.List;

public class CaptureFort {

    private final Fort fort;
    private final CurrentAttack currentAttack;

    private String title;
    private int score;
    private final List<Player> attackers;
    private final List<Player> defenders;
    private final BossBar bossBar;

    public CaptureFort(Fort fort, CurrentAttack currentAttack) {
        this.fort = fort;
        this.score = 0;
        this.currentAttack = currentAttack;
        this.attackers = new ArrayList<>();
        this.defenders = new ArrayList<>();
        updateTitle(0, 0);
        this.bossBar = Bukkit.createBossBar(this.title, BarColor.RED, BarStyle.SEGMENTED_10);
    }

    private void updateTitle(int nbAttackers, int nbDefenders) {
        this.title = Lang.WAR_INFO_FORT_STATUS.get(Lang.getServerLang(), fort.getName(), Integer.toString(nbAttackers), Integer.toString(nbDefenders));
    }

    public Fort getFort() {
        return fort;
    }

    public void clearPlayers() {
        attackers.clear();
        defenders.clear();
    }

    public void addAttacker(Player player) {
        attackers.add(player);
    }

    public void addDefender(Player player) {
        defenders.add(player);
    }

    public void update() {
        int nbAttackers = attackers.size();
        int nbDefenders = defenders.size();
        if (nbAttackers > nbDefenders) {
            score++;
        } else if (nbDefenders > nbAttackers) {
            score--;
        }
        if (score > Constants.getFortCaptureTime()) {
            score = Constants.getFortCaptureTime();
            if(!fort.isOccupied()){
                fort.setOccupier(currentAttack.getAttackData().getWar().getMainAttacker());

                currentAttack.getAttackResultCounter().incrementFortsCaptured();
            }

        } else if (score < 0) {
            score = 0;
            if(fort.isOccupied()){
                fort.liberate();
                currentAttack.getAttackResultCounter().decrementFortsCaptured();
            }
        }
        updateBossBar(nbAttackers, nbDefenders);
    }

    private void updateBossBar(int nbAttackers, int nbDefenders) {
        bossBar.removeAll();

        updateTitle(nbAttackers, nbDefenders);

        bossBar.setTitle(title);
        bossBar.setProgress((double) score / Constants.getFortCaptureTime());
        List<Player> allPlayers = new ArrayList<>(attackers);
        allPlayers.addAll(defenders);
        for (Player player : allPlayers) {
            bossBar.addPlayer(player);
        }
    }


    public void warOver() {
        bossBar.removeAll();
        fort.liberate();
    }

    public String getWarID() {
        return currentAttack.getAttackData().getWar().getID();
    }
}
