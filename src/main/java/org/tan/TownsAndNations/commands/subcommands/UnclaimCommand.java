package org.tan.TownsAndNations.commands.subcommands;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
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

public class UnclaimCommand extends SubCommand {
    @Override
    public String getName() {
        return "unclaim";
    }


    @Override
    public String getDescription() {
        return Lang.UNCLAIM_CHUNK_COMMAND_DESC.getTranslation();
    }
    public int getArguments(){ return 1;}


    @Override
    public String getSyntax() {
        return "/tan unclaim";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args){
        if (args.length != 1){
            player.sendMessage(getTANString() +  Lang.CORRECT_SYNTAX_INFO.getTranslation());
            return;
        }

        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        if(!playerStat.haveTown()){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.getTranslation());
        }

        if(!playerStat.hasPermission(TownRolePermission.UNCLAIM_CHUNK)){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
            return;
        }

        TownData townStat = TownDataStorage.get(playerStat.getTownId());
        if(!townStat.getUuidLeader().equals(playerStat.getUuid())){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
        }



        Chunk chunk = player.getLocation().getChunk();
        if(ClaimedChunkStorage.isChunkClaimed(chunk)){

            if(ClaimedChunkStorage.isOwner(chunk, townStat.getID())) {
                ClaimedChunkStorage.unclaimChunk(player.getLocation().getChunk());
                TownDataStorage.get(player).getChunkSettings().decreaseNumberOfClaimedChunk();

                player.sendMessage(getTANString() + Lang.UNCLAIMED_CHUNK_SUCCESS.getTranslation(townStat.getChunkSettings().getNumberOfClaimedChunk(),townStat.getTownLevel().getChunkCap()));

                return;
            }
            TownData otherTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
            player.sendMessage(getTANString() + Lang.UNCLAIMED_CHUNK_NOT_RIGHT_TOWN.getTranslation(otherTown.getName()));

        }



    }

}


