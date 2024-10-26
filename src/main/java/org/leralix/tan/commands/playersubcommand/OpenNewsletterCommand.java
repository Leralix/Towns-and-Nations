package org.leralix.tan.commands.playersubcommand;

import org.bukkit.entity.Player;
import org.leralix.tan.commands.SubCommand;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;

import java.util.Collections;
import java.util.List;

import static org.leralix.tan.utils.ChatUtils.getTANString;

public class OpenNewsletterCommand extends SubCommand {

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
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
            return;
        }
        PlayerGUI.openNewsletter(player,0);

    }


}
