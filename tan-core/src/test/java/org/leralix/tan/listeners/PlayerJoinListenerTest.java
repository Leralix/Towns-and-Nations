package org.leralix.tan.listeners;

import static org.junit.jupiter.api.Assertions.*;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

/** Unit tests for PlayerJoinListener. */
class PlayerJoinListenerTest {

  private ServerMock server;
  private Player player;
  private PlayerJoinListener listener;
  private ITanPlayer tanPlayer;

  @BeforeEach
  void setUp() {
    server = MockBukkit.mock();
    MockBukkit.load(SphereLib.class);
    MockBukkit.load(TownsAndNations.class);
    player = server.addPlayer("TestPlayer");
    listener = new PlayerJoinListener();
    tanPlayer = PlayerDataStorage.getInstance().getSync(player);
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    MockBukkit.unmock();
  }

  // ==================== Basic Event Handling Tests ====================

  @Test
  void onPlayerJoin_withValidPlayer_doesNotThrow() {
    PlayerJoinEvent event = new PlayerJoinEvent(player, Component.empty());

    assertDoesNotThrow(() -> listener.onPlayerJoin(event));
  }

  @Test
  void onPlayerJoin_withNewPlayer_initializesPlayerData() {
    Player newPlayer = server.addPlayer("NewPlayer");
    PlayerJoinEvent event = new PlayerJoinEvent(newPlayer, Component.empty());

    assertDoesNotThrow(() -> listener.onPlayerJoin(event));

    // Player data should be initialized
    ITanPlayer newTanPlayer = PlayerDataStorage.getInstance().getSync(newPlayer);
    assertNotNull(newTanPlayer);
  }

  @Test
  void onPlayerJoin_withExistingPlayer_doesNotThrow() {
    // Player already exists from setUp
    PlayerJoinEvent event = new PlayerJoinEvent(player, Component.empty());

    assertDoesNotThrow(() -> listener.onPlayerJoin(event));
  }

  // ==================== Multiple Players Tests ====================

  @Test
  void onPlayerJoin_multiplePlayers_handlesEach() {
    Player player2 = server.addPlayer("Player2");
    Player player3 = server.addPlayer("Player3");

    PlayerJoinEvent event1 = new PlayerJoinEvent(player, Component.empty());
    PlayerJoinEvent event2 = new PlayerJoinEvent(player2, Component.empty());
    PlayerJoinEvent event3 = new PlayerJoinEvent(player3, Component.empty());

    assertDoesNotThrow(
        () -> {
          listener.onPlayerJoin(event1);
          listener.onPlayerJoin(event2);
          listener.onPlayerJoin(event3);
        });
  }

  @Test
  void onPlayerJoin_samePlayerTwice_handlesCorrectly() {
    PlayerJoinEvent event1 = new PlayerJoinEvent(player, Component.empty());
    PlayerJoinEvent event2 = new PlayerJoinEvent(player, Component.empty());

    assertDoesNotThrow(
        () -> {
          listener.onPlayerJoin(event1);
          listener.onPlayerJoin(event2);
        });
  }

  // ==================== Edge Cases ====================

  @Test
  void onPlayerJoin_playerWithSpecialCharName_handlesCorrectly() {
    Player specialPlayer = server.addPlayer("Player_123");
    PlayerJoinEvent event = new PlayerJoinEvent(specialPlayer, Component.empty());

    assertDoesNotThrow(() -> listener.onPlayerJoin(event));
  }

  @Test
  void onPlayerJoin_playerWithLongName_handlesCorrectly() {
    Player longNamePlayer = server.addPlayer("VeryLongPlayerNameHere");
    PlayerJoinEvent event = new PlayerJoinEvent(longNamePlayer, Component.empty());

    assertDoesNotThrow(() -> listener.onPlayerJoin(event));
  }

  @Test
  void onPlayerJoin_rapidSuccessiveJoins_handlesCorrectly() {
    PlayerJoinEvent event = new PlayerJoinEvent(player, Component.empty());

    // Simulate rapid joins
    assertDoesNotThrow(
        () -> {
          for (int i = 0; i < 10; i++) {
            listener.onPlayerJoin(event);
          }
        });
  }

  @Test
  void onPlayerJoin_withOp_handlesPermissionsCorrectly() {
    player.setOp(true);
    PlayerJoinEvent event = new PlayerJoinEvent(player, Component.empty());

    assertDoesNotThrow(() -> listener.onPlayerJoin(event));
  }

  @Test
  void onPlayerJoin_withoutOp_handlesCorrectly() {
    player.setOp(false);
    PlayerJoinEvent event = new PlayerJoinEvent(player, Component.empty());

    assertDoesNotThrow(() -> listener.onPlayerJoin(event));
  }
}
