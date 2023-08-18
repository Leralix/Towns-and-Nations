package org.tan.towns_and_nations.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.Lang.Lang;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.enums.TownRolePermission;
import org.tan.towns_and_nations.utils.ChatUtils;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;
import org.tan.towns_and_nations.storage.TownInviteDataStorage;

import java.util.ArrayList;
import java.util.List;

import static org.tan.towns_and_nations.utils.ChatUtils.getTANString;

public class InvitePlayerCommand extends SubCommand {
    @Override
    public String getName() {
        return "invite";
    }


    @Override
    public String getDescription() {
        return Lang.TOWN_INVITE_COMMAND_DESC.getTranslation();
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
            player.sendMessage(getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.getTranslation());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.getTranslation(getSyntax()));

        }else if(args.length == 2){

            PlayerDataClass playerData = PlayerStatStorage.getStat(player);

            if(playerData.getTownId() == null){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.getTranslation());
                return;
            }
            if(playerData.hasPermission(TownRolePermission.INVITE_PLAYER)){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
                return;
            }

            Player invite = Bukkit.getPlayer(args[1]);
            if(invite == null){
                player.sendMessage(getTANString() + Lang.PLAYER_NOT_FOUND.getTranslation());
                return;
            }


            TownDataClass town = TownDataStorage.getTown(player);
            if(!town.canAddMorePlayer()){
                player.sendMessage(getTANString() + Lang.PLAYER_NOT_FOUND.getTranslation());
                return;
            }
            PlayerDataClass inviteStat = PlayerStatStorage.getStat(invite);

            if(inviteStat.getTownId() != null){
                if(inviteStat.getTownId().equals(town.getTownId())){
                    player.sendMessage(getTANString() + Lang.INVITATION_ERROR_PLAYER_ALREADY_IN_TOWN.getTranslation());
                    return;
                }
                player.sendMessage(getTANString() + Lang.INVITATION_ERROR_PLAYER_ALREADY_HAVE_TOWN.getTranslation(invite.getName(),TownDataStorage.getTown(inviteStat.getTownId())));
                return;
            }

            TownInviteDataStorage.addInvitation(invite.getUniqueId().toString(),town.getTownId());

            player.sendMessage(getTANString() + Lang.INVITATION_SENT_SUCCESS.getTranslation(invite.getName()));

            invite.sendMessage(getTANString() + Lang.INVITATION_RECEIVED_1.getTranslation(player.getName(),town.getTownName()));
            ChatUtils.sendClickableCommand(invite,  getTANString() + Lang.INVITATION_RECEIVED_2.getTranslation(),"tan join "  + town.getTownId());

        }else {
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.getTranslation());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.getTranslation(getSyntax()));
        }
    }
}


