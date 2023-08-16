package org.tan.towns_and_nations.commands.debugsubcommands;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.tan.towns_and_nations.commands.SubCommand;

import java.util.ArrayList;
import java.util.List;

public class SpawnVillager extends SubCommand {

    @Override
    public String getName() {
        return "spawnvillager";
    }

    @Override
    public String getDescription() {
        return "Spawns a custom villager named Goldsmith.";
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug spawnvillager";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args) {
        Villager villager = player.getWorld().spawn(player.getLocation(), Villager.class);
        villager.setAI(false);
        villager.setCustomName("Goldsmith");
        villager.setCustomNameVisible(true);
        villager.setProfession(Villager.Profession.TOOLSMITH);
        villager.setInvulnerable(true);
        player.sendMessage("Villager created!");
    }
}