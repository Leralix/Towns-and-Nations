package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.data.chunk.ClaimedChunk;
import org.leralix.tan.data.chunk.TownClaimedChunk;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.data.upgrade.rewards.bool.EnableTownSpawn;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;
import java.util.List;

public class SetTownSpawnCommand extends PlayerSubCommand {

    private final PlayerDataStorage playerDataStorage;

    public SetTownSpawnCommand(PlayerDataStorage playerDataStorage){
        this.playerDataStorage = playerDataStorage;
    }


    @Override
    public String getName() {
        return "setspawn";
    }

    @Override
    public String getDescription() {
        return Lang.SET_SPAWN_COMMAND_DESC.getDefault();
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

        LangType langType = playerDataStorage.get(player).getLang();

        //Incorrect syntax
        if (args.length != 1){
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()) );
            return;
        }

        //No town
        ITanPlayer playerStat = playerDataStorage.get(player.getUniqueId().toString());
        if(!playerStat.hasTown()){
            TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(langType));
            return;
        }

        //No permission
        TownData townData = TownDataStorage.getInstance().get(playerStat);

        if(!townData.doesPlayerHavePermission(playerStat, RolePermission.TOWN_ADMINISTRATOR)){
            TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(langType));
            return;
        }


        //Spawn Unlocked
        EnableTownSpawn enableTownSpawn = townData.getNewLevel().getStat(EnableTownSpawn.class);
        if(!enableTownSpawn.isEnabled()){
            TanChatUtils.message(player, Lang.SPAWN_NOT_UNLOCKED.get(langType));
            return;
        }

        ClaimedChunk currentChunk = NewClaimedChunkStorage.getInstance().get(player.getLocation().getChunk());
        if(!(currentChunk instanceof TownClaimedChunk townChunk && townChunk.getTown().equals(townData))){
            TanChatUtils.message(player, Lang.SPAWN_NEED_TO_BE_IN_CHUNK.get(langType));
            return;
        }

        townData.setSpawn(player.getLocation());
        TanChatUtils.message(player, Lang.SPAWN_SET_SUCCESS.get(langType));
    }

}


