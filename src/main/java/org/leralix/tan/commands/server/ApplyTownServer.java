package org.leralix.tan.commands.server;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;

import java.util.Collections;
import java.util.List;

public class ApplyTownServer extends ServerSubCommand{
    @Override
    public String getName() {
        return "applytown";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public int getArguments() {
        return 3;
    }

    @Override
    public String getSyntax() {
        return "/tanserver apply <town ID> <player username>";
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
        if(args.length < 3){
            commandSender.sendMessage("Invalid arguments");
            return;
        }
        String townID = args[1];
        String playerName = args[2];
        Player p = commandSender.getServer().getPlayer(playerName);
        if(p == null){
            commandSender.sendMessage("Player not found");
            return;
        }
        if(townID == null){
            commandSender.sendMessage("Town ID not found");
            return;
        }
        PlayerData playerData = PlayerDataStorage.get(p.getUniqueId().toString());
        if(playerData.haveTown()){
            commandSender.sendMessage("Player already have a town");
            return;
        }
        TownData townData = TownDataStorage.get(townID);
        townData.addPlayerJoinRequest(p);
    }
}
