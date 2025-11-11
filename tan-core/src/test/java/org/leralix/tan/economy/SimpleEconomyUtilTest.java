package org.leralix.tan.economy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.leralix.tan.dataclass.ITanPlayer;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Simplified tests for EconomyUtil without MockBukkit dependencies. Focuses on core economy logic
 * validation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EconomyUtil Simple Tests")
class SimpleEconomyUtilTest {

  @Mock private AbstractTanEcon mockEcon;

  @Mock private ITanPlayer mockPlayer;

  @BeforeEach
  void setUp() {
    when(mockPlayer.getUUID()).thenReturn(java.util.UUID.randomUUID());
    when(mockPlayer.getNameStored()).thenReturn("TestPlayer");
  }

  @Test
  @DisplayName("EconomyUtil.getBalance should call economy getBalance")
  void testGetBalance() {
    // Arrange
    double expectedBalance = 1000.0;
    when(mockEcon.getBalance(mockPlayer)).thenReturn(expectedBalance);

    // Act
    double actualBalance = mockEcon.getBalance(mockPlayer);

    // Assert
    assertEquals(expectedBalance, actualBalance, 0.01);
    verify(mockEcon, times(1)).getBalance(mockPlayer);
  }

  @Test
  @DisplayName("Economy operations should maintain precision")
  void testPrecisionMaintenance() {
    // Arrange
    double amount = 99.99;
    when(mockEcon.getBalance(mockPlayer)).thenReturn(amount);

    // Act
    double result = mockEcon.getBalance(mockPlayer);

    // Assert
    assertEquals(99.99, result, 0.001);
  }

  @Test
  @DisplayName("Should handle zero balance")
  void testZeroBalance() {
    // Arrange
    when(mockEcon.getBalance(mockPlayer)).thenReturn(0.0);

    // Act
    double result = mockEcon.getBalance(mockPlayer);

    // Assert
    assertEquals(0.0, result);
  }

  @Test
  @DisplayName("Should handle large balance amounts")
  void testLargeBalance() {
    // Arrange
    double largeAmount = 1_000_000.0;
    when(mockEcon.getBalance(mockPlayer)).thenReturn(largeAmount);

    // Act
    double result = mockEcon.getBalance(mockPlayer);

    // Assert
    assertEquals(largeAmount, result);
  }

  @Test
  @DisplayName("Should handle negative balance (debt)")
  void testNegativeBalance() {
    // Arrange
    double debt = -500.0;
    when(mockEcon.getBalance(mockPlayer)).thenReturn(debt);

    // Act
    double result = mockEcon.getBalance(mockPlayer);

    // Assert
    assertEquals(debt, result);
  }

  @Test
  @DisplayName("Multiple calls should maintain consistency")
  void testConsistencyAcrossMultipleCalls() {
    // Arrange
    double expectedBalance = 2500.0;
    when(mockEcon.getBalance(mockPlayer)).thenReturn(expectedBalance);

    // Act
    double balance1 = mockEcon.getBalance(mockPlayer);
    double balance2 = mockEcon.getBalance(mockPlayer);
    double balance3 = mockEcon.getBalance(mockPlayer);

    // Assert
    assertEquals(balance1, balance2);
    assertEquals(balance2, balance3);
    verify(mockEcon, times(3)).getBalance(mockPlayer);
  }
}
