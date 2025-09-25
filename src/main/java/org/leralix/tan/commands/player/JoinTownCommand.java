package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.invitation.TownInviteDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

import java.util.ArrayList;
import java.util.List;

public class JoinTownCommand extends PlayerSubCommand {
    @Override
    public String getName() {
        return "join";
    }


    @Override
    public String getDescription() {
        return Lang.TOWN_ACCEPT_INVITE_DESC.getDefault();
    }

    public int getArguments() {
        return 2;
    }


    @Override
    public String getSyntax() {
        return "/tan join <Town ID>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            suggestions.add("<Town ID>");
        }
        return suggestions;
    }

    @Override
    public void perform(Player player, String[] args) {

        LangType lang = PlayerDataStorage.getInstance().get(player).getLang();

        if (args.length == 1) {
            player.sendMessage(Lang.NOT_ENOUGH_ARGS_ERROR.get(lang));
            player.sendMessage(Lang.CORRECT_SYNTAX_INFO.get(lang, getSyntax()));
        } else if (args.length == 2){

            if(!player.hasPermission("tan.base.town.join")){
                player.sendMessage(Lang.PLAYER_NO_PERMISSION.getDefault());
                SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                return;
            }

            String townID = args[1];

            if(!TownInviteDataStorage.isInvited(player.getUniqueId().toString(),townID)){
                player.sendMessage(Lang.TOWN_INVITATION_NO_INVITATION.get(lang));
                return;
            }

            TownData townData = TownDataStorage.getInstance().get(townID);
            ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

            if(townData.isFull()){
                player.sendMessage(Lang.INVITATION_TOWN_FULL.get(lang));
                return;
            }

            townData.addPlayer(tanPlayer);
        }
        else{
            player.sendMessage(Lang.TOO_MANY_ARGS_ERROR.get(lang));
            player.sendMessage(Lang.CORRECT_SYNTAX_INFO.get(lang, getSyntax()));
        }
    }
}



