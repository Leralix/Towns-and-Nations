package org.tan.towns_and_nations.commands.debugsubcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.Lang.Lang;
import org.tan.towns_and_nations.commands.SubCommand;
import org.tan.towns_and_nations.enums.CustomVillagerProfession;
import org.tan.towns_and_nations.storage.PlayerChatListenerStorage;
import org.tan.towns_and_nations.utils.ChatUtils;
import org.tan.towns_and_nations.utils.DropChances;
import org.tan.towns_and_nations.utils.VillagerUtil;

import java.util.ArrayList;
import java.util.List;

public class getRareItem extends SubCommand {

    @Override
    public String getName() {
        return "getrareitem";
    }

    @Override
    public String getDescription() {
        return "get rare item that can be traded for money";
    }

    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public String getSyntax() {
        return "/tandebug getrareitem";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){

        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            suggestions.add("rarestone");
            suggestions.add("rarewood");
            suggestions.add("rarecrops");
        }
        return suggestions;
    }
    @Override
    public void perform(Player player, String[] args) {
        switch (args[1]) {
            case "rarestone" -> {
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), DropChances.getRareStone());
                return;
            }
            case "rarewood" -> {
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), DropChances.getRareWood());
                return;
            }
            case "rarecrops" -> {
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), DropChances.getRareCrops());
                return;
            }
        }
        player.sendMessage(ChatUtils.getTANString() + Lang.SYNTAX_ERROR.getTranslation());
    }
}