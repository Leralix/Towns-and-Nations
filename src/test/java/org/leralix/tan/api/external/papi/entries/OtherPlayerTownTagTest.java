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

class OtherPlayerTownTagTest extends BasicTest {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }


    @Test
    void nominalTest() {

        Player player = server.addPlayer("name");
        ITanPlayer tanPlayer  = PlayerDataStorage.getInstance().get(player);

        TownData townData = TownDataStorage.getInstance().newTown("Town", tanPlayer);

        OtherPlayerTownTag entry = new OtherPlayerTownTag();

        String name = entry.getData(player, "player_{name}_town_tag");

        assertEquals(townData.getTownTag(), name);
    }

    @Test
    void noTownTest() {

        Player player = server.addPlayer("name");

        OtherPlayerTownTag entry = new OtherPlayerTownTag();

        String name = entry.getData(player, "player_{name}_town_tag");

        assertEquals(Lang.NO_TOWN.get(), name);
    }
}