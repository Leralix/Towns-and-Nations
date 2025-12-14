package org.leralix.tan.commands.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.lib.utils.ChatUtils;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.invitation.TownInviteDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public class InvitePlayerCommand extends PlayerSubCommand {
    @Override
    public String getName() {
        return "invite";
    }


    @Override
    public String getDescription() {
        return Lang.TOWN_INVITE_COMMAND_DESC.getDefault();
    }

    public int getArguments() {
        return 2;
    }

    @Override
    public String getSyntax() {
        return "/tan invite <playerName>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                suggestions.add(p.getName());
            }
        }
        return suggestions;
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length <= 1) {
            TanChatUtils.message(player, Lang.NOT_ENOUGH_ARGS_ERROR.getDefault());
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(getSyntax()).getDefault());

        } else if (args.length == 2) {
            invite(player, args[1]);
        } else {
            TanChatUtils.message(player, Lang.TOO_MANY_ARGS_ERROR.getDefault());
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(getSyntax()).getDefault());
        }
    }

    private static void invite(Player player, String playerToInvite) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        TownData townData = TownDataStorage.getInstance().get(player);
        LangType langType = tanPlayer.getLang();

        if (townData == null) {
            TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(langType));
            return;
        }
        if (!townData.doesPlayerHavePermission(tanPlayer, RolePermission.INVITE_PLAYER)) {
            TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(langType));
            return;
        }

        Player invite = Bukkit.getPlayer(playerToInvite);
        if (invite == null) {
            TanChatUtils.message(player, Lang.PLAYER_NOT_FOUND.get(langType));
            return;
        }


        TownData town = TownDataStorage.getInstance().get(player);
        if (town.isFull()) {
            TanChatUtils.message(player, Lang.INVITATION_TOWN_FULL.get(langType));
            return;
        }
        ITanPlayer inviteStat = PlayerDataStorage.getInstance().get(invite);

        if (inviteStat.getTownId() != null) {
            if (inviteStat.getTownId().equals(town.getID())) {
                TanChatUtils.message(player, Lang.INVITATION_ERROR_PLAYER_ALREADY_IN_TOWN.get(langType, invite.getName()));
                return;
            }
            TanChatUtils.message(player, Lang.INVITATION_ERROR_PLAYER_ALREADY_HAVE_TOWN.get(
                    langType,
                    invite.getName(),
                    inviteStat.getTown().getName())
            );
            return;
        }

        TownInviteDataStorage.addInvitation(invite.getUniqueId().toString(), town.getID());

        TanChatUtils.message(player, Lang.INVITATION_SENT_SUCCESS.get(langType, invite.getName()));

        LangType receiverLang = inviteStat.getLang();
        TanChatUtils.message(invite, Lang.INVITATION_RECEIVED_1.get(receiverLang, player.getName(), town.getName()));
        ChatUtils.sendClickableCommand(invite, Lang.INVITATION_RECEIVED_2.get(receiverLang), "tan join " + town.getID());
    }
}


