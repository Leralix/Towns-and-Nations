package org.tan.towns_and_nations.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.Lang.Lang;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.storage.ClaimedChunkStorage;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;

import static org.tan.towns_and_nations.utils.ChatUtils.getTANString;

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

    @Override
    public void perform(Player player, String[] args){
        if (args.length != 1){
            player.sendMessage(getTANString() +  Lang.CORRECT_SYNTAX_INFO.getTranslation());
            return;
        }

        PlayerDataClass playerStat = PlayerStatStorage.getStat(player.getUniqueId().toString());
        if(!playerStat.haveTown()){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.getTranslation());
        }

        TownDataClass townStat = TownDataStorage.getTown(playerStat.getTownId());
        if(!townStat.getUuidLeader().equals(playerStat.getUuid())){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
        }

        Chunk chunk = player.getLocation().getChunk();
        if(ClaimedChunkStorage.isChunkClaimed(chunk)){

            if(ClaimedChunkStorage.isOwner(chunk, townStat.getTownId())) {
                ClaimedChunkStorage.unclaimChunk(player.getLocation().getChunk());
                TownDataStorage.getTown(player).getChunkSettings().decreaseNumberOfClaimedChunk();

                player.sendMessage(getTANString() + Lang.UNCLAIMED_CHUNK_SUCCESS.getTranslation(townStat.getChunkSettings().getNumberOfClaimedChunk(),townStat.getTownLevel().getChunkCap()));

                return;
            }
            TownDataClass otherTown = TownDataStorage.getTown(ClaimedChunkStorage.getChunkOwner(chunk));
            player.sendMessage(getTANString() + Lang.UNCLAIMED_CHUNK_NOT_RIGHT_TOWN.getTranslation(otherTown.getTownName()));

        }



    }

}


