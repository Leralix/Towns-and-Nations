package org.tan.towns_and_nations.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
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
        return "un-claim a chunk";
    }
    public int getArguments(){ return 1;}


    @Override
    public String getSyntax() {
        return "/tan unclaim";
    }

    @Override
    public void perform(Player player, String[] args){
        if (args.length != 1){
            player.sendMessage(getTANString() +  " Correct Syntax: " + getSyntax());
            return;
        }

        PlayerDataClass playerStat = PlayerStatStorage.getStat(player.getUniqueId().toString());
        if(!playerStat.haveTown()){
            player.sendMessage(getTANString() + " You do not have a Town");
        }

        TownDataClass townStat = TownDataStorage.getTown(playerStat.getTownId());
        if(!townStat.getUuidLeader().equals(playerStat.getUuid())){
            player.sendMessage(getTANString() + " You are not the leader of your town. For now, only the leader of a town can un-claim");
        }
        Chunk chunk = player.getLocation().getChunk();
        if(ClaimedChunkStorage.isChunkClaimed(chunk)){

            if(ClaimedChunkStorage.isOwner(chunk, townStat.getTownId())) {
                ClaimedChunkStorage.unclaimChunk(player.getLocation().getChunk());
                TownDataStorage.getTown(player).getChunkSettings().decreaseNumberOfClaimedChunk();
                player.sendMessage(getTANString() + " Chunk unclaimed ! Current number of chunk: " + ChatColor.YELLOW + townStat.getChunkSettings().getNumberOfClaimedChunk());

                return;
            }
            player.sendMessage(getTANString() + " This chunk is claimed by: " + ChatColor.GREEN + ClaimedChunkStorage.getChunkOwner(chunk)+ " , not your town");
        }



    }

}


