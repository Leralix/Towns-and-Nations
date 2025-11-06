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

/** Utility class for handling teams for scoreboard color coding */
public class TeamUtils {

  private TeamUtils() {
    throw new IllegalStateException("Utility class");
  }

  /** Update the color of all the scoreboards */
  public static void updateAllScoreboardColor() {
    if (Constants.enableColorUsernames()) {
      for (Player player : Bukkit.getOnlinePlayers()) setIndividualScoreBoard(player);
    }
  }

  /**
   * Set the color of the scoreboard of a player
   *
   * @param player The player to set the scoreboard color of
   */
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

      if (PlayerDataStorage.getInstance().getSync(otherPlayer).hasTown()) {
        addPlayerToCorrectTeam(otherPlayer, player);
        if (!otherPlayer
            .getUniqueId()
            .equals(player.getUniqueId())) // If player is not himself, no need to do it twice
        addPlayerToCorrectTeam(player, otherPlayer);
      }
    }
  }

  /**
   * Add a player to the correct team
   *
   * @param player The player's scoreboard
   * @param playerToAdd The player to add to the team scoreboard
   */
  public static void addPlayerToCorrectTeam(Player player, Player playerToAdd) {

    Scoreboard scoreboard = player.getScoreboard();
    if (!PlayerDataStorage.getInstance().getSync(playerToAdd).hasTown()
        || !PlayerDataStorage.getInstance().getSync(player).hasTown()) return;

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    ITanPlayer playerToAddData = PlayerDataStorage.getInstance().getSync(playerToAdd);
    TownRelation relation = tanPlayer.getRelationWithPlayerSync(playerToAddData);
    if (relation == null) return;

    Team playerTeam = scoreboard.getTeam(relation.getName(Lang.getServerLang()).toLowerCase());
    if (playerTeam
        == null) { // Player did not have a town when he logged in. No team was created for him.
      TeamUtils.setIndividualScoreBoard(player);
      playerTeam = scoreboard.getTeam(relation.getName(Lang.getServerLang()).toLowerCase());
    }
    if (playerTeam == null) return;
    playerTeam.addEntry(playerToAdd.getName());
  }
}
