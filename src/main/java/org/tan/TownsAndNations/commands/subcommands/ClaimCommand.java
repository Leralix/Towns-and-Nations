package org.tan.TownsAndNations.commands.subcommands;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.ClaimedChunkSettings;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.enums.TownRolePermission;
import org.tan.TownsAndNations.storage.ClaimedChunkStorage;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;

import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class ClaimCommand extends SubCommand {
    @Override
    public String getName() {
        return "claim";
    }

    @Override
    public String getDescription() {
        return Lang.CLAIM_CHUNK_COMMAND_DESC.get();
    }
    public int getArguments(){ return 1;}


    @Override
    public String getSyntax() {
        return "/tan claim";
    }
    @Override
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args){

        //Incorrect syntax
        if (args.length != 1){
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()) );
            return;
        }

        //No town
        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        if(!playerStat.haveTown()){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.get());
            return;
        }

        //No permission
        TownData townData = TownDataStorage.get(player);
        ClaimedChunkSettings townChunkInfo = townData.getChunkSettings();
        if(!playerStat.hasPermission(TownRolePermission.CLAIM_CHUNK)){
            if(!playerStat.isTownLeader()){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }
        }

        //Chunk already claimed
        Chunk chunkToClaim = player.getLocation().getChunk();
        if(ClaimedChunkStorage.isChunkClaimed(chunkToClaim)){
            player.sendMessage(getTANString() + Lang.CHUNK_ALREADY_CLAIMED_WARNING.get(
                    ClaimedChunkStorage.getChunkOwnerName(chunkToClaim)));
            return;
        }

        //Chunk limit reached
        if(!townData.canClaimMoreChunk()){
            player.sendMessage(getTANString() + Lang.MAX_CHUNK_LIMIT_REACHED.get());
            return;
        }

        if(townData.getNumberOfClaimedChunk() == 0){

            ClaimedChunkStorage.claimChunk(chunkToClaim,townData.getID());
            townData.addNumberOfClaimChunk(1);

            player.sendMessage(getTANString() + Lang.CHUNK_CLAIMED_SUCCESS.get(
                    townData.getNumberOfClaimedChunk(),
                    townData.getTownLevel().getChunkCap())
            );
            return;
        }

        if(!ClaimedChunkStorage.isAdjacentChunkClaimedBySameTown(chunkToClaim,townData.getID())){
            player.sendMessage(getTANString() + Lang.CHUNK_NOT_ADJACENT.get());
            return;
        }

        ClaimedChunkStorage.claimChunk(chunkToClaim,townData.getID());
        townData.addNumberOfClaimChunk(1);

        player.sendMessage(getTANString() + Lang.CHUNK_CLAIMED_SUCCESS.get(
                townData.getNumberOfClaimedChunk(),
                townData.getTownLevel().getChunkCap())
        );
    }

}


