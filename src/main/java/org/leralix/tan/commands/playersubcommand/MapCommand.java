package org.leralix.tan.commands.playersubcommand;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.enums.ClaimAction;
import org.leralix.tan.enums.MapSettings;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.enums.ClaimType;

import java.util.*;

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
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        return new ArrayList<>();
    }

    @Override
    public void perform(Player player, String[] args) {
        if(args.length == 1) {
            openMap(player, new MapSettings());
            return;
        }
        if(args.length == 3) {
            openMap(player, new MapSettings(args[1],args[2]));
            return;
        }

        player.sendMessage(TanChatUtils.getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
        player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
    }

    public static void openMap(Player player, MapSettings settings) {
        Chunk currentChunk = player.getLocation().getChunk();
        int radius = 4;
        Map<Integer,TextComponent> text = new HashMap<>();
        TextComponent claimType = new TextComponent(Lang.MAP_CLAIM_TYPE.get());
        claimType.setHoverEvent(null);
        claimType.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        text.put(-4, claimType);
        TextComponent typeButton = settings.getMapTypeButton();
        text.put(-3,typeButton);
        TextComponent actionButton = settings.getClaimTypeButton();
        text.put(-2,actionButton);

        player.sendMessage("╭─────────⟢⟐⟣─────────╮");
        for (int dz = -radius; dz <= radius; dz++) {
            ComponentBuilder newLine = new ComponentBuilder();
            newLine.append("   ");
            for (int dx = -radius; dx <= radius; dx++) {
                Chunk chunk = player.getWorld().getChunkAt(currentChunk.getX() + dx, currentChunk.getZ() + dz);

                ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.get(chunk);
                TextComponent icon = claimedChunk.getMapIcon(player);
                ClaimAction claimAction = settings.getClaimActionType();
                ClaimType mapType = settings.getClaimType();
                icon.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tan " + claimAction.toString().toLowerCase() + " " + mapType.toString().toLowerCase() + " " + chunk.getX() + " " + chunk.getZ()));

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
