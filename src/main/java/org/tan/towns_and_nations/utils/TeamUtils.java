package org.tan.towns_and_nations.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.enums.TownRelation;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;


public class TeamUtils {
    public static void setScoreBoard(Player player) {

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();


        for (TownRelation relation : TownRelation.values()) {
            Team team = board.registerNewTeam(relation.getName().toLowerCase());
            team.setColor(relation.getColor());
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        }
        player.setScoreboard(board);


        // Add online players to the new player's scoreboard
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if(PlayerStatStorage.getStat(otherPlayer).getTownId() != null){
                addPlayerToCorrectTeam(player.getScoreboard(), player, otherPlayer);
                addPlayerToCorrectTeam(otherPlayer.getScoreboard(), otherPlayer, player);
            }


        }

    }

    public static void addPlayerToCorrectTeam(Scoreboard scoreboard, Player owner, Player toAdd) {
        for (TownRelation relation : TownRelation.values()) {
            if (haveRelation(owner, toAdd, relation)) {
                scoreboard.getTeam(relation.getName().toLowerCase()).addEntry(toAdd.getName());
            }
        }
    }







    private static boolean haveRelation(Player player, Player otherPlayer, TownRelation TargetedRelation){

        TownDataClass playerTown = TownDataStorage.getTown(player);
        TownDataClass otherPlayerTown = TownDataStorage.getTown(otherPlayer);

        if(otherPlayerTown == null){
            return false;
        }

        TownRelation CurrentRelation = playerTown.getRelationWith(otherPlayerTown);

        if(CurrentRelation == null){
            return false;
        }

        return CurrentRelation == TargetedRelation;
    }

}
