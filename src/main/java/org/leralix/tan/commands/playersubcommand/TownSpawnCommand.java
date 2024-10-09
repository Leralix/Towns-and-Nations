package org.leralix.tan.commands.playersubcommand;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.Lang.Lang;
import org.leralix.tan.commands.SubCommand;
import org.leralix.tan.storage.DataStorage.PlayerDataStorage;
import org.leralix.tan.storage.DataStorage.TownDataStorage;
import org.leralix.tan.storage.TeleportationRegister;

import java.util.Collections;
import java.util.List;

import static org.leralix.tan.utils.ChatUtils.getTANString;

public class TownSpawnCommand extends SubCommand {
    @Override
    public String getName() {
        return "spawn";
    }

    @Override
    public String getDescription() {
        return Lang.SPAWN_COMMAND_DESC.get();
    }
    public int getArguments(){ return 1;}


    @Override
    public String getSyntax() {
        return "/tan spawn";
    }
    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        return Collections.emptyList();
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

        TownData townData = TownDataStorage.get(player);
        //Spawn Unlocked
        if(townData.isSpawnLocked()){
            player.sendMessage(getTANString() + Lang.SPAWN_NOT_UNLOCKED.get());
            return;
        }

        //Spawn set
        if(!townData.isSpawnSet()){
            player.sendMessage(getTANString() + Lang.SPAWN_NOT_SET.get());
            return;
        }

        TeleportationRegister.teleportToTownSpawn(playerStat, townData);


    }

}


