package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.enums.ChunkType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.PlayerAutoClaimStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public class AutoClaimCommand extends PlayerSubCommand {
    @Override
    public String getName() {
        return "autoclaim";
    }

    @Override
    public String getDescription() {
        return Lang.TOWN_AUTO_CLAIM_DESC.getDefault();
    }
    public int getArguments(){ return 1;}


    @Override
    public String getSyntax() {
        return "/tan autoclaim <chunk type>";
    }
    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            for(ChunkType chunkType : ChunkType.values()){
                suggestions.add(chunkType.getName());
            }
            suggestions.add("stop");
        }
        return suggestions;
    }

    @Override
    public void perform(Player player, String[] args){

        if (args.length != 2) {
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(player, getSyntax()));
            return;
        }

        String message = args[1];

        switch (message) {
            case "town" -> {
                PlayerAutoClaimStorage.addPlayer(player, ChunkType.TOWN);
                TanChatUtils.message(player, Lang.AUTO_CLAIM_ON_FOR.get(player, ChunkType.TOWN.getName()));
            }
            case "region" -> {
                PlayerAutoClaimStorage.addPlayer(player, ChunkType.REGION);
                TanChatUtils.message(player, Lang.AUTO_CLAIM_ON_FOR.get(player, ChunkType.REGION.getName()));
            }
            case "kingdom" -> {
                PlayerAutoClaimStorage.addPlayer(player, ChunkType.KINGDOM);
                TanChatUtils.message(player, Lang.AUTO_CLAIM_ON_FOR.get(player, ChunkType.KINGDOM.getName()));
            }
            case "stop" -> {
                PlayerAutoClaimStorage.removePlayer(player);
                TanChatUtils.message(player, Lang.AUTO_CLAIM_OFF.get(player));
            }
            default -> TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(player, getSyntax()));
        }
    }
}


