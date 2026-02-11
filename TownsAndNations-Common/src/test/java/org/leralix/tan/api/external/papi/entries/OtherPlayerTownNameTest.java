package org.leralix.tan.api.external.papi.entries;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.lang.Lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OtherPlayerTownNameTest extends BasicTest {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }


    @Test
    void nominalTest() {

        Player player = server.addPlayer("player name");
        ITanPlayer tanPlayer = playerDataStorage.get(player);

        TownData townData = townDataStorage.newTown("Town", tanPlayer);

        OtherPlayerTownName entry = new OtherPlayerTownName(playerDataStorage, townDataStorage, null, null);

        String name = entry.getData(player, "player_{player name}_town_name");

        assertEquals(townData.getName(), name);
    }

    @Test
    void noTownTest() {

        Player player = server.addPlayer("player name");
        playerDataStorage.get(player);

        OtherPlayerTownName entry = new OtherPlayerTownName(playerDataStorage, townDataStorage, null, null);

        String name = entry.getData(player, "player_{player name}_town_name");

        assertEquals(Lang.NO_TOWN.get(langType), name);
    }

}