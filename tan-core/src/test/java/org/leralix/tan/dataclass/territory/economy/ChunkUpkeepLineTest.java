package org.leralix.tan.dataclass.territory.economy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.FilledLang;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChunkUpkeepLine Tests")
class ChunkUpkeepLineTest {

  private ChunkUpkeepLine upkeepLine;

  @Mock private TerritoryData territoryMock;
  @Mock private FilledLang filledLangMock;

  @BeforeEach
  void setUp() {
    when(territoryMock.getNumberOfClaimedChunk()).thenReturn(10);
    upkeepLine = new ChunkUpkeepLine(territoryMock);
  }

  @Test
  @DisplayName("ChunkUpkeepLine should calculate total upkeep for multiple chunks")
  void testUpkeepCalculation() {
    when(territoryMock.getNumberOfClaimedChunk()).thenReturn(5);
    ChunkUpkeepLine line = new ChunkUpkeepLine(territoryMock);

    // Upkeep should be negative (cost) and proportional to chunk count
    double money = line.getMoney();
    assertTrue(money < 0, "Upkeep should be negative (it's a cost)");
  }

  @Test
  @DisplayName("ChunkUpkeepLine with zero chunks should have zero upkeep")
  void testZeroChunksUpkeep() {
    when(territoryMock.getNumberOfClaimedChunk()).thenReturn(0);
    ChunkUpkeepLine line = new ChunkUpkeepLine(territoryMock);

    assertEquals(0.0, line.getMoney());
  }

  @Test
  @DisplayName("ChunkUpkeepLine should scale with chunk count")
  void testUpkeepScaling() {
    when(territoryMock.getNumberOfClaimedChunk()).thenReturn(10);
    ChunkUpkeepLine line10 = new ChunkUpkeepLine(territoryMock);
    double upkeep10 = line10.getMoney();

    when(territoryMock.getNumberOfClaimedChunk()).thenReturn(20);
    ChunkUpkeepLine line20 = new ChunkUpkeepLine(territoryMock);
    double upkeep20 = line20.getMoney();

    // Upkeep with 20 chunks should be approximately 2x the upkeep with 10 chunks
    assertEquals(upkeep20, upkeep10 * 2.0, 0.001);
  }

  @Test
  @DisplayName("ChunkUpkeepLine should return FilledLang")
  void testGetLine() {
    FilledLang line = upkeepLine.getLine();
    assertNotNull(line);
  }

  @Test
  @DisplayName("ChunkUpkeepLine should be marked as recurrent")
  void testIsRecurrent() {
    assertTrue(upkeepLine.isRecurrent(), "ChunkUpkeepLine should be recurrent");
  }

  @Test
  @DisplayName("ChunkUpkeepLine should handle large chunk counts")
  void testLargeChunkCount() {
    when(territoryMock.getNumberOfClaimedChunk()).thenReturn(1000);
    ChunkUpkeepLine line = new ChunkUpkeepLine(territoryMock);

    double money = line.getMoney();
    assertTrue(money < 0);
    assertTrue(Math.abs(money) > 0);
  }

  @Test
  @DisplayName("ChunkUpkeepLine should maintain territory reference")
  void testTerritoryReference() {
    assertNotNull(upkeepLine);
    // Territory mock should be used for calculations
    verify(territoryMock, atLeastOnce()).getNumberOfClaimedChunk();
  }

  @Test
  @DisplayName("ChunkUpkeepLine should have consistent money calculation")
  void testConsistentCalculation() {
    double money1 = upkeepLine.getMoney();
    double money2 = upkeepLine.getMoney();

    assertEquals(money1, money2, "Money calculation should be consistent");
  }

  @Test
  @DisplayName("ChunkUpkeepLine with single chunk should calculate correctly")
  void testSingleChunkUpkeep() {
    when(territoryMock.getNumberOfClaimedChunk()).thenReturn(1);
    ChunkUpkeepLine line = new ChunkUpkeepLine(territoryMock);

    double money = line.getMoney();
    assertTrue(money < 0);
  }

  @Test
  @DisplayName("ChunkUpkeepLine should handle negative scaling consistently")
  void testNegativeScalingConsistency() {
    when(territoryMock.getNumberOfClaimedChunk()).thenReturn(100);
    ChunkUpkeepLine line100 = new ChunkUpkeepLine(territoryMock);

    when(territoryMock.getNumberOfClaimedChunk()).thenReturn(50);
    ChunkUpkeepLine line50 = new ChunkUpkeepLine(territoryMock);

    double upkeep100 = line100.getMoney();
    double upkeep50 = line50.getMoney();

    // Both should be negative
    assertTrue(upkeep100 < 0);
    assertTrue(upkeep50 < 0);

    // 100 chunks should have approximately 2x the cost of 50 chunks
    assertEquals(upkeep100, upkeep50 * 2.0, 0.001);
  }

  @Test
  @DisplayName("ChunkUpkeepLine money should always be non-positive")
  void testNonPositiveMoney() {
    when(territoryMock.getNumberOfClaimedChunk()).thenReturn(15);
    ChunkUpkeepLine line = new ChunkUpkeepLine(territoryMock);

    assertTrue(line.getMoney() <= 0, "Upkeep should never be positive");
  }

  @Test
  @DisplayName("ChunkUpkeepLine should handle zero and positive chunk boundaries")
  void testChunkBoundaries() {
    // Test boundary at 0
    when(territoryMock.getNumberOfClaimedChunk()).thenReturn(0);
    ChunkUpkeepLine line0 = new ChunkUpkeepLine(territoryMock);
    assertEquals(0.0, line0.getMoney());

    // Test boundary at 1
    when(territoryMock.getNumberOfClaimedChunk()).thenReturn(1);
    ChunkUpkeepLine line1 = new ChunkUpkeepLine(territoryMock);
    assertTrue(line1.getMoney() < 0);
  }

  @Test
  @DisplayName("ChunkUpkeepLine should track all updates correctly")
  void testMultipleUpdates() {
    when(territoryMock.getNumberOfClaimedChunk()).thenReturn(5);
    ChunkUpkeepLine line = new ChunkUpkeepLine(territoryMock);
    double upkeep5 = line.getMoney();

    // Create new line with different chunk count
    when(territoryMock.getNumberOfClaimedChunk()).thenReturn(10);
    ChunkUpkeepLine line2 = new ChunkUpkeepLine(territoryMock);
    double upkeep10 = line2.getMoney();

    assertTrue(
        upkeep5 > upkeep10, "5 chunks should have higher upkeep than 10 chunks (less negative)");
  }
}
