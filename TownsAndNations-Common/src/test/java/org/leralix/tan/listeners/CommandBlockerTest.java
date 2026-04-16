package org.leralix.tan.listeners;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandBlockerTest extends BasicTest {

    @Test
    void test_relationForbidCommandWithPlayer() {
        Player sender = server.addPlayer("sender");
        Player target = server.addPlayer("target");

        ITanPlayer tanSender = playerDataStorage.get(sender);
        Town town1 = townStorage.newTown("town1", tanSender);

        ITanPlayer tanTarget = playerDataStorage.get(target);
        Town town2 = townStorage.newTown("town2", tanTarget);

        TerritoryUtil.setRelation(town1, town2, TownRelation.EMBARGO);

        CommandBlocker commandBlocker = new CommandBlocker(playerDataStorage);
        assertTrue(commandBlocker.relationForbidCommandWithPlayer(sender, "/tpa target",
                Collections.singleton("/tpa %PLAYER%")));
    }

}