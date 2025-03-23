package org.leralix.tan.commands.server;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.lang.Lang;
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
        return Lang.CREATE_TOWN_SERVER_DESC.get();
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
            commandSender.sendMessage(Lang.INVALID_ARGUMENTS.get());
            return;
        }

        StringBuilder townNameBuilder = new StringBuilder();
        for(int i = 2; i < args.length; i++){
            townNameBuilder.append(args[i]).append(" ");
        }
        String townName = townNameBuilder.toString().trim();

        Player p = commandSender.getServer().getPlayer(args[1]);
        if(p == null){
            commandSender.sendMessage(Lang.PLAYER_NOT_FOUND.get());
            return;
        }
        boolean allowDuplicate = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("AllowNameDuplication",false);
        if(!allowDuplicate && TownDataStorage.getInstance().isNameUsed(townName)){
            commandSender.sendMessage(Lang.NAME_ALREADY_USED.get());
            return;
        }
        new CreateTown(0).createTown(p, townName);

    }
}
