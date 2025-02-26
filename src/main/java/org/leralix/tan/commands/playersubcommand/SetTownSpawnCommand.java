package org.leralix.tan.commands.playersubcommand;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TownClaimedChunk;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;

import java.util.Collections;
import java.util.List;

public class SetTownSpawnCommand extends PlayerSubCommand {
    @Override
    public String getName() {
        return "setspawn";
    }

    @Override
    public String getDescription() {
        return Lang.SET_SPAWN_COMMAND_DESC.get();
    }

    public int getArguments(){ return 1;}

    @Override
    public String getSyntax() {
        return "/tan setspawn";
    }
    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        return Collections.emptyList();
    }
    @Override
    public void perform(Player player, String[] args){

        //Incorrect syntax
        if (args.length != 1){
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()) );
            return;
        }

        //No town
        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        if(!playerStat.haveTown()){
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_TOWN.get());
            return;
        }

        //No permission
        TownData townData = TownDataStorage.get(player);

        if(!townData.doesPlayerHavePermission(playerStat, RolePermission.TOWN_ADMINISTRATOR)){
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            return;
        }


        //Spawn Unlocked
        if(townData.isSpawnLocked()){
            player.sendMessage(TanChatUtils.getTANString() + Lang.SPAWN_NOT_UNLOCKED.get());
            return;
        }

        ClaimedChunk2 currentChunk = NewClaimedChunkStorage.get(player.getLocation().getChunk());
        if(!(currentChunk instanceof TownClaimedChunk townChunk && townChunk.getTown().equals(townData))){
            player.sendMessage(TanChatUtils.getTANString() + Lang.SPAWN_NEED_TO_BE_IN_CHUNK.get());
            return;
        }

        townData.setSpawn(player.getLocation());
        player.sendMessage(TanChatUtils.getTANString() + Lang.SPAWN_SET_SUCCESS.get());
    }

}


