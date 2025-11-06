package org.leralix.tan.listeners.interact.events;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.impl.FortDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

class CreateFortEventTest extends BasicTest {

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
  }

  /** Event where a player create a fort inside a claimed chunk. */
  @Test
  void nominalTest() {

    PlayerMock player = server.addPlayer();
    World world = server.addSimpleWorld("world");

    world.setBlockData(-1, 1, -1, Bukkit.createBlockData(Material.AIR));

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player).join();

    TownData townData = TownDataStorage.getInstance().newTown("town", tanPlayer).join();
    townData.addToBalance(5000.);

    CreateFortEvent createfortEvent = new CreateFortEvent(townData);
    createfortEvent.execute(
        new PlayerInteractEvent(
            player, Action.RIGHT_CLICK_BLOCK, null, world.getBlockAt(-1, 0, -1), BlockFace.UP));

    assertEquals(1, FortDataStorage.getInstance().getOwnedFort(townData).size());
  }
}
