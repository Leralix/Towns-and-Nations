package org.leralix.tan.commands.player;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.gui.scope.ClaimAction;
import org.leralix.tan.gui.scope.ClaimType;
import org.leralix.tan.gui.scope.MapSettings;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.ChatChunkMapRenderer;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapCommand extends PlayerSubCommand {

    private final PlayerDataStorage playerDataStorage;

    public MapCommand(PlayerDataStorage playerDataStorage){
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public String getName() {
        return "map";
    }

    @Override
    public String getDescription() {
        return Lang.MAP_COMMAND_DESC.getDefault();
    }

    public int getArguments() {
        return 1;
    }

    @Override
    public String getSyntax() {
        return "/tan map";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length == 1) {
            openMap(player, new MapSettings());
            return;
        }
        if (args.length == 3) {
            openMap(player, new MapSettings(args[1], args[2]));
            return;
        }
        LangType langType = playerDataStorage.get(player).getLang();
        TanChatUtils.message(player, Lang.TOO_MANY_ARGS_ERROR.get(langType), SoundEnum.NOT_ALLOWED);
        TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
    }

    public void openMap(Player player, MapSettings settings) {
        LangType langType = playerDataStorage.get(player).getLang();
        int radius = 4;
        Map<Integer, TextComponent> text = new HashMap<>();
        TextComponent claimType = new TextComponent(Lang.MAP_CLAIM_TYPE.get(langType));
        claimType.setHoverEvent(null);
        claimType.setClickEvent(null);
        claimType.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        text.put(-4, claimType);
        TextComponent typeButton = settings.getMapTypeButton(langType);
        text.put(-3, typeButton);
        TextComponent actionButton = settings.getClaimTypeButton(langType);
        text.put(-2, actionButton);

        ClaimAction claimAction = settings.getClaimActionType();
        ClaimType mapType = settings.getClaimType();
        ChatChunkMapRenderer.sendChunkMap(
                player,
                radius,
                langType,
                (chunkX, chunkZ) -> "/tan " + claimAction.toString().toLowerCase() + " " + mapType.toString().toLowerCase() + " " + chunkX + " " + chunkZ,
                text
        );
    }

}
