package org.tan.TownsAndNations.commands.playerSubCommand;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;

import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class UnclaimCommand extends SubCommand {
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
        return null;
    }
    @Override
    public void perform(Player player, String[] args){
        if (args.length != 1){
            player.sendMessage(getTANString() +  Lang.CORRECT_SYNTAX_INFO.get());
            return;
        }

        Chunk chunk = player.getLocation().getChunk();
        if(!NewClaimedChunkStorage.isChunkClaimed(chunk)){
            player.sendMessage(getTANString() + Lang.CHUNK_NOT_CLAIMED.get());
            return;
        }
        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.get(chunk);
        claimedChunk.unclaimChunk(player);
    }
}


