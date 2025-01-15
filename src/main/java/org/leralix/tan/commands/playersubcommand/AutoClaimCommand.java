package org.leralix.tan.commands.playersubcommand;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.storage.PlayerAutoClaimStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.enums.ChunkType;

import java.util.ArrayList;
import java.util.List;

public class AutoClaimCommand extends SubCommand {
    @Override
    public String getName() {
        return "autoclaim";
    }

    @Override
    public String getDescription() {
        return Lang.TOWN_AUTO_CLAIM_DESC.get();
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
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
            return;
        }

        String message = args[1];

        switch (message) {
            case "town" -> {
                PlayerAutoClaimStorage.addPlayer(player, ChunkType.TOWN);
                player.sendMessage(TanChatUtils.getTANString() + Lang.AUTO_CLAIM_ON_FOR.get(ChunkType.TOWN.getName()));
            }
            case "region" -> {
                PlayerAutoClaimStorage.addPlayer(player, ChunkType.REGION);
                player.sendMessage(TanChatUtils.getTANString() + Lang.AUTO_CLAIM_ON_FOR.get(ChunkType.REGION.getName()));
            }
            case "stop" -> {
                PlayerAutoClaimStorage.removePlayer(player);
                player.sendMessage(TanChatUtils.getTANString() + Lang.AUTO_CLAIM_OFF.get());
            }
            default -> player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }
}


