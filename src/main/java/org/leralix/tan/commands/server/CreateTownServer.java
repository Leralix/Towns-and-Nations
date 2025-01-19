package org.leralix.tan.commands.server;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.listeners.chat.events.CreateTown;

import java.util.Collections;
import java.util.List;

class CreateTownServer extends SubCommand {


    @Override
    public String getName() {
        return "createtown";
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
        return "/tanserver createtown <player_username> <town name>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(CommandSender player, String currentMessage, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if(args.length < 3){
            commandSender.sendMessage("Invalid arguments");
            return;
        }

        StringBuilder townNameBuilder = new StringBuilder();
        for(int i = 2; i < args.length; i++){
            townNameBuilder.append(args[i]).append(" ");
        }
        String townName = townNameBuilder.toString().trim();

        Player p = commandSender.getServer().getPlayer(args[1]);
        if(p == null){
            commandSender.sendMessage("Player not found");
            return;
        }
        boolean allowDuplicate = ConfigUtil.getCustomConfig(ConfigTag.TAN).getBoolean("AllowNameDuplication",false);
        if(!allowDuplicate && TownDataStorage.isNameUsed(townName)){
            commandSender.sendMessage("Name duplicates are not allowed");
            return;
        }
        new CreateTown(0).createTown(p, townName);

    }
}
