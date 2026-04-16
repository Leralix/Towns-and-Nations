package org.leralix.tan.listeners.chat.events.treasury;

import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.territory.Town;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SetBuyPropertyRateTest extends BasicTest {

    @Test
    void nominalTest(){

        double wantedBuyRate = 0.2;
        Town townData = townStorage.newTown("town");

        SetBuyPropertyRate command = new SetBuyPropertyRate(townData);
        command.setRate(wantedBuyRate);

        assertEquals(wantedBuyRate, townData.getTaxOnBuyingProperty());
    }
}