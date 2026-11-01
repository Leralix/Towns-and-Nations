package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.invitation.TownInviteDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownStorage;
import org.leralix.tan.utils.text.StringUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JoinTownCommand extends PlayerSubCommand {

    private final PlayerDataStorage playerDataStorage;
    private final TownStorage townDataStorage;

    public JoinTownCommand(
            PlayerDataStorage playerDataStorage,
            TownStorage townDataStorage
    ) {
        this.playerDataStorage = playerDataStorage;
        this.townDataStorage = townDataStorage;
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
        return "/tan join <Town Name>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
        List<String> suggestions = new ArrayList<>();
        // Get All town IDs that invited this player
        for(String townId:TownInviteDataStorage.getInvitations(player.getUniqueId())) {
            Town town= townDataStorage.get(townId);
            if(town!=null) {
                suggestions.add(StringUtil.removeSpaceChar(town.getName()));
            }
        }
        if(suggestions.isEmpty()){
            suggestions.add("<No Invitations>");
        }

        return suggestions;
    }

    @Override
    public void perform(Player player, String[] args) {

        ITanPlayer tanPlayer = playerDataStorage.get(player);

        if (args.length == 1) {
            TanChatUtils.message(tanPlayer, Lang.NOT_ENOUGH_ARGS_ERROR, SoundEnum.NOT_ALLOWED);
            TanChatUtils.message(tanPlayer, Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        } else if (args.length == 2) {

            if (!player.hasPermission("tan.base.town.join")) {
                TanChatUtils.message(tanPlayer, Lang.PLAYER_NO_PERMISSION, SoundEnum.NOT_ALLOWED);
                return;
            }


            String townName = StringUtil.removeSpaceChar(args[1]);
            Optional<Town> optTownData = townDataStorage.getByName(townName); // find town by name
            if(optTownData.isEmpty() || !TownInviteDataStorage.isInvited(player.getUniqueId(), optTownData.get().getID())) {
                TanChatUtils.message(tanPlayer, Lang.TOWN_INVITATION_NO_INVITATION);
                return;
            }

            if (optTownData.get().isFull()) {
                TanChatUtils.message(tanPlayer, Lang.INVITATION_TOWN_FULL);
                return;
            }

            optTownData.get().addPlayer(tanPlayer);
        } else {
            TanChatUtils.message(tanPlayer, Lang.TOO_MANY_ARGS_ERROR);
            TanChatUtils.message(tanPlayer, Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }
}



