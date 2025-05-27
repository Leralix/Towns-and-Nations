package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.gui.user.NewsletterMenu;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.newsletter.NewsletterScope;

import java.util.Collections;
import java.util.List;

public class OpenNewsletterCommand extends PlayerSubCommand {

    @Override
    public String getName() {
        return "newsletter";
    }

    @Override
    public String getDescription() {
        return Lang.OPEN_NEWSLETTER_DESC.get();
    }
    public int getArguments(){ return 1;}


    @Override
    public String getSyntax() {
        return "/tan newsletter";
    }
    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        return Collections.emptyList();
    }

    @Override
    public void perform(Player player, String[] args){

        if (args.length != 1) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
            return;
        }
        new NewsletterMenu(player).open();

    }


}
