package org.tan.TownsAndNations.commands.debugsubcommands;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.wars.AttackStatus;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.AttackStatusStorage;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class startTestWar extends SubCommand {

    @Override
    public String getName() {
        return "startwar";
    }

    @Override
    public String getDescription() {
        return "Starts a test war between two towns";
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug startwar";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args) {
        Collection<PlayerData> players = new ArrayList<>();
        players.add(PlayerDataStorage.get(player));
        AttackStatusStorage.add(new AttackStatus("A1", players, new ArrayList<>()));

    }
}

