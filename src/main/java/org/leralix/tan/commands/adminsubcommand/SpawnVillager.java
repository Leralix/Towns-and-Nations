package org.leralix.tan.commands.adminsubcommand;


import org.bukkit.entity.Player;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.commands.SubCommand;
import org.leralix.tan.enums.CustomVillagerProfession;
import org.leralix.tan.utils.ChatUtils;
import org.leralix.tan.utils.VillagerUtil;

import java.util.ArrayList;
import java.util.List;

public class SpawnVillager extends SubCommand {

    @Override
    public String getName() {
        return "spawnvillager";
    }

    @Override
    public String getDescription() {
        return Lang.ADMIN_SPAWN_CUSTOM_VILLAGER.get();
    }

    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public String getSyntax() {
        return "/tanadmin spawnvillager <Villager Name>";
    }
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            suggestions.add("goldsmith");
            suggestions.add("cook");
            suggestions.add("botanist");
            suggestions.add("wizard");
            suggestions.add("fisherman");
        }
        return suggestions;
    }
    @Override
    public void perform(Player player, String[] args) {


        switch (args[1]) {
            case "goldsmith" -> {
                VillagerUtil.createCustomVillager(player, CustomVillagerProfession.GOLDSMITH);
                return;
            }
            case "cook" -> {
                VillagerUtil.createCustomVillager(player, CustomVillagerProfession.COOK);
                return;
            }
            case "botanist" -> {
                VillagerUtil.createCustomVillager(player, CustomVillagerProfession.BOTANIST);
                return;
            }
            case "wizard" -> {
                VillagerUtil.createCustomVillager(player, CustomVillagerProfession.WIZARD);
                return;
            }
            case "fisherman" -> {
                VillagerUtil.createCustomVillager(player, CustomVillagerProfession.FISHERMAN);
                return;
            }
        }
        player.sendMessage(ChatUtils.getTANString() + Lang.SYNTAX_ERROR.get());
        player.sendMessage(ChatUtils.getTANString() + getSyntax());

    }
}