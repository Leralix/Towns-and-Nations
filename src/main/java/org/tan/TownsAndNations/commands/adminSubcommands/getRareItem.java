package org.tan.TownsAndNations.commands.adminSubcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.DropChances;
import org.tan.TownsAndNations.utils.FileUtil;

import java.util.ArrayList;
import java.util.List;

public class getRareItem extends SubCommand {

    @Override
    public String getName() {
        return "getrareitem";
    }

    @Override
    public String getDescription() {
        return Lang.ADMIN_SPAWN_RARE_ITEM.get();
    }

    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public String getSyntax() {
        return "/tanadmin getrareitem";
    }
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){

        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            suggestions.add("rarestone");
            suggestions.add("rarewood");
            suggestions.add("rarecrop");
            suggestions.add("raresoul");
            suggestions.add("rarefish");
        }
        return suggestions;
    }
    @Override
    public void perform(Player player, String[] args) {
        switch (args[1]) {
            case "rarestone" -> {
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), DropChances.getRareStone());
            }
            case "rarewood" -> {
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), DropChances.getRareWood());
            }
            case "rarecrop" -> {
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), DropChances.getRareCrops());
            }
            case "raresoul" -> {
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), DropChances.getRareSoul());
            }
            case "rarefish" -> {
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), DropChances.getRareFish());
            }
            default -> {
                player.sendMessage(ChatUtils.getTANString() + Lang.SYNTAX_ERROR.get());
                return;
            }
        }
        FileUtil.addLineToHistory(Lang.HISTORY_ADMIN_SUMMON_RARE_ITEM.get(player.getName(), 1, args[1]));
        player.sendMessage(ChatUtils.getTANString() + Lang.COMMAND_GENERIC_SUCCESS.get());
    }
}