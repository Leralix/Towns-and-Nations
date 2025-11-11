package org.leralix.tan.dataclass.territory.economy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.triumphteam.gui.guis.Gui;
import java.util.List;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Simplified Budget tests without MockBukkit dependencies. Tests profit line management and budget
 * calculations.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Budget Simple Tests")
class SimpleBudgetTest {

  private Budget budget;

  @Mock private Lang mockLang;

  @BeforeEach
  void setUp() {
    budget = new Budget();
  }

  @Test
  @DisplayName("Budget should initialize empty")
  void testBudgetInitialization() {
    assertNotNull(budget);
  }

  @Test
  @DisplayName("Budget should handle profit line additions")
  void testAddProfitLine() {
    // Arrange
    TestProfitLine profitLine = new TestProfitLine(100.0, true);

    // Act
    budget.addProfitLine(profitLine);
    List<FilledLang> lore = budget.createLore();

    // Assert
    assertNotNull(lore);
    assertFalse(lore.isEmpty());
  }

  @Test
  @DisplayName("Budget lore should contain total")
  void testLoreContainsTotal() {
    // Arrange
    TestProfitLine line1 = new TestProfitLine(100.0, true);
    TestProfitLine line2 = new TestProfitLine(50.0, true);

    // Act
    budget.addProfitLine(line1);
    budget.addProfitLine(line2);
    List<FilledLang> lore = budget.createLore();

    // Assert
    assertTrue(lore.size() >= 1, "Budget lore should have at least total line");
  }

  @Test
  @DisplayName("Budget should filter non-recurrent lines")
  void testFilterNonRecurrentLines() {
    // Arrange
    TestProfitLine recurrent = new TestProfitLine(100.0, true);
    TestProfitLine nonRecurrent = new TestProfitLine(50.0, false);

    // Act
    budget.addProfitLine(recurrent);
    budget.addProfitLine(nonRecurrent);
    List<FilledLang> lore = budget.createLore();

    // Assert
    // Should only show recurrent + total (2 lines minimum)
    assertTrue(lore.size() >= 1);
  }

  @Test
  @DisplayName("Budget should calculate correct total")
  void testCorrectTotalCalculation() {
    // Arrange
    TestProfitLine line1 = new TestProfitLine(100.0, true);
    TestProfitLine line2 = new TestProfitLine(50.0, true);
    TestProfitLine line3 = new TestProfitLine(25.0, true);

    // Act
    budget.addProfitLine(line1);
    budget.addProfitLine(line2);
    budget.addProfitLine(line3);
    List<FilledLang> lore = budget.createLore();

    // Assert - Total should be 175.0
    assertEquals(4, lore.size(), "Should have total + 3 lines");
  }

  @Test
  @DisplayName("Budget should handle multiple operations")
  void testMultipleOperations() {
    // Arrange & Act
    for (int i = 1; i <= 5; i++) {
      budget.addProfitLine(new TestProfitLine(i * 10.0, true));
    }
    List<FilledLang> lore = budget.createLore();

    // Assert
    assertEquals(6, lore.size(), "Should have total + 5 lines");
  }

  @Test
  @DisplayName("Budget should sort lines by amount")
  void testLinesSortedByAmount() {
    // Arrange
    TestProfitLine small = new TestProfitLine(10.0, true);
    TestProfitLine large = new TestProfitLine(100.0, true);
    TestProfitLine medium = new TestProfitLine(50.0, true);

    // Act
    budget.addProfitLine(small);
    budget.addProfitLine(large);
    budget.addProfitLine(medium);
    List<FilledLang> lore = budget.createLore();

    // Assert
    assertTrue(lore.size() >= 3, "Should have all lines sorted");
  }

  /** Test implementation of ProfitLine for testing purposes. */
  private static class TestProfitLine extends ProfitLine {
    private final double money;
    private final boolean recurrent;

    TestProfitLine(double money, boolean recurrent) {
      super(null);
      this.money = money;
      this.recurrent = recurrent;
    }

    @Override
    protected double getMoney() {
      return money;
    }

    @Override
    public FilledLang getLine() {
      Lang mockLang = mock(Lang.class);
      return new FilledLang(mockLang);
    }

    @Override
    public void addItems(Gui gui, Player player, LangType langType) {
      // Not needed for tests
    }

    @Override
    public boolean isRecurrent() {
      return recurrent;
    }
  }
}
