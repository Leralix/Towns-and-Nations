package org.leralix.tan.commands.player;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.enums.MapSettings;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public class UnclaimCommand extends PlayerSubCommand {
    @Override
    public String getName() {
        return "unclaim";
    }


    @Override
    public String getDescription() {
        return Lang.UNCLAIM_CHUNK_COMMAND_DESC.getDefault();
    }
    public int getArguments(){ return 1;}


    @Override
    public String getSyntax() {
        return "/tan unclaim <town/region/kingdom> <x> <z>";
    }
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        if (args.length == 2) {
            ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
            return TerritoryCommandUtil.getTerritoryTypeSuggestions(tanPlayer);
        }
        return new ArrayList<>();
    }
    @Override
    public void perform(Player player, String[] args){

        LangType langType = PlayerDataStorage.getInstance().get(player).getLang();
        if (!(args.length == 1 || args.length == 4)) {
            TanChatUtils.message(player, Lang.SYNTAX_ERROR.get(langType));
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()) );
            return;
        }

        Chunk chunk = null;
        if (args.length == 1){
            chunk = player.getLocation().getChunk();
        }

        if(args.length == 4){
            String territoryType = args[1].toLowerCase();
            if (!territoryType.equals("town") && !territoryType.equals("region") && !territoryType.equals("kingdom")) {
                TanChatUtils.message(player, Lang.SYNTAX_ERROR.get(langType));
                TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
                return;
            }
            chunk = TerritoryCommandUtil.parseChunkFromArgs(player, args, 2, 3, langType, getSyntax());
            if (chunk == null) {
                return;
            }
        }

        if(!NewClaimedChunkStorage.getInstance().isChunkClaimed(chunk)){
            TanChatUtils.message(player, Lang.CHUNK_NOT_CLAIMED.get(langType));
            return;
        }
        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(chunk);
        claimedChunk.unclaimChunk(player);
        if(args.length == 4){
            MapCommand.openMap(player, new MapSettings(args[0], args[1]));
        }
    }
}


