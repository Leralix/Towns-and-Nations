package org.leralix.tan.commands.playersubcommand;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.enums.MapSettings;
import org.leralix.tan.lang.Lang;

import java.util.Collections;
import java.util.List;

public class UnclaimCommand extends PlayerSubCommand {
    @Override
    public String getName() {
        return "unclaim";
    }


    @Override
    public String getDescription() {
        return Lang.UNCLAIM_CHUNK_COMMAND_DESC.get();
    }
    public int getArguments(){ return 1;}


    @Override
    public String getSyntax() {
        return "/tan unclaim";
    }
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        return Collections.emptyList();
    }
    @Override
    public void perform(Player player, String[] args){

        if (!(args.length == 1 || args.length == 4)) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.SYNTAX_ERROR.get());
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()) );
            return;
        }

        Chunk chunk = null;
        if (args.length == 1){
            chunk = player.getLocation().getChunk();
        }

        if(args.length == 4){
            int x = Integer.parseInt(args[2]);
            int y = Integer.parseInt(args[3]);
            chunk = player.getLocation().getWorld().getChunkAt(x, y);
        }

        if(!NewClaimedChunkStorage.isChunkClaimed(chunk)){
            player.sendMessage(TanChatUtils.getTANString() + Lang.CHUNK_NOT_CLAIMED.get());
            return;
        }
        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.get(chunk);
        claimedChunk.unclaimChunk(player);
        if(args.length == 4){
            MapCommand.openMap(player, new MapSettings(args[0], args[1]));
        }
    }
}


