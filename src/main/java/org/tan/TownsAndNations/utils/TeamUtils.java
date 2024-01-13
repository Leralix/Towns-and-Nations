package org.tan.TownsAndNations.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;


public class TeamUtils {

    public static void updateColor(){
        for(Player player : Bukkit.getOnlinePlayers()){
            setScoreBoard(player);
        }
    }

    public static void setScoreBoard(Player player) {

        Scoreboard board = player.getScoreboard();
        for (TownRelation relation : TownRelation.values()) {
            Team team = board.getTeam(relation.getName().toLowerCase());

            // Si l'équipe n'existe pas déjà, créez-la
            if (team == null) {
                team = board.registerNewTeam(relation.getName().toLowerCase());
            }

            team.setColor(relation.getColor());
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        }
        player.setScoreboard(board);

        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if(PlayerDataStorage.get(otherPlayer).getTownId() != null){
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

        TownData playerTown = TownDataStorage.get(player);
        TownData otherPlayerTown = TownDataStorage.get(otherPlayer);

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
