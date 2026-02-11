package org.leralix.tan.listeners;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.data.territory.relation.TownRelation;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandBlockerTest extends BasicTest {

    @Test
    void test_relationForbidCommandWithPlayer() {
        Player sender = server.addPlayer("sender");
        Player target = server.addPlayer("target");

        ITanPlayer tanSender = playerDataStorage.get(sender);
        TownData town1 = townDataStorage.newTown("town1", tanSender);

        ITanPlayer tanTarget = playerDataStorage.get(target);
        TownData town2 = townDataStorage.newTown("town2", tanTarget);

        town1.setRelation(town2, TownRelation.EMBARGO);

        CommandBlocker commandBlocker = new CommandBlocker(playerDataStorage);
        assertTrue(commandBlocker.relationForbidCommandWithPlayer(sender, "/tpa target",
                Collections.singleton("/tpa %PLAYER%")));
    }

}