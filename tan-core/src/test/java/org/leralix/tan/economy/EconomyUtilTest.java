package org.leralix.tan.economy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Unit tests for EconomyUtil class testing economy operations (balance retrieval, deposits,
 * withdrawals) with support for both standalone and Vault-based economy systems.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("EconomyUtil Tests")
class EconomyUtilTest {

  @Mock private AbstractTanEcon mockEcon;

  @Mock private TanEconomyStandalone mockStandaloneEcon;

  @Mock private ITanPlayer mockTanPlayer;

  @Mock private Player mockPlayer;

  @Mock private OfflinePlayer mockOfflinePlayer;

  @Mock private PlayerDataStorage mockPlayerDataStorage;

  private MockedStatic<PlayerDataStorage> playerDataStorageMock;

  @BeforeEach
  void setUp() {
    // Mock PlayerDataStorage singleton
    playerDataStorageMock = mockStatic(PlayerDataStorage.class);
    playerDataStorageMock.when(PlayerDataStorage::getInstance).thenReturn(mockPlayerDataStorage);

    // Setup default player data storage responses
    when(mockPlayerDataStorage.getSync(mockPlayer)).thenReturn(mockTanPlayer);
    when(mockPlayerDataStorage.getSync(mockOfflinePlayer)).thenReturn(mockTanPlayer);

    // Register default economy
    EconomyUtil.register(mockEcon);
  }

  @AfterEach
  void tearDown() {
    // Close static mock to avoid interference with other tests
    playerDataStorageMock.close();
  }

  // ============ REGISTER & SYSTEM TYPE TESTS ============

  @Test
  @DisplayName("Should register economy system successfully")
  void testRegister_Success() {
    // Arrange
    AbstractTanEcon newEcon = mock(AbstractTanEcon.class);

    // Act
    EconomyUtil.register(newEcon);

    // Assert
    // Verify by checking if methods work with new economy
    when(newEcon.getMoneyIcon()).thenReturn("$");
    assertEquals("$", EconomyUtil.getMoneyIcon());
  }

  @Test
  @DisplayName("Should return true when using standalone economy")
  void testIsStandalone_WhenUsingStandalone() {
    // Arrange
    EconomyUtil.register(mockStandaloneEcon);

    // Act
    boolean isStandalone = EconomyUtil.isStandalone();

    // Assert
    assertTrue(isStandalone);
  }

  @Test
  @DisplayName("Should return false when not using standalone economy")
  void testIsStandalone_WhenUsingVault() {
    // Arrange
    EconomyUtil.register(mockEcon); // Not standalone

    // Act
    boolean isStandalone = EconomyUtil.isStandalone();

    // Assert
    assertFalse(isStandalone);
  }

  // ============ GET BALANCE TESTS ============

  @Test
  @DisplayName("Should get balance with ITanPlayer")
  void testGetBalance_WithITanPlayer() {
    // Arrange
    double expectedBalance = 1000.0;
    when(mockEcon.getBalance(mockTanPlayer)).thenReturn(expectedBalance);

    // Act
    double balance = EconomyUtil.getBalance(mockTanPlayer);

    // Assert
    assertEquals(expectedBalance, balance, 0.01);
    verify(mockEcon).getBalance(mockTanPlayer);
  }

  @Test
  @DisplayName("Should get balance with Player")
  void testGetBalance_WithPlayer() {
    // Arrange
    double expectedBalance = 1500.0;
    when(mockEcon.getBalance(mockTanPlayer)).thenReturn(expectedBalance);

    // Act
    double balance = EconomyUtil.getBalance(mockPlayer);

    // Assert
    assertEquals(expectedBalance, balance, 0.01);
    verify(mockPlayerDataStorage).getSync(mockPlayer);
    verify(mockEcon).getBalance(mockTanPlayer);
  }

  @Test
  @DisplayName("Should get balance with OfflinePlayer")
  void testGetBalance_WithOfflinePlayer() {
    // Arrange
    double expectedBalance = 2000.0;
    when(mockEcon.getBalance(mockTanPlayer)).thenReturn(expectedBalance);

    // Act
    double balance = EconomyUtil.getBalance(mockOfflinePlayer);

    // Assert
    assertEquals(expectedBalance, balance, 0.01);
    verify(mockPlayerDataStorage).getSync(mockOfflinePlayer);
    verify(mockEcon).getBalance(mockTanPlayer);
  }

  @Test
  @DisplayName("Should get zero balance when player has no money")
  void testGetBalance_ZeroBalance() {
    // Arrange
    when(mockEcon.getBalance(mockTanPlayer)).thenReturn(0.0);

    // Act
    double balance = EconomyUtil.getBalance(mockTanPlayer);

    // Assert
    assertEquals(0.0, balance, 0.01);
  }

  // ============ REMOVE FROM BALANCE TESTS ============

  @Test
  @DisplayName("Should remove from balance with ITanPlayer")
  void testRemoveFromBalance_WithITanPlayer() {
    // Arrange
    double amount = 100.0;

    // Act
    EconomyUtil.removeFromBalance(mockTanPlayer, amount);

    // Assert
    verify(mockEcon).withdrawPlayer(mockTanPlayer, amount);
  }

  @Test
  @DisplayName("Should remove from balance with Player")
  void testRemoveFromBalance_WithPlayer() {
    // Arrange
    double amount = 250.0;

    // Act
    EconomyUtil.removeFromBalance(mockPlayer, amount);

    // Assert
    verify(mockPlayerDataStorage).getSync(mockPlayer);
    verify(mockEcon).withdrawPlayer(mockTanPlayer, amount);
  }

  @Test
  @DisplayName("Should remove from balance with OfflinePlayer")
  void testRemoveFromBalance_WithOfflinePlayer() {
    // Arrange
    double amount = 500.0;

    // Act
    EconomyUtil.removeFromBalance(mockOfflinePlayer, amount);

    // Assert
    verify(mockPlayerDataStorage).getSync(mockOfflinePlayer);
    verify(mockEcon).withdrawPlayer(mockTanPlayer, amount);
  }

  // ============ ADD TO BALANCE TESTS ============

  @Test
  @DisplayName("Should add to balance with ITanPlayer")
  void testAddFromBalance_WithITanPlayer() {
    // Arrange
    double amount = 300.0;

    // Act
    EconomyUtil.addFromBalance(mockTanPlayer, amount);

    // Assert
    verify(mockEcon).depositPlayer(mockTanPlayer, amount);
  }

  @Test
  @DisplayName("Should add to balance with Player")
  void testAddFromBalance_WithPlayer() {
    // Arrange
    double amount = 450.0;

    // Act
    EconomyUtil.addFromBalance(mockPlayer, amount);

    // Assert
    verify(mockPlayerDataStorage).getSync(mockPlayer);
    verify(mockEcon).depositPlayer(mockTanPlayer, amount);
  }

  @Test
  @DisplayName("Should add to balance with OfflinePlayer")
  void testAddFromBalance_WithOfflinePlayer() {
    // Arrange
    double amount = 600.0;

    // Act
    EconomyUtil.addFromBalance(mockOfflinePlayer, amount);

    // Assert
    verify(mockPlayerDataStorage).getSync(mockOfflinePlayer);
    verify(mockEcon).depositPlayer(mockTanPlayer, amount);
  }

  // ============ SET BALANCE TESTS ============

  @Test
  @DisplayName("Should set balance to specific amount")
  void testSetBalance_ToSpecificAmount() {
    // Arrange
    double currentBalance = 500.0;
    double targetBalance = 1000.0;
    when(mockEcon.getBalance(mockTanPlayer)).thenReturn(currentBalance);

    // Act
    EconomyUtil.setBalance(mockTanPlayer, targetBalance);

    // Assert
    verify(mockEcon).withdrawPlayer(mockTanPlayer, currentBalance); // Remove current
    verify(mockEcon).depositPlayer(mockTanPlayer, targetBalance); // Add new amount
  }

  @Test
  @DisplayName("Should set balance to zero")
  void testSetBalance_ToZero() {
    // Arrange
    double currentBalance = 1000.0;
    when(mockEcon.getBalance(mockTanPlayer)).thenReturn(currentBalance);

    // Act
    EconomyUtil.setBalance(mockTanPlayer, 0.0);

    // Assert
    verify(mockEcon).withdrawPlayer(mockTanPlayer, currentBalance);
    verify(mockEcon).depositPlayer(mockTanPlayer, 0.0);
  }

  @Test
  @DisplayName("Should handle setting balance when current balance is zero")
  void testSetBalance_FromZero() {
    // Arrange
    double targetBalance = 5000.0;
    when(mockEcon.getBalance(mockTanPlayer)).thenReturn(0.0);

    // Act
    EconomyUtil.setBalance(mockTanPlayer, targetBalance);

    // Assert
    verify(mockEcon).withdrawPlayer(mockTanPlayer, 0.0);
    verify(mockEcon).depositPlayer(mockTanPlayer, targetBalance);
  }

  // ============ GET MONEY ICON TESTS ============

  @Test
  @DisplayName("Should get money icon from economy system")
  void testGetMoneyIcon() {
    // Arrange
    String expectedIcon = "$";
    when(mockEcon.getMoneyIcon()).thenReturn(expectedIcon);

    // Act
    String icon = EconomyUtil.getMoneyIcon();

    // Assert
    assertEquals(expectedIcon, icon);
    verify(mockEcon).getMoneyIcon();
  }

  @Test
  @DisplayName("Should get custom money icon")
  void testGetMoneyIcon_CustomIcon() {
    // Arrange
    String customIcon = "⛃";
    when(mockEcon.getMoneyIcon()).thenReturn(customIcon);

    // Act
    String icon = EconomyUtil.getMoneyIcon();

    // Assert
    assertEquals(customIcon, icon);
  }

  // ============ EDGE CASE TESTS ============

  @Test
  @DisplayName("Should handle large balance amounts")
  void testGetBalance_LargeAmount() {
    // Arrange
    double largeBalance = 1_000_000_000.0;
    when(mockEcon.getBalance(mockTanPlayer)).thenReturn(largeBalance);

    // Act
    double balance = EconomyUtil.getBalance(mockTanPlayer);

    // Assert
    assertEquals(largeBalance, balance, 0.01);
  }

  @Test
  @DisplayName("Should handle fractional balance amounts")
  void testGetBalance_FractionalAmount() {
    // Arrange
    double fractionalBalance = 123.456;
    when(mockEcon.getBalance(mockTanPlayer)).thenReturn(fractionalBalance);

    // Act
    double balance = EconomyUtil.getBalance(mockTanPlayer);

    // Assert
    assertEquals(fractionalBalance, balance, 0.001);
  }

  @Test
  @DisplayName("Should handle removing large amounts")
  void testRemoveFromBalance_LargeAmount() {
    // Arrange
    double largeAmount = 10_000_000.0;

    // Act
    EconomyUtil.removeFromBalance(mockTanPlayer, largeAmount);

    // Assert
    verify(mockEcon).withdrawPlayer(mockTanPlayer, largeAmount);
  }

  @Test
  @DisplayName("Should handle adding zero amount")
  void testAddFromBalance_ZeroAmount() {
    // Arrange
    double zeroAmount = 0.0;

    // Act
    EconomyUtil.addFromBalance(mockTanPlayer, zeroAmount);

    // Assert
    verify(mockEcon).depositPlayer(mockTanPlayer, zeroAmount);
  }

  @Test
  @DisplayName("Should handle removing zero amount")
  void testRemoveFromBalance_ZeroAmount() {
    // Arrange
    double zeroAmount = 0.0;

    // Act
    EconomyUtil.removeFromBalance(mockTanPlayer, zeroAmount);

    // Assert
    verify(mockEcon).withdrawPlayer(mockTanPlayer, zeroAmount);
  }

  // ============ INTEGRATION TESTS ============

  @Test
  @DisplayName("Should handle multiple operations in sequence")
  void testMultipleOperations_InSequence() {
    // Arrange
    when(mockEcon.getBalance(mockTanPlayer))
        .thenReturn(1000.0) // Initial
        .thenReturn(1500.0) // After add
        .thenReturn(1200.0); // After remove

    // Act & Assert
    assertEquals(1000.0, EconomyUtil.getBalance(mockTanPlayer), 0.01);

    EconomyUtil.addFromBalance(mockTanPlayer, 500.0);
    verify(mockEcon).depositPlayer(mockTanPlayer, 500.0);

    assertEquals(1500.0, EconomyUtil.getBalance(mockTanPlayer), 0.01);

    EconomyUtil.removeFromBalance(mockTanPlayer, 300.0);
    verify(mockEcon).withdrawPlayer(mockTanPlayer, 300.0);

    assertEquals(1200.0, EconomyUtil.getBalance(mockTanPlayer), 0.01);
  }

  @Test
  @DisplayName("Should work correctly after re-registering economy system")
  void testReRegisterEconomy() {
    // Arrange
    AbstractTanEcon newEcon = mock(AbstractTanEcon.class);
    when(newEcon.getBalance(mockTanPlayer)).thenReturn(999.0);

    // Act
    EconomyUtil.register(newEcon);
    double balance = EconomyUtil.getBalance(mockTanPlayer);

    // Assert
    assertEquals(999.0, balance, 0.01);
    verify(newEcon).getBalance(mockTanPlayer);
    verify(mockEcon, never()).getBalance(any()); // Old econ not called
  }
}
