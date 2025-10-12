package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.TeleportationRegister;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;
import java.util.List;

public class TownSpawnCommand extends PlayerSubCommand {
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
        return Collections.emptyList();
    }

    @Override
    public void perform(Player player, String[] args) {

        LangType langType = PlayerDataStorage.getInstance().get(player).getLang();
        //Incorrect syntax
        if (args.length != 1) {
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
            return;
        }

        //No town
        ITanPlayer playerStat = PlayerDataStorage.getInstance().get(player.getUniqueId().toString());
        if (!playerStat.hasTown()) {
            TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(langType));
            return;
        }

        TownData townData = TownDataStorage.getInstance().get(player);
        //Spawn Unlocked
        if (townData.isSpawnLocked()) {
            TanChatUtils.message(player, Lang.SPAWN_NOT_UNLOCKED.get(langType));
            return;
        }

        //Spawn set
        if (!townData.isSpawnSet()) {
            TanChatUtils.message(player, Lang.SPAWN_NOT_SET.get(langType));
            return;
        }

        TeleportationRegister.teleportToTownSpawn(playerStat, townData);


    }

}


