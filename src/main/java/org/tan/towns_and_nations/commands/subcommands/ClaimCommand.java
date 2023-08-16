package org.tan.towns_and_nations.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.DataClass.ClaimedChunkSettings;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.Lang.Lang;
import org.tan.towns_and_nations.TownsAndNations;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.enums.TownRolePermission;
import org.tan.towns_and_nations.storage.ClaimedChunkStorage;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;

import java.util.ArrayList;
import java.util.List;

import static org.tan.towns_and_nations.utils.ChatUtils.getTANString;

public class ClaimCommand extends SubCommand {
    @Override
    public String getName() {
        return "claim";
    }

    @Override
    public String getDescription() {
        return Lang.CLAIM_CHUNK_COMMAND_DESC.getTranslation();
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
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.getTranslation(getSyntax()) );
            return;
        }

        //No town
        PlayerDataClass playerStat = PlayerStatStorage.getStat(player.getUniqueId().toString());
        if(!playerStat.haveTown()){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.getTranslation());
            return;
        }

        //No permission
        TownDataClass townStat = TownDataStorage.getTown(player);
        ClaimedChunkSettings townChunkInfo = townStat.getChunkSettings();
        if(!playerStat.hasPermission(TownRolePermission.CLAIM_CHUNK)){
            if(!playerStat.isTownLeader()){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION);
                return;
            }
        }

        //Chunk already claimed
        Chunk chunkToClaim = player.getLocation().getChunk();
        if(ClaimedChunkStorage.isChunkClaimed(chunkToClaim)){
            player.sendMessage(getTANString() + Lang.CHUNK_ALREADY_CLAIMED_WARNING.getTranslation(
                    ClaimedChunkStorage.getChunkOwnerName(chunkToClaim)));
            return;
        }

        //Chunk limit reached
        if(!townStat.canClaimMoreChunk()){
            player.sendMessage(getTANString() + Lang.MAX_CHUNK_LIMIT_REACHED.getTranslation());
        }

        ClaimedChunkStorage.claimChunk(chunkToClaim,townStat.getTownId());
        townChunkInfo.incrementNumberOfClaimedChunk();

        player.sendMessage(getTANString() + Lang.CHUNK_CLAIMED_SUCCESS.getTranslation(
                townChunkInfo.getNumberOfClaimedChunk(),
                townStat.getTownLevel().getChunkCap()));
    }

}


