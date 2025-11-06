package org.leralix.tan.wars.capture;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.wars.fort.Fort;

public class CaptureFort {

  private final String warId;
  private final Fort fort;
  private final TerritoryData attackingTerritory;

  private String title;
  private int score;
  private final int maxScore = 60;
  private final List<Player> attackers;
  private final List<Player> defenders;
  private final BossBar bossBar;

  public CaptureFort(Fort fort, TerritoryData attackingTerritory, String warId) {
    this.fort = fort;
    this.score = 0;
    this.attackingTerritory = attackingTerritory;
    this.attackers = new ArrayList<>();
    this.defenders = new ArrayList<>();
    updateTitle(0, 0);
    this.bossBar = Bukkit.createBossBar(this.title, BarColor.RED, BarStyle.SEGMENTED_10);
    this.warId = warId;
  }

  private void updateTitle(int nbAttackers, int nbDefenders) {
    this.title =
        Lang.WAR_INFO_FORT_STATUS.get(
            Lang.getServerLang(),
            fort.getName(),
            Integer.toString(nbAttackers),
            Integer.toString(nbDefenders));
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
    if (score > maxScore) {
      score = maxScore;
      fort.setOccupier(attackingTerritory);
      fort.updateFlag();
    } else if (score < 0) {
      score = 0;
      fort.liberate();
      fort.updateFlag();
    }
    updateBossBar(nbAttackers, nbDefenders);
  }

  private void updateBossBar(int nbAttackers, int nbDefenders) {
    bossBar.removeAll();

    updateTitle(nbAttackers, nbDefenders);

    bossBar.setTitle(title);
    bossBar.setProgress((double) score / maxScore);
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
    return warId;
  }
}
