package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.gui.scope.ClaimType;
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
            for(ClaimType chunkType : ClaimType.values()){
                suggestions.add(chunkType.getTypeCommand());
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
                PlayerAutoClaimStorage.addPlayer(player, ClaimType.TOWN);
                TanChatUtils.message(player, Lang.AUTO_CLAIM_ON_FOR.get(player, ClaimType.TOWN.getTypeCommand()));
            }
            case "region" -> {
                PlayerAutoClaimStorage.addPlayer(player, ClaimType.REGION);
                TanChatUtils.message(player, Lang.AUTO_CLAIM_ON_FOR.get(player, ClaimType.REGION.getTypeCommand()));
            }
            case "nation" -> {
                PlayerAutoClaimStorage.addPlayer(player, ClaimType.NATION);
                TanChatUtils.message(player, Lang.AUTO_CLAIM_ON_FOR.get(player, ClaimType.NATION.getTypeCommand()));
            }
            case "stop" -> {
                PlayerAutoClaimStorage.removePlayer(player);
                TanChatUtils.message(player, Lang.AUTO_CLAIM_OFF.get(player));
            }
            default -> TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(player, getSyntax()));
        }
    }
}


