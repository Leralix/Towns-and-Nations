package org.leralix.tan.commands.player;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.chunk.ClaimedChunk;
import org.leralix.tan.data.chunk.LandmarkClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunk;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.gui.scope.MapSettings;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public class UnclaimCommand extends PlayerSubCommand {

    private final PlayerDataStorage playerDataStorage;

    public UnclaimCommand(PlayerDataStorage playerDataStorage){
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public String getName() {
        return "unclaim";
    }

    @Override
    public String getDescription() {
        return Lang.UNCLAIM_CHUNK_COMMAND_DESC.getDefault();
    }

    public int getArguments() {
        return 1;
    }

    @Override
    public String getSyntax() {
        return "/tan unclaim <town/region/nation> <x> <z>";
    }

    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
        if (args.length == 2) {
            ITanPlayer tanPlayer = playerDataStorage.get(player);
            return TerritoryCommandUtil.getTerritoryTypeSuggestions(tanPlayer);
        }
        return new ArrayList<>();
    }

    @Override
    public void perform(Player player, String[] args) {

        ITanPlayer tanPlayer = playerDataStorage.get(player);
        LangType langType = tanPlayer.getLang();
        if (!(args.length == 1 || args.length == 4)) {
            TanChatUtils.message(player, Lang.SYNTAX_ERROR.get(langType));
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
            return;
        }

        Chunk chunk = null;
        if (args.length == 1) {
            chunk = player.getLocation().getChunk();
        }

        if (args.length == 4) {
            String territoryType = args[1].toLowerCase();
            if (!territoryType.equals("town") && !territoryType.equals("region") && !territoryType.equals("nation")) {
                TanChatUtils.message(player, Lang.SYNTAX_ERROR.get(langType));
                TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
                return;
            }
            chunk = TerritoryCommandUtil.parseChunkFromArgs(player, args, 2, 3, langType, getSyntax());
            if (chunk == null) {
                return;
            }
        }

        ClaimedChunk claimedChunk = NewClaimedChunkStorage.getInstance().get(chunk);
        if (claimedChunk instanceof TerritoryChunk territoryChunk) {
            territoryChunk.unclaimChunk(player, tanPlayer, langType);
            if (args.length == 4) {
                var mapCommand = new MapCommand(TownsAndNations.getPlugin().getPlayerDataStorage());
                mapCommand.openMap(player, new MapSettings(args[0], args[1]));
            }
        }
        else if (claimedChunk instanceof LandmarkClaimedChunk){
            TanChatUtils.message(player, Lang.CANNOT_CLAIM_LANDMARK.get(langType));
        }
        else {
            TanChatUtils.message(player, Lang.CHUNK_NOT_CLAIMED.get(langType));
        }
    }
}


