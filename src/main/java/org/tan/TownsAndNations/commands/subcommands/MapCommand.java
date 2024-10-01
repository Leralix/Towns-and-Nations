package org.tan.TownsAndNations.commands.subcommands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.enums.MapType;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;

import java.util.*;

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
            openMap(player, MapType.TOWN);
            return;
        }
        if(args.length == 2) {
            if(Arrays.stream(MapType.values()).anyMatch(e -> e.name().equals(args[1])))
                openMap(player, MapType.valueOf(args[1]));
            return;
        }

        player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
        player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
    }

    public static void openMap(Player player, MapType type) {
        Chunk currentChunk = player.getLocation().getChunk();
        int radius = 4;
        Map<Integer,TextComponent> text = new HashMap<>();
        TextComponent claimType = new TextComponent(Lang.MAP_CLAIM_TYPE.get());
        claimType.setHoverEvent(null);
        claimType.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        text.put(-4, claimType);
        TextComponent claimButton = type.getButton();
        text.put(-3,claimButton);

        player.sendMessage("╭─────────⟢⟐⟣─────────╮");

        for (int dz = -radius; dz <= radius; dz++) {
            ComponentBuilder newLine = new ComponentBuilder();

            newLine.append("   ");
            for (int dx = -radius; dx <= radius; dx++) {
                Chunk chunk = player.getWorld().getChunkAt(currentChunk.getX() + dx, currentChunk.getZ() + dz);

                ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.get(chunk);
                TextComponent icon = claimedChunk.getMapIcon(player);
                icon.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tan claim " + type.toString().toLowerCase() + " " + chunk.getX() + " " + chunk.getZ()));

                newLine.append(icon);
            }
            if(text.containsKey(dz)){
                newLine.append(text.get(dz));
            }

            player.spigot().sendMessage(newLine.create());
        }
        player.sendMessage("╰─────────⟢⟐⟣─────────╯");
    }


}
