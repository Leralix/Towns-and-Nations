package org.leralix.tan.listeners;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;


class CommandBlockerTest extends BasicTest {



    @Test
    void test_relationForbidCommandWithPlayer(){
        Player sender = server.addPlayer("sender");
        Player target = server.addPlayer("target");

        ITanPlayer tanSender = PlayerDataStorage.getInstance().get(sender).join();
        TownData town1 = TownDataStorage.getInstance().newTown("town1", tanSender).join();

        ITanPlayer tanTarget = PlayerDataStorage.getInstance().get(target).join();
        TownData town2 = TownDataStorage.getInstance().newTown("town2", tanTarget).join();

        town1.setRelation(town2, TownRelation.EMBARGO);

        assertTrue(CommandBlocker.relationForbidCommandWithPlayer(sender, "/tpa target", Collections.singleton("/tpa %PLAYER%")).join());
    }

}