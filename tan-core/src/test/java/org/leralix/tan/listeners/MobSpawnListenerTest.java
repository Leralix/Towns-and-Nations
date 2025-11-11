package org.leralix.tan.listeners;

import static org.junit.jupiter.api.Assertions.*;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

/** Unit tests for MobSpawnListener. */
class MobSpawnListenerTest {

  private ServerMock server;
  private MobSpawnListener listener;
  private World world;

  @BeforeEach
  void setUp() {
    server = MockBukkit.mock();
    MockBukkit.load(SphereLib.class);
    MockBukkit.load(TownsAndNations.class);
    listener = new MobSpawnListener();
    world = server.addSimpleWorld("world");
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    MockBukkit.unmock();
  }

  // ==================== Basic Event Handling Tests ====================

  @Test
  void entitySpawn_inUnclaimedChunk_allowsSpawn() {
    Location location = new Location(world, 0, 64, 0);
    Zombie zombie = world.spawn(location, Zombie.class);
    EntitySpawnEvent event = new EntitySpawnEvent(zombie);

    listener.entitySpawn(event);

    assertFalse(event.isCancelled());
  }

  @Test
  void entitySpawn_withZombie_handlesCorrectly() {
    Location location = new Location(world, 0, 64, 0);
    Zombie zombie = world.spawn(location, Zombie.class);
    EntitySpawnEvent event = new EntitySpawnEvent(zombie);

    assertDoesNotThrow(() -> listener.entitySpawn(event));
  }

  @Test
  void entitySpawn_withCreeper_handlesCorrectly() {
    Location location = new Location(world, 0, 64, 0);
    Creeper creeper = world.spawn(location, Creeper.class);
    EntitySpawnEvent event = new EntitySpawnEvent(creeper);

    assertDoesNotThrow(() -> listener.entitySpawn(event));
  }

  @Test
  void entitySpawn_withCow_handlesCorrectly() {
    Location location = new Location(world, 0, 64, 0);
    Cow cow = world.spawn(location, Cow.class);
    EntitySpawnEvent event = new EntitySpawnEvent(cow);

    assertDoesNotThrow(() -> listener.entitySpawn(event));
  }

  // ==================== Multiple Spawns Tests ====================

  @Test
  void entitySpawn_multipleEntities_handlesEach() {
    Location location = new Location(world, 0, 64, 0);

    Zombie zombie = world.spawn(location, Zombie.class);
    Creeper creeper = world.spawn(location, Creeper.class);
    Cow cow = world.spawn(location, Cow.class);

    EntitySpawnEvent event1 = new EntitySpawnEvent(zombie);
    EntitySpawnEvent event2 = new EntitySpawnEvent(creeper);
    EntitySpawnEvent event3 = new EntitySpawnEvent(cow);

    assertDoesNotThrow(
        () -> {
          listener.entitySpawn(event1);
          listener.entitySpawn(event2);
          listener.entitySpawn(event3);
        });
  }

  // ==================== Edge Cases ====================

  @Test
  void entitySpawn_atWorldBoundary_handlesCorrectly() {
    Location location = new Location(world, 30000000, 64, 30000000);
    Zombie zombie = world.spawn(location, Zombie.class);
    EntitySpawnEvent event = new EntitySpawnEvent(zombie);

    assertDoesNotThrow(() -> listener.entitySpawn(event));
  }

  @Test
  void entitySpawn_atNegativeCoordinates_handlesCorrectly() {
    Location location = new Location(world, -1000, 64, -1000);
    Zombie zombie = world.spawn(location, Zombie.class);
    EntitySpawnEvent event = new EntitySpawnEvent(zombie);

    assertDoesNotThrow(() -> listener.entitySpawn(event));
  }

  @Test
  void entitySpawn_atZeroCoordinates_handlesCorrectly() {
    Location location = new Location(world, 0, 0, 0);
    Zombie zombie = world.spawn(location, Zombie.class);
    EntitySpawnEvent event = new EntitySpawnEvent(zombie);

    assertDoesNotThrow(() -> listener.entitySpawn(event));
  }

  @Test
  void entitySpawn_rapidSuccessiveSpawns_handlesCorrectly() {
    Location location = new Location(world, 0, 64, 0);
    Zombie zombie = world.spawn(location, Zombie.class);
    EntitySpawnEvent event = new EntitySpawnEvent(zombie);

    assertDoesNotThrow(
        () -> {
          for (int i = 0; i < 100; i++) {
            listener.entitySpawn(event);
          }
        });
  }

  @Test
  void entitySpawn_differentEntityTypes_handlesAll() {
    Location location = new Location(world, 0, 64, 0);

    // Test various entity types
    EntityType[] types = {
      EntityType.ZOMBIE, EntityType.CREEPER, EntityType.COW,
      EntityType.PIG, EntityType.SHEEP, EntityType.CHICKEN
    };

    for (EntityType type : types) {
      if (type.isSpawnable()) {
        assertDoesNotThrow(
            () -> {
              var entity = world.spawnEntity(location, type);
              EntitySpawnEvent event = new EntitySpawnEvent(entity);
              listener.entitySpawn(event);
            });
      }
    }
  }
}
