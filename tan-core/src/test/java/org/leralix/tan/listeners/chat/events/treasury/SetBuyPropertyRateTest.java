package org.leralix.tan.listeners.chat.events.treasury;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.TownDataStorage;

class SetBuyPropertyRateTest extends BasicTest {

  @Test
  void nominalTest() {

    double wantedBuyRate = 0.2;
    TownData townData = TownDataStorage.getInstance().newTown("town").join();

    SetBuyPropertyRate command = new SetBuyPropertyRate(townData);
    command.setRate(wantedBuyRate);

    assertEquals(wantedBuyRate, townData.getTaxOnBuyingProperty());
  }
}
