package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.MinimapManager;

import java.util.List;

public class ShowMinimap extends PlayerSubCommand {

    MinimapManager minimapManager;

    public ShowMinimap(MinimapManager minimapManager) {
        this.minimapManager = minimapManager;
    }

    @Override
    public String getName() {
        return "minimap";
    }

    @Override
    public String getDescription() {
        return Lang.OPEN_MINIMAP_COMMAND_DESC.getDefault();
    }

    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public String getSyntax() {
        return "/tan minimap";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String currentMessage, String[] args) {
        return List.of();
    }

    @Override
    public void perform(Player player, String[] args) {
        if(minimapManager.contains(player.getUniqueId())){
            minimapManager.removePlayer(player.getUniqueId());
        }
        else {
            minimapManager.addPlayer(player.getUniqueId());
        }
    }
}
