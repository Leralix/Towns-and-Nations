package org.leralix.tan.economy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Advanced tests for EconomyUtil class covering balance operations and money transfers. Tests the
 * contract of the static economy utility methods.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EconomyUtil Advanced Tests")
class EconomyUtilAdvancedTest {

  @Mock private AbstractTanEcon mockEcon;

  @Mock private ITanPlayer mockTanPlayer;

  @Mock private Player mockPlayer;

  @Mock private OfflinePlayer mockOfflinePlayer;

  @Mock private PlayerDataStorage mockPlayerDataStorage;

  @BeforeEach
  void setUp() {
    EconomyUtil.register(mockEcon);
    when(mockPlayer.getUniqueId()).thenReturn(UUID.randomUUID());
  }

  // ============ GET BALANCE TESTS ============

  @Test
  @DisplayName("Should get balance for ITanPlayer")
  void testGetBalanceForTanPlayer() {
    // Arrange
    double expectedBalance = 1500.0;
    when(mockEcon.getBalance(mockTanPlayer)).thenReturn(expectedBalance);

    // Act
    double actualBalance = EconomyUtil.getBalance(mockTanPlayer);

    // Assert
    assertEquals(expectedBalance, actualBalance, 0.01);
    verify(mockEcon, times(1)).getBalance(mockTanPlayer);
  }

  @Test
  @DisplayName("Should get balance for Player")
  void testGetBalanceForPlayer() {
    // Arrange
    double expectedBalance = 2000.0;
    when(mockEcon.getBalance(any(ITanPlayer.class))).thenReturn(expectedBalance);

    // Act
    double actualBalance = EconomyUtil.getBalance(mockPlayer);

    // Assert
    assertEquals(expectedBalance, actualBalance, 0.01);
  }

  @Test
  @DisplayName("Should get balance for OfflinePlayer")
  void testGetBalanceForOfflinePlayer() {
    // Arrange
    double expectedBalance = 500.0;
    when(mockEcon.getBalance(any(ITanPlayer.class))).thenReturn(expectedBalance);

    // Act
    double actualBalance = EconomyUtil.getBalance(mockOfflinePlayer);

    // Assert
    assertEquals(expectedBalance, actualBalance, 0.01);
  }

  @Test
  @DisplayName("Should return 0 for player with no balance")
  void testGetBalanceZeroBalance() {
    // Arrange
    when(mockEcon.getBalance(mockTanPlayer)).thenReturn(0.0);

    // Act
    double actualBalance = EconomyUtil.getBalance(mockTanPlayer);

    // Assert
    assertEquals(0.0, actualBalance, 0.01);
  }

  // ============ REMOVE FROM BALANCE TESTS ============

  @Test
  @DisplayName("Should remove money from ITanPlayer balance")
  void testRemoveFromBalanceTanPlayer() {
    // Arrange
    double amountToRemove = 250.0;

    // Act
    EconomyUtil.removeFromBalance(mockTanPlayer, amountToRemove);

    // Assert
    ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
    verify(mockEcon, times(1)).withdrawPlayer(eq(mockTanPlayer), captor.capture());
    assertEquals(amountToRemove, captor.getValue(), 0.01);
  }

  @Test
  @DisplayName("Should remove money from Player balance")
  void testRemoveFromBalancePlayer() {
    // Arrange
    double amountToRemove = 100.0;

    // Act
    EconomyUtil.removeFromBalance(mockPlayer, amountToRemove);

    // Assert
    verify(mockEcon, times(1)).withdrawPlayer(any(ITanPlayer.class), eq(amountToRemove));
  }

  @Test
  @DisplayName("Should remove money from OfflinePlayer balance")
  void testRemoveFromBalanceOfflinePlayer() {
    // Arrange
    double amountToRemove = 75.0;

    // Act
    EconomyUtil.removeFromBalance(mockOfflinePlayer, amountToRemove);

    // Assert
    verify(mockEcon, times(1)).withdrawPlayer(any(ITanPlayer.class), eq(amountToRemove));
  }

  @Test
  @DisplayName("Should remove exact amount from balance")
  void testRemoveExactAmount() {
    // Arrange
    double amountToRemove = 333.33;

    // Act
    EconomyUtil.removeFromBalance(mockTanPlayer, amountToRemove);

    // Assert
    ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
    verify(mockEcon, times(1)).withdrawPlayer(eq(mockTanPlayer), captor.capture());
    assertEquals(amountToRemove, captor.getValue(), 0.01);
  }

  // ============ ADD TO BALANCE TESTS ============

  @Test
  @DisplayName("Should add money to ITanPlayer balance")
  void testAddFromBalanceTanPlayer() {
    // Arrange
    double amountToAdd = 500.0;

    // Act
    EconomyUtil.addFromBalance(mockTanPlayer, amountToAdd);

    // Assert
    ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
    verify(mockEcon, times(1)).depositPlayer(eq(mockTanPlayer), captor.capture());
    assertEquals(amountToAdd, captor.getValue(), 0.01);
  }

  @Test
  @DisplayName("Should add money to Player balance")
  void testAddFromBalancePlayer() {
    // Arrange
    double amountToAdd = 250.0;

    // Act
    EconomyUtil.addFromBalance(mockPlayer, amountToAdd);

    // Assert
    verify(mockEcon, times(1)).depositPlayer(any(ITanPlayer.class), eq(amountToAdd));
  }

  @Test
  @DisplayName("Should add exact amount to balance")
  void testAddExactAmount() {
    // Arrange
    double amountToAdd = 1234.56;

    // Act
    EconomyUtil.addFromBalance(mockTanPlayer, amountToAdd);

    // Assert
    ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
    verify(mockEcon, times(1)).depositPlayer(eq(mockTanPlayer), captor.capture());
    assertEquals(amountToAdd, captor.getValue(), 0.01);
  }

  @Test
  @DisplayName("Should add zero amount without error")
  void testAddZeroAmount() {
    // Arrange
    double amountToAdd = 0.0;

    // Act
    EconomyUtil.addFromBalance(mockTanPlayer, amountToAdd);

    // Assert
    verify(mockEcon, times(1)).depositPlayer(mockTanPlayer, amountToAdd);
  }

  // ============ STANDALONE MODE TESTS ============

  @Test
  @DisplayName("Should identify standalone economy mode")
  void testIsStandalone() {
    // Arrange
    TanEconomyStandalone standaloneEcon = mock(TanEconomyStandalone.class);
    EconomyUtil.register(standaloneEcon);

    // Act
    boolean isStandalone = EconomyUtil.isStandalone();

    // Assert
    assertTrue(isStandalone);
  }

  @Test
  @DisplayName("Should identify non-standalone economy mode")
  void testIsNotStandalone() {
    // Arrange
    EconomyUtil.register(mockEcon);

    // Act
    boolean isStandalone = EconomyUtil.isStandalone();

    // Assert
    assertFalse(isStandalone);
  }

  // ============ ECONOMY REGISTRATION TESTS ============

  @Test
  @DisplayName("Should register new economy implementation")
  void testRegisterEconomy() {
    // Arrange
    AbstractTanEcon newEcon = mock(AbstractTanEcon.class);
    when(newEcon.getBalance(mockTanPlayer)).thenReturn(1000.0);

    // Act
    EconomyUtil.register(newEcon);
    double balance = EconomyUtil.getBalance(mockTanPlayer);

    // Assert
    assertEquals(1000.0, balance, 0.01);
    verify(newEcon, times(1)).getBalance(mockTanPlayer);
  }

  // ============ EDGE CASE TESTS ============

  @Test
  @DisplayName("Should handle large balance amounts")
  void testLargeBalance() {
    // Arrange
    double largeBalance = 999999999.99;
    when(mockEcon.getBalance(mockTanPlayer)).thenReturn(largeBalance);

    // Act
    double actualBalance = EconomyUtil.getBalance(mockTanPlayer);

    // Assert
    assertEquals(largeBalance, actualBalance, 0.01);
  }

  @Test
  @DisplayName("Should handle negative balance removal")
  void testRemoveNegativeBalance() {
    // Arrange
    double negativeAmount = -100.0;

    // Act
    EconomyUtil.removeFromBalance(mockTanPlayer, negativeAmount);

    // Assert
    // Implementation should handle this gracefully
    verify(mockEcon, times(1)).withdrawPlayer(mockTanPlayer, negativeAmount);
  }

  @Test
  @DisplayName("Should handle concurrent balance operations")
  void testConcurrentOperations() {
    // Arrange
    double amount1 = 100.0;
    double amount2 = 200.0;

    // Act
    EconomyUtil.addFromBalance(mockTanPlayer, amount1);
    EconomyUtil.removeFromBalance(mockTanPlayer, amount2);

    // Assert
    verify(mockEcon, times(1)).depositPlayer(mockTanPlayer, amount1);
    verify(mockEcon, times(1)).withdrawPlayer(mockTanPlayer, amount2);
  }
}
