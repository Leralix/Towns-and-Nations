package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.war.cosmetic.BoundaryRegister;

import java.util.List;

public class EnableBoundaryCommand extends PlayerSubCommand {

    private final PlayerDataStorage playerDataStorage;

    public EnableBoundaryCommand(PlayerDataStorage playerDataStorage) {
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public String getName() {
        return "borders";
    }

    @Override
    public String getDescription() {
        return Lang.SEE_BORDER_COMMAND_DESC.getDefault();
    }

    public int getArguments() {
        return 1;
    }


    @Override
    public String getSyntax() {
        return "/tan borders";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
        return List.of();
    }

    @Override
    public void perform(Player player, String[] args) {
        LangType langType = playerDataStorage.get(player).getLang();

        if (args.length != 1) {
            TanChatUtils.message(player, Lang.SEE_BORDER_COMMAND_DESC.get(langType, getSyntax()));
            return;
        }
        if (BoundaryRegister.isRegistered(player)) {
            TanChatUtils.message(player, Lang.DISABLE_BORDER_INFO.get(langType), SoundEnum.MINOR_GOOD);
        } else {
            TanChatUtils.message(player, Lang.ENABLE_BORDER_INFO.get(langType), SoundEnum.MINOR_GOOD);
        }
        BoundaryRegister.switchPlayer(player);
    }
}


