package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.war.War;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChangeWarNameTest extends BasicTest {


    @Test
    void nominalCase() {

        Player player = server.addPlayer();
        ITanPlayer tanPlayer = townsAndNations.getPlayerDataStorage().get(player);

        TownData town1 = TownDataStorage.getInstance().newTown("town 1");
        TownData town2 = TownDataStorage.getInstance().newTown("town 2");

        War war = new War("W1", town1, town2, Collections.emptyList());

        ChangeWarName changeWarName = new ChangeWarName(war, null);

        String newName = "new war name";
        changeWarName.execute(player, tanPlayer, newName);

        assertEquals(newName, war.getName());

    }

}