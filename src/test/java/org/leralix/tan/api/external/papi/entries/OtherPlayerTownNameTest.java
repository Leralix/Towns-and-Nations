package org.leralix.tan.api.external.papi.entries;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

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
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player).join();

        TownData townData = TownDataStorage.getInstance().newTown("Town", tanPlayer).join();

        OtherPlayerTownName entry = new OtherPlayerTownName();

        String name = entry.getData(player, "player_{player name}_town_name");

        assertEquals(townData.getName(), name);
    }

    @Test
    void noTownTest() {

        Player player = server.addPlayer("player name");
        PlayerDataStorage.getInstance().get(player);

        OtherPlayerTownName entry = new OtherPlayerTownName();

        String name = entry.getData(player, "player_{player name}_town_name");

        assertEquals(Lang.NO_TOWN.get(player), name);
    }

}