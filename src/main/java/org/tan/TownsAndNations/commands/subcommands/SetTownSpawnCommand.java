package org.tan.TownsAndNations.commands.subcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.ClaimedChunkSettings;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.enums.TownRolePermission;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class SetTownSpawnCommand extends SubCommand {
    @Override
    public String getName() {
        return "setspawn";
    }

    @Override
    public String getDescription() {
        return Lang.CLAIM_CHUNK_COMMAND_DESC.get();
    }
    public int getArguments(){ return 1;}


    @Override
    public String getSyntax() {
        return "/tan setspawn";
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
        if(!playerStat.hasPermission(TownRolePermission.UPGRADE_TOWN)){
            if(!playerStat.isTownLeader()){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }
        }



        //Spawn Unlocked
        if(townData.isSpawnLocked()){
            player.sendMessage(getTANString() + Lang.SPAWN_NOT_UNLOCKED.get());
            return;
        }


        townData.setSpawn(player.getLocation());

        player.sendMessage(getTANString() + Lang.SPAWN_SET_SUCCESS.get());
    }

}


