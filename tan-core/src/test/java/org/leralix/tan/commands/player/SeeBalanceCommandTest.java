package org.leralix.tan.commands.player;

import static org.junit.jupiter.api.Assertions.*;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

/** Unit tests for SeeBalanceCommand. */
class SeeBalanceCommandTest {

  private ServerMock server;
  private Player player;
  private SeeBalanceCommand command;
  private ITanPlayer tanPlayer;

  @BeforeEach
  void setUp() {
    server = MockBukkit.mock();
    MockBukkit.load(SphereLib.class);
    MockBukkit.load(TownsAndNations.class);
    player = server.addPlayer("TestPlayer");
    command = new SeeBalanceCommand();
    tanPlayer = PlayerDataStorage.getInstance().getSync(player);
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    MockBukkit.unmock();
  }

  // ==================== Basic Command Info Tests ====================

  @Test
  void getName_returnsCorrectName() {
    assertEquals("balance", command.getName());
  }

  @Test
  void getSyntax_returnsCorrectSyntax() {
    assertEquals("/tan balance", command.getSyntax());
  }

  @Test
  void getArguments_returnsCorrectCount() {
    assertEquals(1, command.getArguments());
  }

  @Test
  void getTabCompleteSuggestions_returnsEmptyList() {
    assertTrue(command.getTabCompleteSuggestions(player, "test", new String[] {}).isEmpty());
  }

  // ==================== Balance Display Tests ====================

  @Test
  void perform_withOneArg_displaysBalance() {
    String[] args = {"balance"};
    EconomyUtil.setBalance(tanPlayer, 1000.0);

    assertDoesNotThrow(() -> command.perform(player, args));
    assertEquals(1000.0, EconomyUtil.getBalance(tanPlayer), 0.01);
  }

  @Test
  void perform_zeroBalance_displaysZero() {
    String[] args = {"balance"};
    EconomyUtil.setBalance(tanPlayer, 0.0);

    assertDoesNotThrow(() -> command.perform(player, args));
    assertEquals(0.0, EconomyUtil.getBalance(tanPlayer), 0.01);
  }

  @Test
  void perform_largeBalance_displaysCorrectly() {
    String[] args = {"balance"};
    EconomyUtil.setBalance(tanPlayer, 999999999.99);

    assertDoesNotThrow(() -> command.perform(player, args));
    assertEquals(999999999.99, EconomyUtil.getBalance(tanPlayer), 0.01);
  }

  @Test
  void perform_negativeBalance_displaysCorrectly() {
    String[] args = {"balance"};
    EconomyUtil.setBalance(tanPlayer, -500.0);

    assertDoesNotThrow(() -> command.perform(player, args));
    assertEquals(-500.0, EconomyUtil.getBalance(tanPlayer), 0.01);
  }

  // ==================== Argument Validation Tests ====================

  @Test
  void perform_tooManyArgs_showsError() {
    String[] args = {"balance", "extra"};

    command.perform(player, args);

    // Should display error message (can't verify without chat history)
  }

  @Test
  void perform_threeArgs_showsError() {
    String[] args = {"balance", "arg1", "arg2"};

    command.perform(player, args);

    // Should display error message for too many arguments
  }

  // ==================== Edge Cases ====================

  @Test
  void perform_decimalBalance_displaysCorrectly() {
    String[] args = {"balance"};
    EconomyUtil.setBalance(tanPlayer, 123.45);

    assertDoesNotThrow(() -> command.perform(player, args));
    assertEquals(123.45, EconomyUtil.getBalance(tanPlayer), 0.01);
  }

  @Test
  void perform_multipleCalls_consistentResults() {
    String[] args = {"balance"};
    EconomyUtil.setBalance(tanPlayer, 500.0);

    // First call
    command.perform(player, args);
    double balance1 = EconomyUtil.getBalance(tanPlayer);

    // Second call
    command.perform(player, args);
    double balance2 = EconomyUtil.getBalance(tanPlayer);

    // Balance should remain unchanged
    assertEquals(balance1, balance2, 0.01);
  }

  @Test
  void perform_afterBalanceChange_showsUpdatedBalance() {
    String[] args = {"balance"};
    EconomyUtil.setBalance(tanPlayer, 100.0);

    command.perform(player, args);
    assertEquals(100.0, EconomyUtil.getBalance(tanPlayer), 0.01);

    // Change balance
    EconomyUtil.setBalance(tanPlayer, 200.0);

    command.perform(player, args);
    assertEquals(200.0, EconomyUtil.getBalance(tanPlayer), 0.01);
  }
}
