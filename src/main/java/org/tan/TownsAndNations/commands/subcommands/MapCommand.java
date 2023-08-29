package org.tan.TownsAndNations.commands.subcommands;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.ClaimedChunkStorage;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;
import org.tan.TownsAndNations.storage.TownInviteDataStorage;

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
                if (ClaimedChunkStorage.isChunkClaimed(chunk)) { // isClaimed est une méthode hypothétique que vous devez créer
                    claimedChunks.add(chunk);
                }
            }

            for (int dx = -radius; dx <= radius; dx++) {
                StringBuilder line = new StringBuilder();
                for (int dz = -radius; dz <= radius; dz++) {
                    Chunk chunk = player.getWorld().getChunkAt(currentChunk.getX() + dx, currentChunk.getZ() + dz);
                    if (claimedChunks.contains(chunk)) {
                        line.append("■");
                    } else {
                        line.append("□");
                    }
                }
                player.sendMessage(line.toString());
            }

        }
        else {
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.getTranslation());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.getTranslation(getSyntax()));
        }
    }





}
