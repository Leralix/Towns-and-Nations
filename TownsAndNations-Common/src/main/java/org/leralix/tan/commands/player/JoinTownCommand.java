package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.invitation.TownInviteDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public class JoinTownCommand extends PlayerSubCommand {

    private final PlayerDataStorage playerDataStorage;

    public JoinTownCommand(PlayerDataStorage playerDataStorage) {
        this.playerDataStorage = playerDataStorage;
    }

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
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            suggestions.add("<Town ID>");
        }
        return suggestions;
    }

    @Override
    public void perform(Player player, String[] args) {

        LangType lang = playerDataStorage.get(player).getLang();

        if (args.length == 1) {
            TanChatUtils.message(player, Lang.NOT_ENOUGH_ARGS_ERROR.get(lang), SoundEnum.NOT_ALLOWED);
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(lang, getSyntax()));
        } else if (args.length == 2) {

            if (!player.hasPermission("tan.base.town.join")) {
                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(lang), SoundEnum.NOT_ALLOWED);
                return;
            }

            String townID = args[1];

            if (!TownInviteDataStorage.isInvited(player.getUniqueId(), townID)) {
                TanChatUtils.message(player, Lang.TOWN_INVITATION_NO_INVITATION.get(lang));
                return;
            }

            TownData townData = TownDataStorage.getInstance().get(townID);
            ITanPlayer tanPlayer = playerDataStorage.get(player);

            if (townData.isFull()) {
                TanChatUtils.message(player, Lang.INVITATION_TOWN_FULL.get(lang));
                return;
            }

            townData.addPlayer(tanPlayer);
        } else {
            TanChatUtils.message(player, Lang.TOO_MANY_ARGS_ERROR.get(lang));
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(lang, getSyntax()));
        }
    }
}



