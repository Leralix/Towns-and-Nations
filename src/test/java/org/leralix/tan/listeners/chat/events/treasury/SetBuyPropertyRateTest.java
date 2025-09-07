package org.leralix.tan.listeners.chat.events.treasury;

import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.*;

class SetBuyPropertyRateTest extends BasicTest {

    @Test
    void nominalTest(){

        double wantedBuyRate = 0.2;
        TownData townData = TownDataStorage.getInstance().newTown("town");

        SetBuyPropertyRate command = new SetBuyPropertyRate(townData);
        command.setRate(wantedBuyRate);

        assertEquals(wantedBuyRate, townData.getTaxOnBuyingProperty());
    }
}