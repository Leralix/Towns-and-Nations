package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.chunk.IClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunk;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.data.upgrade.rewards.bool.EnableTownSpawn;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.NationStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionStorage;
import org.leralix.tan.storage.stored.TownStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;
import java.util.List;

public class SetSpawnCommand extends AbstractSpawnCommand {

    public SetSpawnCommand(
            PlayerDataStorage playerDataStorage,
            TownStorage townStorage,
            RegionStorage regionDataStorage,
            NationStorage nationDataStorage
    ) {
       super(playerDataStorage, townStorage, regionDataStorage, nationDataStorage);
    }

    @Override
    public String getName() {
        return "setspawn";
    }

    @Override
    public String getDescription() {
        return Lang.SET_SPAWN_COMMAND_DESC.getDefault();
    }

    public int getArguments() {
        return 1;
    }

    @Override
    public String getSyntax() {
        return "/tan setspawn <territory type>";
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


        ITanPlayer playerStat = playerDataStorage.get(player.getUniqueId());
        LangType langType = playerStat.getLang();

        //Incorrect syntax if not 1 or 2 arguments
        if (!(args.length == 1 || args.length == 2)) {
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
            return;
        }

        var optTerritory = getTerritoryDataFromArgs(player, playerStat, args);
        if(optTerritory.isEmpty()){
            return;
        }

        //No permission
        Territory territoryData = optTerritory.get();

        if (!territoryData.doesPlayerHavePermission(playerStat, RolePermission.TOWN_ADMINISTRATOR)) {
            TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(langType));
            return;
        }


        //Spawn Unlocked
        EnableTownSpawn enableTownSpawn = territoryData.getNewLevel().getStat(EnableTownSpawn.class);
        if (!enableTownSpawn.isEnabled()) {
            TanChatUtils.message(player, Lang.SPAWN_NOT_UNLOCKED.get(langType));
            return;
        }

        IClaimedChunk currentChunk = TownsAndNations.getPlugin().getClaimStorage().get(player.getLocation().getChunk());
        if (!(currentChunk instanceof TerritoryChunk territoryChunk && territoryChunk.getOwner().equals(territoryData))) {
            TanChatUtils.message(player, Lang.SPAWN_NEED_TO_BE_IN_CHUNK.get(langType, territoryData.getColoredName()));
            return;
        }

        territoryData.getTeleportationData().setPosition(player.getLocation());
        TanChatUtils.message(player, Lang.SPAWN_SET_SUCCESS.get(langType));
    }

}


