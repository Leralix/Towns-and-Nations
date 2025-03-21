package org.leralix.tan.commands.server;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;

import java.util.Collections;
import java.util.List;

class QuitTownServer extends SubCommand {


    @Override
    public String getName() {
        return "quittown";
    }

    @Override
    public String getDescription() {
        return Lang.QUIT_TOWN_SERVER_DESC.get();
    }

    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public String getSyntax() {
        return "/tanserver quittown <player_username>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(CommandSender commandSender, String currentMessage, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if(args.length < 2){
            commandSender.sendMessage(Lang.INVALID_ARGUMENTS.get());
            return;
        }


        Player p = commandSender.getServer().getPlayer(args[1]);
        if(p == null){
            commandSender.sendMessage(Lang.PLAYER_NOT_FOUND.get());
            return;
        }
        PlayerData playerData = PlayerDataStorage.getInstance().get(p);
        TownData townData = playerData.getTown();
        if(townData == null){
            commandSender.sendMessage(Lang.PLAYER_NO_TOWN.get());
            return;
        }
        if(townData.isLeader(playerData)){
            commandSender.sendMessage(Lang.LEADER_CANNOT_QUIT_TOWN.get());
            return;
        }
        townData.removePlayer(playerData);
    }
}
