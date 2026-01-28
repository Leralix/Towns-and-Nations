package org.leralix.tan.commands.player;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public final class TerritoryCommandUtil {

    private TerritoryCommandUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static List<String> getTerritoryTypeSuggestions(ITanPlayer tanPlayer) {
        List<String> suggestions = new ArrayList<>();
        if (tanPlayer.hasTown()) {
            suggestions.add("town");
        }
        if (tanPlayer.hasRegion()) {
            suggestions.add("region");
        }
        if (tanPlayer.hasNation()) {
            suggestions.add("nation");
        }
        return suggestions;
    }

    public static TerritoryData resolveTerritory(Player player, ITanPlayer tanPlayer, String territoryArg, String syntax) {
        if (territoryArg.equals("town")) {
            if (!tanPlayer.hasTown()) {
                TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(player));
                return null;
            }
            return tanPlayer.getTown();
        }
        if (territoryArg.equals("region")) {
            if (!tanPlayer.hasRegion()) {
                TanChatUtils.message(player, Lang.PLAYER_NO_REGION.get(player));
                return null;
            }
            return tanPlayer.getRegion();
        }
        if (territoryArg.equals("nation")) {
            if (!tanPlayer.hasNation()) {
                TanChatUtils.message(player, Lang.PLAYER_NO_NATION.get(player));
                return null;
            }
            return tanPlayer.getNation();
        }

        TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(syntax).getDefault());
        return null;
    }

    public static Chunk parseChunkFromArgs(Player player, String[] args, int xIndex, int zIndex, LangType langType, String syntax) {
        int x;
        int z;
        try {
            x = Integer.parseInt(args[xIndex]);
            z = Integer.parseInt(args[zIndex]);
        } catch (NumberFormatException e) {
            TanChatUtils.message(player, Lang.SYNTAX_ERROR.get(langType));
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, syntax));
            return null;
        }
        return player.getWorld().getChunkAt(x, z);
    }
}
