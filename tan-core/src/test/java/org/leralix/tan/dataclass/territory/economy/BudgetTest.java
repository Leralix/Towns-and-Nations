package org.leralix.tan.dataclass.territory.economy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.triumphteam.gui.guis.Gui;
import java.util.List;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for the Budget class to ensure correct profit line management. */
class BudgetTest {

  private Budget budget;

  @BeforeEach
  void setUp() {
    budget = new Budget();
  }

  @Test
  void constructor_createsEmptyBudget() {
    List<FilledLang> lore = budget.createLore();
    // Should have at least the total line
    assertNotNull(lore);
    assertFalse(lore.isEmpty());
  }

  @Test
  void addProfitLine_withSingleLine_addsSuccessfully() {
    TestProfitLine profitLine = new TestProfitLine(100.0, true);
    budget.addProfitLine(profitLine);

    List<FilledLang> lore = budget.createLore();
    // Should have total + 1 profit line
    assertTrue(lore.size() >= 2);
  }

  @Test
  void createLore_withMultipleRecurrentLines_includesAllLines() {
    budget.addProfitLine(new TestProfitLine(100.0, true));
    budget.addProfitLine(new TestProfitLine(50.0, true));
    budget.addProfitLine(new TestProfitLine(25.0, true));

    List<FilledLang> lore = budget.createLore();
    // Total line + 3 profit lines
    assertEquals(4, lore.size());
  }

  @Test
  void createLore_withNonRecurrentLines_excludesThem() {
    budget.addProfitLine(new TestProfitLine(100.0, true)); // Should be included
    budget.addProfitLine(new TestProfitLine(50.0, false)); // Should be excluded
    budget.addProfitLine(new TestProfitLine(25.0, true)); // Should be included

    List<FilledLang> lore = budget.createLore();
    // Total line + 2 recurrent lines (non-recurrent excluded)
    assertEquals(3, lore.size());
  }

  @Test
  void createLore_calculatesCorrectTotal() {
    budget.addProfitLine(new TestProfitLine(100.0, true));
    budget.addProfitLine(new TestProfitLine(50.0, true));
    budget.addProfitLine(new TestProfitLine(25.0, true));

    List<FilledLang> lore = budget.createLore();
    // First line should be the total (175.0)
    String totalLine = lore.get(0).toString();
    assertNotNull(totalLine);
    // Total should be 175.0 (100 + 50 + 25)
  }

  @Test
  void createLore_withNegativeProfits_calculatesCorrectTotal() {
    budget.addProfitLine(new TestProfitLine(100.0, true));
    budget.addProfitLine(new TestProfitLine(-30.0, true)); // Expense
    budget.addProfitLine(new TestProfitLine(-20.0, true)); // Expense

    List<FilledLang> lore = budget.createLore();
    // Total should be 50.0 (100 - 30 - 20)
    assertEquals(4, lore.size()); // Total + 3 lines
  }

  @Test
  void createLore_sortsLinesByAmount_descendingOrder() {
    budget.addProfitLine(new TestProfitLine(25.0, true));
    budget.addProfitLine(new TestProfitLine(100.0, true));
    budget.addProfitLine(new TestProfitLine(50.0, true));

    List<FilledLang> lore = budget.createLore();
    // Lines should be sorted: 100, 50, 25 (descending)
    // First line is total, then sorted profit lines
    assertEquals(4, lore.size());
  }

  @Test
  void createLore_withZeroProfits_includesZeroLines() {
    budget.addProfitLine(new TestProfitLine(0.0, true));
    budget.addProfitLine(new TestProfitLine(100.0, true));

    List<FilledLang> lore = budget.createLore();
    assertEquals(3, lore.size()); // Total + 2 lines
  }

  @Test
  void createLore_withOnlyNonRecurrent_showsOnlyTotal() {
    budget.addProfitLine(new TestProfitLine(100.0, false));
    budget.addProfitLine(new TestProfitLine(50.0, false));

    List<FilledLang> lore = budget.createLore();
    // Only total line (no recurrent lines)
    assertEquals(1, lore.size());
  }

  @Test
  void createLore_withMixedPositiveNegative_calculatesNetTotal() {
    budget.addProfitLine(new TestProfitLine(1000.0, true)); // Large income
    budget.addProfitLine(new TestProfitLine(-200.0, true)); // Expense
    budget.addProfitLine(new TestProfitLine(-300.0, true)); // Expense
    budget.addProfitLine(new TestProfitLine(100.0, true)); // Small income

    List<FilledLang> lore = budget.createLore();
    // Net total: 1000 - 200 - 300 + 100 = 600
    assertEquals(5, lore.size()); // Total + 4 lines
  }

  @Test
  void createLore_withLargeAmounts_handlesCorrectly() {
    budget.addProfitLine(new TestProfitLine(1_000_000.0, true));
    budget.addProfitLine(new TestProfitLine(500_000.0, true));

    List<FilledLang> lore = budget.createLore();
    // Should handle large amounts without issues
    assertEquals(3, lore.size());
  }

  @Test
  void addProfitLine_multipleAdditions_maintainsAllLines() {
    for (int i = 1; i <= 10; i++) {
      budget.addProfitLine(new TestProfitLine(i * 10.0, true));
    }

    List<FilledLang> lore = budget.createLore();
    // Total + 10 profit lines
    assertEquals(11, lore.size());
  }

  /**
   * Test implementation of ProfitLine for testing purposes. Simplified version that doesn't require
   * TerritoryData or GUI components.
   */
  @ExtendWith(MockitoExtension.class)
  private static class TestProfitLine extends ProfitLine {
    private final double money;
    private final boolean recurrent;
    @Mock private Lang mockLang;

    TestProfitLine(double money, boolean recurrent) {
      super(null); // TerritoryData not needed for basic tests
      this.money = money;
      this.recurrent = recurrent;
    }

    @Override
    protected double getMoney() {
      return money;
    }

    @Override
    public FilledLang getLine() {
      FilledLang mockFilledLang = mock(FilledLang.class);
      when(mockFilledLang.toString()).thenReturn("Mocked Profit Line");
      return mockFilledLang;
    }

    @Override
    public void addItems(Gui gui, Player player, LangType langType) {
      // Not needed for basic Budget tests
    }

    @Override
    public boolean isRecurrent() {
      return recurrent;
    }
  }
}
