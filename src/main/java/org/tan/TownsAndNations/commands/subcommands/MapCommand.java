package org.tan.TownsAndNations.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;

import java.util.ArrayList;
import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class MapCommand extends SubCommand {

    @Override
    public String getName() {
        return "map";
    }


    @Override
    public String getDescription() {
        return "";
    }

    public int getArguments() {
        return 1;
    }


    @Override
    public String getSyntax() {
        return "/tan map ";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return new ArrayList<String>();
    }

    @Override
    public void perform(Player player, String[] args) {


        if(args.length == 1) {
            Chunk currentChunk = player.getLocation().getChunk();

            int radius = 4;
            List<Chunk> nearbyChunks = new ArrayList<>();
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    Chunk chunk = player.getWorld().getChunkAt(currentChunk.getX() + dx, currentChunk.getZ() + dz);
                    nearbyChunks.add(chunk);
                }
            }

            List<Chunk> claimedChunks = new ArrayList<>();
            for (Chunk chunk : nearbyChunks) {
                if (NewClaimedChunkStorage.isChunkClaimed(chunk)) {
                    claimedChunks.add(chunk);
                }
            }

            player.sendMessage("▬▬▬▬▬↑O↑▬▬▬▬▬");

            for (int dx = -radius; dx <= radius; dx++) {
                StringBuilder line = new StringBuilder();


                line.append("   ");


                for (int dz = -radius; dz <= radius; dz++) {
                    Chunk chunk = player.getWorld().getChunkAt(currentChunk.getX() + dx, currentChunk.getZ() + dz);
                    if (claimedChunks.contains(chunk)) {

                        TownData playerTown = TownDataStorage.get(player);
                        TownData otherTown = TownDataStorage.get(NewClaimedChunkStorage.get(chunk).getID());

                        TownRelation relation;
                        if(playerTown == null ){
                            relation = TownRelation.NEUTRAL;                        }
                        else{
                            relation = playerTown.getRelationWith(otherTown);
                        }

                        ChatColor townColor;
                        if(relation == null){
                            townColor = ChatColor.WHITE;
                        }
                        else{
                            townColor = relation.getColor();
                        }


                        if (dx == 0 && dz == 0) {
                            line.append(townColor + "★");
                        }
                        else{
                            line.append(townColor + "■");
                        }

                    } else {
                        if (dx == 0 && dz == 0) {
                            line.append(ChatColor.WHITE + "★");
                        }
                        else{
                            line.append(ChatColor.WHITE + "□");
                        }
                    }
                }

                line.append("   ");


                player.sendMessage(line.toString());
            }

            player.sendMessage("▬▬▬▬▬↓E↓▬▬▬▬▬");

        }
        else {
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }





}
