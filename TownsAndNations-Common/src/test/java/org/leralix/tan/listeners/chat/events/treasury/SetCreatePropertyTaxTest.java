package org.leralix.tan.listeners.chat.events.treasury;

import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.territory.Town;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SetCreatePropertyTaxTest extends BasicTest {


    @Test
    void nominalTest(){

        double wantedBuyRate = 1.2;
        Town townData = townStorage.newTown("town");

        SetCreatePropertyTax command = new SetCreatePropertyTax(townData, null);
        command.setTax(wantedBuyRate);

        assertEquals(wantedBuyRate, townData.getTaxOnCreatingProperty());
    }
}