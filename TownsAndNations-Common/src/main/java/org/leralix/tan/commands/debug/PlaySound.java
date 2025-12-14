package org.leralix.tan.commands.debug;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;

import java.util.ArrayList;
import java.util.List;


public class PlaySound extends PlayerSubCommand {

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
            SoundUtil.playSound(player, SoundEnum.LEVEL_UP);
        }
        if (args[1].equalsIgnoreCase("minorlevelup")) {
            SoundUtil.playSound(player, SoundEnum.MINOR_LEVEL_UP);
        }
        else if (args[1].equalsIgnoreCase("add")) {
            SoundUtil.playSound(player, SoundEnum.ADD);
        }
        else if (args[1].equalsIgnoreCase("remove")) {
            SoundUtil.playSound(player, SoundEnum.REMOVE);
        }
        else if (args[1].equalsIgnoreCase("notallowed")) {
            SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
        }
        else if (args[1].equalsIgnoreCase("war")) {
            SoundUtil.playSound(player, SoundEnum.WAR);
        }
        else if (args[1].equalsIgnoreCase("good")) {
            SoundUtil.playSound(player, SoundEnum.GOOD);
        }
        else if (args[1].equalsIgnoreCase("minorgood")) {
            SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
        }
        else if (args[1].equalsIgnoreCase("bad")) {
            SoundUtil.playSound(player, SoundEnum.BAD);
        }
        else if (args[1].equalsIgnoreCase("minorbad")) {
            SoundUtil.playSound(player, SoundEnum.MINOR_BAD);
        }
    }
}