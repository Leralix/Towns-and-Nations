package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.storage.invitation.TownInviteDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public class JoinTownCommand extends PlayerSubCommand {
    @Override
    public String getName() {
        return "join";
    }


    @Override
    public String getDescription() {
        return Lang.TOWN_ACCEPT_INVITE_DESC.get();
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


        if (args.length == 1) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.get());
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        } else if (args.length == 2){

            if(!player.hasPermission("tan.base.town.join")){
                player.sendMessage(Lang.PLAYER_NO_PERMISSION.get());
                SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                return;
            }

            String townID = args[1];

            if(!TownInviteDataStorage.isInvited(player.getUniqueId().toString(),townID)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_INVITATION_NO_INVITATION.get());
                return;
            }

            TownData townData = TownDataStorage.getInstance().get(townID);
            PlayerData playerData = PlayerDataStorage.getInstance().get(player);

            if(townData.isFull()){
                player.sendMessage(TanChatUtils.getTANString() + Lang.INVITATION_TOWN_FULL.get());
                return;
            }

            townData.addPlayer(playerData);
            townData.broadcastMessageWithSound(Lang.TOWN_INVITATION_ACCEPTED_TOWN_SIDE.get(playerData.getNameStored()), SoundEnum.MINOR_GOOD);
        }
        else{
            player.sendMessage(TanChatUtils.getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }
}



