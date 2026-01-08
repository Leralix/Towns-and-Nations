package org.leralix.tan.commands.player;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.MapSettings;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public class ClaimCommand extends PlayerSubCommand {
    @Override
    public String getName() {
        return "claim";
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
        return "/tan claim <town/region/kingdom>";
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

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

        if (args[1].equals("town")) {
            if (!tanPlayer.hasTown()) {
                TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(player));
                return;
            }
            territoryData = tanPlayer.getTown();
        } else if (args[1].equals("region")) {
            if (!tanPlayer.hasRegion()) {
                TanChatUtils.message(player, Lang.PLAYER_NO_REGION.get(player));
                return;
            }
            territoryData = tanPlayer.getRegion();
        } else if (args[1].equals("kingdom")) {
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
            int x = Integer.parseInt(args[2]);
            int z = Integer.parseInt(args[3]);
            Chunk chunk = player.getWorld().getChunkAt(x, z);
            territoryData.claimChunk(player, chunk);
            MapCommand.openMap(player, new MapSettings(args[0], args[1]));
        } else {
            territoryData.claimChunk(player);
        }
    }


}


