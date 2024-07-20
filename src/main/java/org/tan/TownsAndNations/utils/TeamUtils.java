package org.tan.TownsAndNations.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

/**
 * Utility class for handling teams for scoreboard color coding
 */
public class TeamUtils {
    /**
     * Update the color of all the scoreboards
     */
    public static void updateAllScoreboardColor(){
        if(TownsAndNations.colorCodeIsNotEnabled())
            return;
        for(Player player : Bukkit.getOnlinePlayers())
            setIndividualScoreBoard(player);
    }

    /**
     * Set the color of the scoreboard of a player
     * @param player    The player to set the scoreboard color of
     */

    public static void setIndividualScoreBoard(Player player) {
        if(TownsAndNations.colorCodeIsNotEnabled())
            return;

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
                addPlayerToCorrectTeam(otherPlayer, player);
                if(!otherPlayer.getUniqueId().equals(player.getUniqueId())) //If player is not himself, no need to do it twice
                    addPlayerToCorrectTeam(player, otherPlayer);
            }
        }
    }

    /**
     * Add a player to the correct team
     * @param player        The player's scoreboard
     * @param toAdd         The player to add to the team scoreboard
     */
    public static void addPlayerToCorrectTeam(Player player, Player toAdd) {

        Scoreboard scoreboard = player.getScoreboard();
        if(!PlayerDataStorage.get(toAdd).haveTown() || !PlayerDataStorage.get(player).haveTown())
            return;


        TownRelation relation = getRelation(player, toAdd);
        if(relation == null)
            return;

        Team playerTeam = scoreboard.getTeam(relation.getName().toLowerCase());
        if(playerTeam == null){ //Player did not have a town when he logged in. No team was created for him.
            TeamUtils.setIndividualScoreBoard(player);
            playerTeam = scoreboard.getTeam(relation.getName().toLowerCase());
        }
        playerTeam.addEntry(toAdd.getName());
    }

    /**
     * Check if two players have a specific relation
     * @param player            The player to check the relation of. Player must have a town
     * @param otherPlayer       The other player to check the relation with. Player must have a town
     * @return                  True if the players have the specific relation, false otherwise
     */
    private static TownRelation getRelation(Player player, Player otherPlayer){

        TownData playerTown = TownDataStorage.get(player);
        TownData otherPlayerTown = TownDataStorage.get(otherPlayer);



        TownRelation currentRelation = playerTown.getRelationWith(otherPlayerTown);


        //If no relation, check if maybe they are from the same region
        if(currentRelation == null && playerTown.haveRegion() && otherPlayerTown.haveRegion()){
            currentRelation = playerTown.getRegion().getRelationWith(otherPlayerTown.getRegion());
        }

        return currentRelation;
    }

}
