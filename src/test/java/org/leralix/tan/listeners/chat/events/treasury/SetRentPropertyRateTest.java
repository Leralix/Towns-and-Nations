package org.leralix.tan.listeners.chat.events.treasury;

import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.*;

class SetRentPropertyRateTest extends BasicTest {

    @Test
    void nominalTest(){

        double wantedRentRate = 0.2;
        TownData townData = TownDataStorage.getInstance().newTown("town").join();

        SetRentPropertyRate command = new SetRentPropertyRate(townData);
        command.setRate(wantedRentRate);

        assertEquals(wantedRentRate, townData.getTaxOnRentingProperty());
    }

}