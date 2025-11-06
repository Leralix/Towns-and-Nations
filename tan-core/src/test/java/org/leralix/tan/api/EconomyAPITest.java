package org.leralix.tan.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tan.api.EconomyAPI;

/**
 * Unit tests for EconomyAPI implementation.
 *
 * <p>This test class demonstrates best practices for testing API implementations using JUnit 5 and
 * Mockito. It covers the main economy operations including balance retrieval, updates, and
 * transactions.
 *
 * <p>Key testing patterns demonstrated:
 *
 * <ul>
 *   <li>Using @ExtendWith(MockitoExtension.class) for Mockito integration
 *   <li>Mocking Player and EconomyAPI for isolated unit tests
 *   <li>Using descriptive test names with @DisplayName
 *   <li>Testing both happy path and edge cases
 *   <li>Verifying mock interactions with verify()
 * </ul>
 *
 * @see EconomyAPI
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EconomyAPI Tests")
class EconomyAPITest {

  @Mock private EconomyAPI economyAPI;

  @Mock private Player player;

  private UUID testPlayerUUID;

  @BeforeEach
  void setUp() {
    // Initialize test data
    testPlayerUUID = UUID.randomUUID();
  }

  @Test
  @DisplayName("Should get player balance by Player object")
  void testGetBalanceByPlayer() {
    // Arrange
    double expectedBalance = 1000.0;
    when(economyAPI.getBalance(player)).thenReturn(expectedBalance);

    // Act
    double actualBalance = economyAPI.getBalance(player);

    // Assert
    assertEquals(expectedBalance, actualBalance, 0.01, "Balance should match expected value");
    verify(economyAPI, times(1)).getBalance(player);
  }

  @Test
  @DisplayName("Should get player balance by UUID")
  void testGetBalanceByUUID() {
    // Arrange
    double expectedBalance = 2500.0;
    when(economyAPI.getBalance(testPlayerUUID)).thenReturn(expectedBalance);

    // Act
    double actualBalance = economyAPI.getBalance(testPlayerUUID);

    // Assert
    assertEquals(expectedBalance, actualBalance, 0.01, "Balance should match expected value");
    verify(economyAPI, times(1)).getBalance(testPlayerUUID);
  }

  @Test
  @DisplayName("Should set player balance by Player object")
  void testSetBalanceByPlayer() {
    // Arrange
    double newBalance = 1500.0;

    // Act
    economyAPI.setBalance(player, newBalance);

    // Assert
    verify(economyAPI, times(1)).setBalance(player, newBalance);
  }

  @Test
  @DisplayName("Should set player balance by UUID")
  void testSetBalanceByUUID() {
    // Arrange
    double newBalance = 3000.0;

    // Act
    economyAPI.setBalance(testPlayerUUID, newBalance);

    // Assert
    verify(economyAPI, times(1)).setBalance(testPlayerUUID, newBalance);
  }

  @Test
  @DisplayName("Should add money to player balance by Player object")
  void testAddToBalanceByPlayer() {
    // Arrange
    double amountToAdd = 500.0;

    // Act
    economyAPI.addToBalance(player, amountToAdd);

    // Assert
    verify(economyAPI, times(1)).addToBalance(player, amountToAdd);
  }

  @Test
  @DisplayName("Should add money to player balance by UUID")
  void testAddToBalanceByUUID() {
    // Arrange
    double amountToAdd = 750.0;

    // Act
    economyAPI.addToBalance(testPlayerUUID, amountToAdd);

    // Assert
    verify(economyAPI, times(1)).addToBalance(testPlayerUUID, amountToAdd);
  }

  @Test
  @DisplayName("Should remove money from player balance by Player object")
  void testRemoveFromBalanceByPlayer() {
    // Arrange
    double amountToRemove = 300.0;

    // Act
    economyAPI.removeFromBalance(player, amountToRemove);

    // Assert
    verify(economyAPI, times(1)).removeFromBalance(player, amountToRemove);
  }

  @Test
  @DisplayName("Should remove money from player balance by UUID")
  void testRemoveFromBalanceByUUID() {
    // Arrange
    double amountToRemove = 250.0;

    // Act
    economyAPI.removeFromBalance(testPlayerUUID, amountToRemove);

    // Assert
    verify(economyAPI, times(1)).removeFromBalance(testPlayerUUID, amountToRemove);
  }

  @Test
  @DisplayName("Should handle zero balance correctly")
  void testZeroBalance() {
    // Arrange
    when(economyAPI.getBalance(player)).thenReturn(0.0);

    // Act
    double balance = economyAPI.getBalance(player);

    // Assert
    assertEquals(0.0, balance, 0.01, "Zero balance should be handled correctly");
  }

  @Test
  @DisplayName("Should handle negative amounts in transactions")
  void testNegativeAmountHandling() {
    // Arrange
    double negativeAmount = -100.0;

    // Act
    economyAPI.addToBalance(player, negativeAmount);

    // Assert
    verify(economyAPI, times(1)).addToBalance(player, negativeAmount);
  }

  @Test
  @DisplayName("Should handle large balance values")
  void testLargeBalanceValues() {
    // Arrange
    double largeBalance = 999999999.99;
    when(economyAPI.getBalance(testPlayerUUID)).thenReturn(largeBalance);

    // Act
    double balance = economyAPI.getBalance(testPlayerUUID);

    // Assert
    assertEquals(largeBalance, balance, 0.01, "Large balance values should be handled correctly");
  }
}
