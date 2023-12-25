package org.tan.TownsAndNations.commands.debugsubcommands;

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

public class UnclaimDebugCommand extends SubCommand {
    @Override
    public String getName() {
        return "unclaim";
    }


    @Override
    public String getDescription() {
        return "unclaim any chunk";
    }
    public int getArguments(){ return 1;}


    @Override
    public String getSyntax() {
        return "/tandebug unclaim";
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


        Chunk chunk = player.getLocation().getChunk();
        if(ClaimedChunkStorage.isChunkClaimed(chunk)){

            TownData townData = ClaimedChunkStorage.getChunkOwnerTown(chunk);

            ClaimedChunkStorage.unclaimChunk(chunk);
            townData.getChunkSettings().decreaseNumberOfClaimedChunk();

            player.sendMessage(getTANString() + Lang.DEBUG_UNCLAIMED_CHUNK_SUCCESS.getTranslation(townData.getName(), townData.getChunkSettings().getNumberOfClaimedChunk(),townData.getTownLevel().getChunkCap()));

            return;
        }
        TownData otherTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
        player.sendMessage(getTANString() + Lang.UNCLAIMED_CHUNK_NOT_RIGHT_TOWN.getTranslation(otherTown.getName()));

    }
}

