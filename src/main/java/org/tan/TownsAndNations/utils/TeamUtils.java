package org.tan.TownsAndNations.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;


public class TeamUtils {

    public static void updateAllScoreboardColor(){
        if(!TownsAndNations.colorCodeIsEnabled()){
            return;
        }
        for(Player player : Bukkit.getOnlinePlayers()){
            setIndividualScoreBoard(player);
        }
    }

    public static void setIndividualScoreBoard(Player player) {
        if(!TownsAndNations.colorCodeIsEnabled()){
            return;
        }

        if(!ConfigUtil.getCustomConfig("config.yml").getBoolean("EnablePlayerColorCode")){
            return;
        }

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

        for (TownRelation relation : TownRelation.values()) {
            Team team = board.getTeam(relation.getName().toLowerCase());

            if (team == null) {
                team = board.registerNewTeam(relation.getName().toLowerCase());
            }

            team.setColor(relation.getColor());
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        }
        player.setScoreboard(board);

        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if(PlayerDataStorage.get(otherPlayer).getTownId() != null){
                addPlayerToCorrectTeam(otherPlayer.getScoreboard(), otherPlayer, player);
                addPlayerToCorrectTeam(player.getScoreboard(), player, otherPlayer);
            }
        }
    }

    public static void addPlayerToCorrectTeam(Scoreboard scoreboard, Player player, Player toAdd) {

        if(PlayerDataStorage.get(toAdd).getTownId() == null)
            return;

        if(!PlayerDataStorage.get(player).haveTown())
            return;

        for (TownRelation relation : TownRelation.values()) {
            if (haveRelation(player, toAdd, relation)) {
                Team playerTeam = scoreboard.getTeam(relation.getName().toLowerCase());
                if(playerTeam == null){ //Player did not have a town when he logged in. No team was created for him.
                    TeamUtils.setIndividualScoreBoard(player);
                    playerTeam = scoreboard.getTeam(relation.getName().toLowerCase());
                }
                playerTeam.addEntry(toAdd.getName());


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
