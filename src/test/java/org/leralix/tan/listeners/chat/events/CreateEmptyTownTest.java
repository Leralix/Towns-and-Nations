package org.leralix.tan.listeners.chat.events;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.factory.AbstractionFactory;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.*;

class CreateEmptyTownTest {

    @BeforeAll
    static void setUp() {
        AbstractionFactory.initializeConfigs();
    }

    @Test
    void nominalCase() {
        String townName = "TestTown";
        CreateEmptyTown createEmptyTown = new CreateEmptyTown(null);
        createEmptyTown.execute(AbstractionFactory.getRandomPlayer(), townName);

        assertTrue(TownDataStorage.getInstance().isNameUsed(townName));
    }

}