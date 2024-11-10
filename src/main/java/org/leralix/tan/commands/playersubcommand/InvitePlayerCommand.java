package org.leralix.tan.commands.playersubcommand;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.commands.SubCommand;
import org.leralix.tan.enums.TownRolePermission;
import org.leralix.tan.utils.ChatUtils;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.storage.invitation.TownInviteDataStorage;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.tan.utils.ChatUtils.getTANString;

public class InvitePlayerCommand extends SubCommand {
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
            player.sendMessage(getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));

        }else if(args.length == 2){

            PlayerData playerData = PlayerDataStorage.get(player);
            TownData townData = TownDataStorage.get(player);

            if(townData == null){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.get());
                return;
            }
            if(!townData.doesPlayerHavePermission(playerData, TownRolePermission.INVITE_PLAYER)){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }

            Player invite = Bukkit.getPlayer(args[1]);
            if(invite == null){
                player.sendMessage(getTANString() + Lang.PLAYER_NOT_FOUND.get());
                return;
            }


            TownData town = TownDataStorage.get(player);
            if(town.isFull()){
                player.sendMessage(getTANString() + Lang.INVITATION_TOWN_FULL.get());
                return;
            }
            PlayerData inviteStat = PlayerDataStorage.get(invite);

            if(inviteStat.getTownId() != null){
                if(inviteStat.getTownId().equals(town.getID())){
                    player.sendMessage(getTANString() + Lang.INVITATION_ERROR_PLAYER_ALREADY_IN_TOWN.get());
                    return;
                }
                player.sendMessage(getTANString() + Lang.INVITATION_ERROR_PLAYER_ALREADY_HAVE_TOWN.get(
                        invite.getName(),
                        inviteStat.getTown().getName()));
                return;
            }

            TownInviteDataStorage.addInvitation(invite.getUniqueId().toString(),town.getID());

            player.sendMessage(getTANString() + Lang.INVITATION_SENT_SUCCESS.get(invite.getName()));

            invite.sendMessage(getTANString() + Lang.INVITATION_RECEIVED_1.get(player.getName(),town.getName()));
            ChatUtils.sendClickableCommand(invite,  getTANString() + Lang.INVITATION_RECEIVED_2.get(),"tan join "  + town.getID());

        }else {
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }
}


