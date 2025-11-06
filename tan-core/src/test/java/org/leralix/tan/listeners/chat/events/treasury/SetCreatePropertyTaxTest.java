package org.leralix.tan.listeners.chat.events.treasury;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.TownDataStorage;

class SetCreatePropertyTaxTest extends BasicTest {

  @Test
  void nominalTest() {

    double wantedBuyRate = 1.2;
    TownData townData = TownDataStorage.getInstance().newTown("town").join();

    SetCreatePropertyTax command = new SetCreatePropertyTax(townData, null);
    command.setTax(wantedBuyRate);

    assertEquals(wantedBuyRate, townData.getTaxOnCreatingProperty());
  }
}
