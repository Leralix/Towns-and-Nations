package org.leralix.tan.utils.graphic;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;

public class TeamUtils {

  private TeamUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static void updateAllScoreboardColor() {
    if (Constants.enableColorUsernames()) {
      for (Player player : Bukkit.getOnlinePlayers()) setIndividualScoreBoard(player);
    }
  }

  public static void setIndividualScoreBoard(Player player) {
    org.leralix.tan.utils.FoliaScheduler.runTask(
        TownsAndNations.getPlugin(), () -> setIndividualScoreBoardSync(player));
  }

  private static void setIndividualScoreBoardSync(Player player) {
    if (!Constants.enableColorUsernames()) return;

    ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

    Scoreboard board = scoreboardManager.getNewScoreboard();

    for (TownRelation relation : TownRelation.values()) {
      Team team = board.getTeam(relation.getName(Lang.getServerLang()).toLowerCase());

      if (team == null) {
        team = board.registerNewTeam(relation.getName(Lang.getServerLang()).toLowerCase());
      }

      team.setColor(
          org.leralix.tan.utils.text.ComponentUtil.toLegacyChatColor(relation.getColor()));
      team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
    }
    player.setScoreboard(board);

    for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
      PlayerDataStorage.getInstance()
          .get(otherPlayer)
          .thenAccept(
              otherPlayerData -> {
                if (otherPlayerData.hasTown()) {
                  addPlayerToCorrectTeam(otherPlayer, player);
                  if (!otherPlayer.getUniqueId().equals(player.getUniqueId()))
                    addPlayerToCorrectTeam(player, otherPlayer);
                }
              });
    }
  }

  public static void addPlayerToCorrectTeam(Player player, Player playerToAdd) {

    Scoreboard scoreboard = player.getScoreboard();
    if (!PlayerDataStorage.getInstance().get(playerToAdd).join().hasTown()
        || !PlayerDataStorage.getInstance().get(player).join().hasTown()) return;

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player).join();
    ITanPlayer playerToAddData = PlayerDataStorage.getInstance().get(playerToAdd).join();
    TownRelation relation = tanPlayer.getRelationWithPlayerSync(playerToAddData);
    if (relation == null) return;

    Team playerTeam = scoreboard.getTeam(relation.getName(Lang.getServerLang()).toLowerCase());
    if (playerTeam == null) {
      TeamUtils.setIndividualScoreBoard(player);
      playerTeam = scoreboard.getTeam(relation.getName(Lang.getServerLang()).toLowerCase());
    }
    if (playerTeam == null) return;
    playerTeam.addEntry(playerToAdd.getName());
  }
}
