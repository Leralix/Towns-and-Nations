package org.tan.towns_and_nations.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.TownsAndNations;
import org.tan.towns_and_nations.enums.TownRelation;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class TeamUtils {
    public static void setScoreBoard(Player player) {

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();


        for (TownRelation relation : TownRelation.values()) {
            Team team = board.registerNewTeam(relation.getName().toLowerCase());
            team.setColor(relation.getColor());
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            System.out.println("Setting prefix for team: " + relation.getName().toLowerCase() + " to: " + relation.getColor() + "");
        }
        player.setScoreboard(board);


        // Add online players to the new player's scoreboard
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if(PlayerStatStorage.getStat(otherPlayer).getTownId() != null){
                addPlayerToCorrectTeam(player.getScoreboard(), player, otherPlayer);
                addPlayerToCorrectTeam(otherPlayer.getScoreboard(), otherPlayer, player);
            }


        }
        /* Old code
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            System.out.println("Case: " + otherPlayer.getName());
            for (TownRelation relation : TownRelation.values()) {
                System.out.println("Relation: " + relation.getName());
                if (haveRelation(player, otherPlayer, relation)) {
                    player.getScoreboard().getTeam(relation.getName().toLowerCase()).addEntry(otherPlayer.getName());
                    System.out.println("Adding " + otherPlayer.getName() + " to team " + relation.getName().toLowerCase());
                }
            }
        }
        *
         */
    }

    public static void addPlayerToCorrectTeam(Scoreboard scoreboard, Player owner, Player toAdd) {
        for (TownRelation relation : TownRelation.values()) {
            System.out.println("Relation: " + relation.getName());
            if (haveRelation(owner, toAdd, relation)) {
                scoreboard.getTeam(relation.getName().toLowerCase()).addEntry(toAdd.getName());
                System.out.println("Adding " + toAdd.getName() + " to team " + relation.getName().toLowerCase());
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
