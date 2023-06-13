package org.tan.towns_and_nations.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.DataClass.ClaimedChunkDataClass;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.storage.ClaimedChunkStorage;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;

import static org.tan.towns_and_nations.utils.ChatUtils.getTANString;

public class ClaimCommand extends SubCommand {
    @Override
    public String getName() {
        return "claim";
    }


    @Override
    public String getDescription() {
        return "claim a chunk";
    }
    public int getArguments(){ return 1;}


    @Override
    public String getSyntax() {
        return "/tan claim";
    }

    @Override
    public void perform(Player player, String[] args){
        if (args.length != 1){
            player.sendMessage(getTANString() +  " Correct Syntax: " + getSyntax());
            return;
        }

        PlayerDataClass playerStat = PlayerStatStorage.getStatUUID(player.getUniqueId().toString());
        if(!playerStat.haveTown()){
            player.sendMessage(getTANString() + " You do not have a Town");
        }

        TownDataClass townStat = TownDataStorage.getTown(playerStat.getTownId());
        if(!townStat.getUuidLeader().equals(playerStat.getUuid())){
            player.sendMessage(getTANString() + " You are not the leader of your town. For now, only the leader of a town can claim");
        }
        Chunk chunk = player.getLocation().getChunk();
        if(ClaimedChunkStorage.isChunkClaimed(chunk)){
            player.sendMessage(getTANString() + " This chunk is already claimed by: " + ChatColor.GREEN + ClaimedChunkStorage.getChunkOwner(chunk));
        }

        ClaimedChunkStorage.claimChunk(player.getLocation().getChunk(),townStat.getTownId());
        player.sendMessage(getTANString() + " Chunk claimed !");

    }

}


