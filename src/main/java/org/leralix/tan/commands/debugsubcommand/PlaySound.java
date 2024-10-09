package org.leralix.tan.commands.debugsubcommand;

import org.bukkit.entity.Player;
import org.leralix.tan.commands.SubCommand;
import org.leralix.tan.utils.SoundUtil;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.tan.enums.SoundEnum.*;

public class PlaySound extends SubCommand {

    @Override
    public String getName() {
        return "playsound";
    }

    @Override
    public String getDescription() {
        return "Play sounds used in the plugin";
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug playsound";
    }
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            suggestions.add("levelup");
            suggestions.add("add");
            suggestions.add("remove");
            suggestions.add("notallowed");
            suggestions.add("war");
            suggestions.add("good");
            suggestions.add("minorgood");
            suggestions.add("bad");
            suggestions.add("minorbad");
        }
        return suggestions;
    }
    @Override
    public void perform(Player player, String[] args) {
        if (args.length != 2)
            return;

        if (args[1].equalsIgnoreCase("levelup")) {
            SoundUtil.playSound(player, LEVEL_UP);
        }
        if (args[1].equalsIgnoreCase("minorlevelup")) {
            SoundUtil.playSound(player, MINOR_LEVEL_UP);
        }
        else if (args[1].equalsIgnoreCase("add")) {
            SoundUtil.playSound(player, ADD);
        }
        else if (args[1].equalsIgnoreCase("remove")) {
            SoundUtil.playSound(player, REMOVE);
        }
        else if (args[1].equalsIgnoreCase("notallowed")) {
            SoundUtil.playSound(player, NOT_ALLOWED);
        }
        else if (args[1].equalsIgnoreCase("war")) {
            SoundUtil.playSound(player, WAR);
        }
        else if (args[1].equalsIgnoreCase("good")) {
            SoundUtil.playSound(player, GOOD);
        }
        else if (args[1].equalsIgnoreCase("minorgood")) {
            SoundUtil.playSound(player, MINOR_GOOD);
        }
        else if (args[1].equalsIgnoreCase("bad")) {
            SoundUtil.playSound(player, BAD);
        }
        else if (args[1].equalsIgnoreCase("minorbad")) {
            SoundUtil.playSound(player, MINOR_BAD);
        }






    }
}