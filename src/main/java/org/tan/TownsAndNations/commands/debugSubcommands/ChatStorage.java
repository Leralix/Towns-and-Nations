package org.tan.TownsAndNations.commands.debugSubcommands;


import org.bukkit.entity.Player;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.PlayerChatListenerStorage;

import java.util.List;

public class ChatStorage extends SubCommand {

    @Override
    public String getName() {
        return "chatstorage";
    }

    @Override
    public String getDescription() {
        return Lang.DEBUG_CHAT_STORAGE.get();
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug chatstorage";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args) {
        player.sendMessage(PlayerChatListenerStorage.getAllData().toString());
    }
}