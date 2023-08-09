package org.tan.towns_and_nations.commands.debugsubcommands;


import org.bukkit.entity.Player;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.storage.PlayerChatListenerStorage;

public class ChatStorage extends SubCommand {

    @Override
    public String getName() {
        return "chatstorage";
    }

    @Override
    public String getDescription() {
        return "Displays the contents of PlayerChatListenerStorage.";
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug chatstorage";
    }

    @Override
    public void perform(Player player, String[] args) {
        player.sendMessage(PlayerChatListenerStorage.getAllData().toString());
    }
}