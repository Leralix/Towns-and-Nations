package org.leralix.tan.commands.player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public class ClaimAreaCommand extends PlayerSubCommand {

    @Override
    public String getName() {
        return "claimarea";
    }

    @Override
    public String getDescription() {
        return Lang.CLAIM_CHUNK_COMMAND_DESC.getDefault();
    }

    public int getArguments() {
        return 1;
    }

    @Override
    public String getSyntax() {
        return "/tan claimarea <town/region/kingdom>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
            if (tanPlayer.hasTown()) {
                suggestions.add("town");
            }
            if (tanPlayer.hasRegion()) {
                suggestions.add("region");
            }
            if (tanPlayer.hasKingdom()) {
                suggestions.add("kingdom");
            }
        }
        return suggestions;
    }

    @Override
    public void perform(Player player, String[] args) {

        LangType langType = LangType.of(player);

        if (!(args.length == 2 || args.length == 4)) {
            TanChatUtils.message(player, Lang.SYNTAX_ERROR.get(langType));
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
            return;
        }

        TerritoryData territoryData;
        String territoryArg = args[1];

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

        if (territoryArg.equals("town")) {
            if (!tanPlayer.hasTown()) {
                TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(player));
                return;
            }
            territoryData = tanPlayer.getTown();
        } else if (territoryArg.equals("region")) {
            if (!tanPlayer.hasRegion()) {
                TanChatUtils.message(player, Lang.PLAYER_NO_REGION.get(player));
                return;
            }
            territoryData = tanPlayer.getRegion();
        } else if (territoryArg.equals("kingdom")) {
            if (!tanPlayer.hasKingdom()) {
                TanChatUtils.message(player, Lang.PLAYER_NO_KINGDOM.get(player));
                return;
            }
            territoryData = tanPlayer.getKingdom();
        } else {
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(getSyntax()).getDefault());
            return;
        }

        if (args.length == 4) {
            int x;
            int z;
            try {
                x = Integer.parseInt(args[2]);
                z = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                TanChatUtils.message(player, Lang.SYNTAX_ERROR.get(langType));
                TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
                return;
            }
            Chunk chunk = player.getWorld().getChunkAt(x, z);
            territoryData.claimChunk(player, chunk);
        }

        openClaimAreaMap(player, territoryArg);
    }

    public static void openClaimAreaMap(Player player, String territoryArg) {
        Chunk currentChunk = player.getLocation().getChunk();
        LangType langType = PlayerDataStorage.getInstance().get(player).getLang();
        int radius = 4;

        player.sendMessage("╭─────────⟢⟐⟣─────────╮");
        for (int dz = -radius; dz <= radius; dz++) {
            TextComponent newLine = new TextComponent();
            newLine.addExtra("   ");
            for (int dx = -radius; dx <= radius; dx++) {
                int chunkX = currentChunk.getX() + dx;
                int chunkZ = currentChunk.getZ() + dz;

                ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(chunkX, chunkZ, player.getWorld().getUID().toString());
                TextComponent icon = claimedChunk.getMapIcon(langType);

                if (dx == 0 && dz == 0) {
                    if (claimedChunk instanceof TerritoryChunk territoryChunk && territoryChunk.isOccupied()) {
                        icon.setText("🟠");
                    } else {
                        icon.setText("🌑");
                    }
                }

                icon.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tan claimarea " + territoryArg + " " + chunkX + " " + chunkZ));
                newLine.addExtra(icon);
            }
            player.spigot().sendMessage(newLine);
        }
        player.sendMessage("╰─────────⟢⟐⟣─────────╯");
        TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, "/tan claimarea " + territoryArg), SoundEnum.MINOR_GOOD);
    }
}
