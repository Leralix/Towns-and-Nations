package org.leralix.tan.commands.server;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;

import java.util.Collections;
import java.util.List;

class QuitTownServer extends ServerSubCommand {


    @Override
    public String getName() {
        return "quittown";
    }

    @Override
    public String getDescription() {
        return "";
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
    public List<String> getTabCompleteSuggestions(Player player, String currentMessage, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void perform(Player player, String[] args) {
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if(args.length < 2){
            commandSender.sendMessage("Invalid arguments");
            return;
        }


        Player p = commandSender.getServer().getPlayer(args[1]);
        if(p == null){
            commandSender.sendMessage("Player not found");
            return;
        }
        PlayerData playerData = PlayerDataStorage.get(p);
        TownData townData = playerData.getTown();
        if(townData == null){
            commandSender.sendMessage("Player does not have a town");
            return;
        }
        if(townData.isLeader(playerData)){
            commandSender.sendMessage("Leader cannot quit town");
            return;
        }
        townData.removePlayer(playerData);
    }
}
