package org.leralix.tan.commands.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.lib.utils.ChatUtils;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.storage.invitation.TownInviteDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public class InvitePlayerCommand extends PlayerSubCommand {
    @Override
    public String getName() {
        return "invite";
    }


    @Override
    public String getDescription() {
        return Lang.TOWN_INVITE_COMMAND_DESC.get();
    }
    public int getArguments(){ return 2;}
    @Override
    public String getSyntax() {
        return "/tan invite <playerName>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                suggestions.add(p.getName());
            }
        }
        return suggestions;
    }
    @Override
    public void perform(Player player, String[] args){
        if (args.length <= 1){
            player.sendMessage(TanChatUtils.getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.get());
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));

        }else if(args.length == 2){

            PlayerData playerData = PlayerDataStorage.getInstance().get(player);
            TownData townData = TownDataStorage.getInstance().get(player);

            if(townData == null){
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_TOWN.get());
                return;
            }
            if(!townData.doesPlayerHavePermission(playerData, RolePermission.INVITE_PLAYER)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }

            Player invite = Bukkit.getPlayer(args[1]);
            if(invite == null){
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NOT_FOUND.get());
                return;
            }


            TownData town = TownDataStorage.getInstance().get(player);
            if(town.isFull()){
                player.sendMessage(TanChatUtils.getTANString() + Lang.INVITATION_TOWN_FULL.get());
                return;
            }
            PlayerData inviteStat = PlayerDataStorage.getInstance().get(invite);

            if(inviteStat.getTownId() != null){
                if(inviteStat.getTownId().equals(town.getID())){
                    player.sendMessage(TanChatUtils.getTANString() + Lang.INVITATION_ERROR_PLAYER_ALREADY_IN_TOWN.get());
                    return;
                }
                player.sendMessage(TanChatUtils.getTANString() + Lang.INVITATION_ERROR_PLAYER_ALREADY_HAVE_TOWN.get(
                        invite.getName(),
                        inviteStat.getTown().getName()));
                return;
            }

            TownInviteDataStorage.addInvitation(invite.getUniqueId().toString(),town.getID());

            player.sendMessage(TanChatUtils.getTANString() + Lang.INVITATION_SENT_SUCCESS.get(invite.getName()));

            invite.sendMessage(TanChatUtils.getTANString() + Lang.INVITATION_RECEIVED_1.get(player.getName(),town.getName()));
            ChatUtils.sendClickableCommand(invite,  TanChatUtils.getTANString() + Lang.INVITATION_RECEIVED_2.get(),"tan join "  + town.getID());

        }else {
            player.sendMessage(TanChatUtils.getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }
}


