package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.war.WarData;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChangeWarNameTest extends BasicTest {


    @Test
    void nominalCase() {

        Player player = server.addPlayer();
        ITanPlayer tanPlayer = playerDataStorage.get(player);

        Town town1 = townStorage.newTown("town 1");
        Town town2 = townStorage.newTown("town 2");

        WarData war = new WarData("W1", town1, town2);

        ChangeWarName changeWarName = new ChangeWarName(war, null);

        String newName = "new war name";
        changeWarName.execute(player, tanPlayer, newName);

        assertEquals(newName, war.getName());

    }

}