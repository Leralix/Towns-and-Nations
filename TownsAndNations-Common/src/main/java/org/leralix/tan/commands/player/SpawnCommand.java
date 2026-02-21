package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.upgrade.rewards.bool.EnableTownSpawn;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.TeleportationRegister;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;
import java.util.List;

public class SpawnCommand extends AbstractSpawnCommand {

    public SpawnCommand(PlayerDataStorage playerDataStorage, TownDataStorage townDataStorage){
        super(playerDataStorage, townDataStorage);
    }

    @Override
    public String getName() {
        return "spawn";
    }

    @Override
    public String getDescription() {
        return Lang.SPAWN_COMMAND_DESC.getDefault();
    }

    public int getArguments() {
        return 1;
    }


    @Override
    public String getSyntax() {
        return "/tan spawn";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
        if(args.length == 2){
            return List.of("town", "region", "nation");
        }
        return Collections.emptyList();
    }

    @Override
    public void perform(Player player, String[] args) {

        ITanPlayer playerData = playerDataStorage.get(player);
        LangType langType = playerData.getLang();
        //Incorrect syntax
        if (!(args.length == 1 || args.length == 2)) {
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
            return;
        }


        var optTerritory = getTerritoryDataFromArgs(player, playerData, args);
        if(optTerritory.isEmpty()){
            return;
        }

        TerritoryData territoryData = optTerritory.get();
        EnableTownSpawn enableTownSpawn = territoryData.getNewLevel().getStat(EnableTownSpawn.class);
        //Spawn Unlocked
        if (!enableTownSpawn.isEnabled()) {
            return;
        }

        //Spawn set
        if (!territoryData.isSpawnSet()) {
            TanChatUtils.message(player, Lang.SPAWN_NOT_SET.get(langType));
            return;
        }

        TeleportationRegister.teleportToTownSpawn(playerData, territoryData);


    }

}


