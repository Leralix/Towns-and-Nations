package org.tan.TownsAndNations.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.enums.TownRolePermission;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;
import org.tan.TownsAndNations.storage.TownInviteDataStorage;

import java.util.ArrayList;
import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

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
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
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

            if(playerData.getTownId() == null){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.get());
                return;
            }
            if(!playerData.hasPermission(TownRolePermission.INVITE_PLAYER)){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }

            Player invite = Bukkit.getPlayer(args[1]);
            if(invite == null){
                player.sendMessage(getTANString() + Lang.PLAYER_NOT_FOUND.get());
                return;
            }


            TownData town = TownDataStorage.get(player);
            if(!town.canAddMorePlayer()){
                player.sendMessage(getTANString() + Lang.INVITATION_TOWN_FULL.get());
                return;
            }
            PlayerData inviteStat = PlayerDataStorage.get(invite);

            if(inviteStat.getTownId() != null){
                if(inviteStat.getTownId().equals(town.getID())){
                    player.sendMessage(getTANString() + Lang.INVITATION_ERROR_PLAYER_ALREADY_IN_TOWN.get());
                    return;
                }
                player.sendMessage(getTANString() + Lang.INVITATION_ERROR_PLAYER_ALREADY_HAVE_TOWN.get(invite.getName(),TownDataStorage.get(inviteStat.getTownId()).getName()));
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


