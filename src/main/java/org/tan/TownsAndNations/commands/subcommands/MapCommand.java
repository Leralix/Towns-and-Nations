package org.tan.TownsAndNations.commands.subcommands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.territoryData.RegionData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.DataClass.newChunkData.RegionClaimedChunk;
import org.tan.TownsAndNations.DataClass.newChunkData.TownClaimedChunk;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import java.awt.*;
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
        return Lang.MAP_COMMAND_DESC.get();
    }
    public int getArguments() {
        return 1;
    }
    @Override
    public String getSyntax() {
        return "/tan map";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return new ArrayList<>();
    }

    @Override
    public void perform(Player player, String[] args) {


        if(args.length == 1) {
            openMap(player);
        }
        else {
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }

    public static void openMap(Player player) {
        Chunk currentChunk = player.getLocation().getChunk();

        int radius = 4;
        List<Chunk> nearbyChunks = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                Chunk chunk = player.getWorld().getChunkAt(currentChunk.getX() + dx, currentChunk.getZ() + dz);
                nearbyChunks.add(chunk);
            }
        }


        player.sendMessage("▬▬▬▬▬↑O↑▬▬▬▬▬");

        for (int dx = -radius; dx <= radius; dx++) {
            ComponentBuilder newLine = new ComponentBuilder();

            newLine.append("   ");
            for (int dz = -radius; dz <= radius; dz++) {
                Chunk chunk = player.getWorld().getChunkAt(currentChunk.getX() + dx, currentChunk.getZ() + dz);

                ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.get(chunk);
                TextComponent icon = claimedChunk.getMapIcon(player);
                icon.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tan claim town " + chunk.getX() + " " + chunk.getZ()));

                newLine.append(icon);
            }
            player.spigot().sendMessage(newLine.create());
        }
        player.sendMessage("▬▬▬▬▬↓E↓▬▬▬▬▬");
    }


}
