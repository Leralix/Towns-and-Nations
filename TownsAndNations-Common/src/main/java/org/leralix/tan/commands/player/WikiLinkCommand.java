package org.leralix.tan.commands.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;

import java.util.List;

public class WikiLinkCommand extends PlayerSubCommand {

    private final PlayerDataStorage playerDataStorage;

    public WikiLinkCommand(PlayerDataStorage playerDataStorage){
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public String getName() {
        return "wiki";
    }

    @Override
    public String getDescription() {
        return "Link to the wiki";
    }

    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public String getSyntax() {
        return "/tan wiki";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String currentMessage, String[] args) {
        return List.of();
    }

    @Override
    public void perform(Player player, String[] args) {
        LangType langType = playerDataStorage.get(player).getLang();
        TextComponent link = Component.text(Lang.WIKI_LINK.get(langType))
                .decorate(TextDecoration.BOLD)
                .clickEvent(ClickEvent.openUrl("https://arcadia-9.gitbook.io/towns-and-nations"));
        player.sendMessage(link);
    }
}
