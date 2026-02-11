package org.leralix.tan.api.external.papi.entries;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.lang.Lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OtherPlayerTownTagTest extends BasicTest {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }


    @Test
    void nominalTest() {

        Player player = server.addPlayer("name");
        ITanPlayer tanPlayer  = playerDataStorage.get(player);

        TownData townData = townDataStorage.newTown("Town", tanPlayer);

        OtherPlayerTownTag entry = new OtherPlayerTownTag(playerDataStorage, townDataStorage, null, null);

        String name = entry.getData(player, "player_{name}_town_tag");

        assertEquals(townData.getTownTag(), name);
    }

    @Test
    void noTownTest() {

        Player player = server.addPlayer("name");

        OtherPlayerTownTag entry = new OtherPlayerTownTag(playerDataStorage, townDataStorage, null, null);

        String name = entry.getData(player, "player_{name}_town_tag");

        assertEquals(Lang.NO_TOWN.get(langType), name);
    }
}