package org.tan.towns_and_nations.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.enums.TownRelation;
import org.tan.towns_and_nations.storage.TownDataStorage;

public class TeamUtils {
    public static void setScoreBoard(Player player) {

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();


        for (TownRelation relation : TownRelation.values()) {
            Team team = board.registerNewTeam(relation.getName().toLowerCase());
            team.setPrefix(relation.getColor() + "");
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            System.out.println("Setting prefix for team: " + relation.getName().toLowerCase() + " to: " + relation.getColor() + "");
        }
        player.setScoreboard(board);


        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            for (TownRelation relation : TownRelation.values()) {
                if (haveRelation(player, otherPlayer, relation)) {
                    player.getScoreboard().getTeam(relation.getName().toLowerCase()).addEntry(otherPlayer.getName());
                    System.out.println("Adding " + otherPlayer.getName() + " to team " + relation.getName().toLowerCase());
                }
            }
        }
    }

    private static boolean haveRelation(Player player, Player otherPlayer, TownRelation TargetedRelation){
        TownDataClass playerTown = TownDataStorage.getTown(player);
        TownDataClass otherPlayerTown = TownDataStorage.getTown(otherPlayer);

        TownRelation CurrentRelation = playerTown.getRelationWith(otherPlayerTown);

        if(CurrentRelation == null){
            return false;
        }
        return CurrentRelation == TargetedRelation;
    }


}
