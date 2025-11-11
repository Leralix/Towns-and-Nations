package org.leralix.tan.dataclass.territory.economy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.FilledLang;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for SubjectTaxLine class testing tax calculations and tax line management. */
@ExtendWith(MockitoExtension.class)
@DisplayName("SubjectTaxLine Tests")
class SubjectTaxLineTest {

  @Mock private RegionData mockRegionData;

  @Mock private TerritoryData mockVassal1;

  @Mock private TerritoryData mockVassal2;

  @Mock private TerritoryData mockVassal3;

  private SubjectTaxLine taxLine;

  @BeforeEach
  void setUp() {
    // Setup default mock behaviors
    when(mockRegionData.getTax()).thenReturn(100.0);
  }

  // ============ TAX LINE CREATION TESTS ============

  @Test
  @DisplayName("Should create SubjectTaxLine with region data")
  void testCreateSubjectTaxLine() {
    // Arrange
    when(mockRegionData.getVassals()).thenReturn(java.util.Collections.emptyList());

    // Act
    taxLine = new SubjectTaxLine(mockRegionData);

    // Assert
    assertNotNull(taxLine);
  }

  @Test
  @DisplayName("Should calculate actual taxes from wealthy vassals")
  void testCalculateActualTaxesFromWealthyVassals() {
    // Arrange
    double taxRate = 100.0;
    double vassal1Balance = 500.0;
    double vassal2Balance = 300.0;

    when(mockRegionData.getTax()).thenReturn(taxRate);
    when(mockVassal1.getBalance()).thenReturn(vassal1Balance);
    when(mockVassal2.getBalance()).thenReturn(vassal2Balance);
    when(mockRegionData.getVassals()).thenReturn(java.util.Arrays.asList(mockVassal1, mockVassal2));

    // Act
    taxLine = new SubjectTaxLine(mockRegionData);

    // Assert
    // Both vassals should pay tax (200 total)
    assertEquals(200.0, taxLine.getMoney(), 0.01);
  }

  @Test
  @DisplayName("Should calculate missing taxes from poor vassals")
  void testCalculateMissingTaxesFromPoorVassals() {
    // Arrange
    double taxRate = 100.0;
    double vassal1Balance = 50.0; // Can't pay full tax
    double vassal2Balance = 30.0; // Can't pay full tax

    when(mockRegionData.getTax()).thenReturn(taxRate);
    when(mockVassal1.getBalance()).thenReturn(vassal1Balance);
    when(mockVassal2.getBalance()).thenReturn(vassal2Balance);
    when(mockRegionData.getVassals()).thenReturn(java.util.Arrays.asList(mockVassal1, mockVassal2));

    // Act
    taxLine = new SubjectTaxLine(mockRegionData);

    // Assert
    // Both vassals should have missing taxes
    assertEquals(0.0, taxLine.getMoney(), 0.01); // They can't pay
  }

  @Test
  @DisplayName("Should mix actual and missing taxes")
  void testMixActualAndMissingTaxes() {
    // Arrange
    double taxRate = 100.0;
    when(mockRegionData.getTax()).thenReturn(taxRate);
    when(mockVassal1.getBalance()).thenReturn(500.0); // Can pay
    when(mockVassal2.getBalance()).thenReturn(50.0); // Can't pay
    when(mockVassal3.getBalance()).thenReturn(300.0); // Can pay
    when(mockRegionData.getVassals())
        .thenReturn(java.util.Arrays.asList(mockVassal1, mockVassal2, mockVassal3));

    // Act
    taxLine = new SubjectTaxLine(mockRegionData);

    // Assert
    // Should collect 200 from 2 vassals, miss 100 from 1 vassal
    assertEquals(200.0, taxLine.getMoney(), 0.01);
  }

  // ============ TAX LINE LORE GENERATION TESTS ============

  @Test
  @DisplayName("Should generate lore with all taxes collected")
  void testGenerateLoreAllCollected() {
    // Arrange
    when(mockRegionData.getTax()).thenReturn(100.0);
    when(mockVassal1.getBalance()).thenReturn(500.0);
    when(mockRegionData.getVassals()).thenReturn(java.util.Collections.singletonList(mockVassal1));

    // Act
    taxLine = new SubjectTaxLine(mockRegionData);
    FilledLang lore = taxLine.getLine();

    // Assert
    assertNotNull(lore);
  }

  @Test
  @DisplayName("Should generate lore with missing taxes")
  void testGenerateLoreMissingTaxes() {
    // Arrange
    when(mockRegionData.getTax()).thenReturn(100.0);
    when(mockVassal1.getBalance()).thenReturn(30.0); // Can't pay full tax
    when(mockRegionData.getVassals()).thenReturn(java.util.Collections.singletonList(mockVassal1));

    // Act
    taxLine = new SubjectTaxLine(mockRegionData);
    FilledLang lore = taxLine.getLine();

    // Assert
    assertNotNull(lore);
  }

  // ============ TAX AMOUNT TESTS ============

  @Test
  @DisplayName("Should get correct money amount from getMoney()")
  void testGetMoney() {
    // Arrange
    when(mockRegionData.getTax()).thenReturn(100.0);
    when(mockVassal1.getBalance()).thenReturn(500.0);
    when(mockRegionData.getVassals()).thenReturn(java.util.Collections.singletonList(mockVassal1));

    // Act
    taxLine = new SubjectTaxLine(mockRegionData);
    double money = taxLine.getMoney();

    // Assert
    assertEquals(100.0, money, 0.01);
  }

  // ============ MULTIPLE VASSALS TESTS ============

  @Test
  @DisplayName("Should handle multiple vassals correctly")
  void testMultipleVassals() {
    // Arrange
    double taxRate = 50.0;
    when(mockRegionData.getTax()).thenReturn(taxRate);
    when(mockVassal1.getBalance()).thenReturn(200.0);
    when(mockVassal2.getBalance()).thenReturn(150.0);
    when(mockVassal3.getBalance()).thenReturn(100.0);
    when(mockRegionData.getVassals())
        .thenReturn(java.util.Arrays.asList(mockVassal1, mockVassal2, mockVassal3));

    // Act
    taxLine = new SubjectTaxLine(mockRegionData);

    // Assert
    // All 3 vassals can pay 50 each = 150 total
    assertEquals(150.0, taxLine.getMoney(), 0.01);
  }

  @Test
  @DisplayName("Should handle no vassals")
  void testNoVassals() {
    // Arrange
    when(mockRegionData.getTax()).thenReturn(100.0);
    when(mockRegionData.getVassals()).thenReturn(java.util.Collections.emptyList());

    // Act
    taxLine = new SubjectTaxLine(mockRegionData);

    // Assert
    assertEquals(0.0, taxLine.getMoney(), 0.01);
  }

  // ============ EDGE CASE TESTS ============

  @Test
  @DisplayName("Should handle zero tax rate")
  void testZeroTaxRate() {
    // Arrange
    when(mockRegionData.getTax()).thenReturn(0.0);
    when(mockVassal1.getBalance()).thenReturn(500.0);
    when(mockRegionData.getVassals()).thenReturn(java.util.Collections.singletonList(mockVassal1));

    // Act
    taxLine = new SubjectTaxLine(mockRegionData);

    // Assert
    assertEquals(0.0, taxLine.getMoney(), 0.01);
  }

  @Test
  @DisplayName("Should handle very high tax rate")
  void testHighTaxRate() {
    // Arrange
    double taxRate = 10000.0;
    when(mockRegionData.getTax()).thenReturn(taxRate);
    when(mockVassal1.getBalance()).thenReturn(20000.0);
    when(mockRegionData.getVassals()).thenReturn(java.util.Collections.singletonList(mockVassal1));

    // Act
    taxLine = new SubjectTaxLine(mockRegionData);

    // Assert
    assertEquals(taxRate, taxLine.getMoney(), 0.01);
  }

  @Test
  @DisplayName("Should handle vassal with exact tax balance")
  void testVassalWithExactBalance() {
    // Arrange
    double taxRate = 100.0;
    when(mockRegionData.getTax()).thenReturn(taxRate);
    when(mockVassal1.getBalance()).thenReturn(taxRate); // Exact amount
    when(mockRegionData.getVassals()).thenReturn(java.util.Collections.singletonList(mockVassal1));

    // Act
    taxLine = new SubjectTaxLine(mockRegionData);

    // Assert
    assertEquals(taxRate, taxLine.getMoney(), 0.01);
  }

  @Test
  @DisplayName("Should handle fractional tax amounts")
  void testFractionalTaxAmount() {
    // Arrange
    double taxRate = 33.33;
    when(mockRegionData.getTax()).thenReturn(taxRate);
    when(mockVassal1.getBalance()).thenReturn(100.0);
    when(mockVassal2.getBalance()).thenReturn(100.0);
    when(mockVassal3.getBalance()).thenReturn(100.0);
    when(mockRegionData.getVassals())
        .thenReturn(java.util.Arrays.asList(mockVassal1, mockVassal2, mockVassal3));

    // Act
    taxLine = new SubjectTaxLine(mockRegionData);

    // Assert
    // Should collect 33.33 * 3 = 99.99
    assertEquals(99.99, taxLine.getMoney(), 0.01);
  }
}
