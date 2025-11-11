package org.leralix.tan.dataclass.territory.economy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.FilledLang;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("SalaryPaymentLine Tests")
class SalaryPaymentLineTest {

  private SalaryPaymentLine salaryLine;

  @Mock private TerritoryData territoryMock;
  @Mock private RankData rankMock1;
  @Mock private RankData rankMock2;
  @Mock private RankData rankMock3;

  @BeforeEach
  void setUp() {
    when(rankMock1.getSalary()).thenReturn(100);
    when(rankMock1.getPlayersID()).thenReturn(Arrays.asList("player1", "player2"));

    when(rankMock2.getSalary()).thenReturn(50);
    when(rankMock2.getPlayersID()).thenReturn(Arrays.asList("player3", "player4", "player5"));

    when(rankMock3.getSalary()).thenReturn(0);
    when(rankMock3.getPlayersID()).thenReturn(Collections.emptyList());

    when(territoryMock.getAllRanks()).thenReturn(Arrays.asList(rankMock1, rankMock2, rankMock3));

    salaryLine = new SalaryPaymentLine(territoryMock);
  }

  @Test
  @DisplayName("SalaryPaymentLine should calculate total salaries correctly")
  void testSalaryCalculation() {
    // rankMock1: 100 * 2 = 200
    // rankMock2: 50 * 3 = 150
    // rankMock3: 0 * 0 = 0
    // Total = -(200 + 150) = -350
    assertEquals(-350.0, salaryLine.getMoney());
  }

  @Test
  @DisplayName("SalaryPaymentLine with no ranks should have zero cost")
  void testNoRanksSalary() {
    when(territoryMock.getAllRanks()).thenReturn(Collections.emptyList());
    SalaryPaymentLine line = new SalaryPaymentLine(territoryMock);

    assertEquals(0.0, line.getMoney());
  }

  @Test
  @DisplayName("SalaryPaymentLine with no players should have zero cost")
  void testNoPlayersSalary() {
    when(rankMock1.getPlayersID()).thenReturn(Collections.emptyList());
    when(rankMock2.getPlayersID()).thenReturn(Collections.emptyList());
    when(rankMock3.getPlayersID()).thenReturn(Collections.emptyList());

    SalaryPaymentLine line = new SalaryPaymentLine(territoryMock);
    assertEquals(0.0, line.getMoney());
  }

  @Test
  @DisplayName("SalaryPaymentLine should be marked as recurrent")
  void testIsRecurrent() {
    assertTrue(salaryLine.isRecurrent(), "SalaryPaymentLine should be recurrent");
  }

  @Test
  @DisplayName("SalaryPaymentLine should return FilledLang")
  void testGetLine() {
    FilledLang line = salaryLine.getLine();
    assertNotNull(line);
  }

  @Test
  @DisplayName("SalaryPaymentLine should handle single rank with multiple players")
  void testSingleRankMultiplePlayers() {
    when(territoryMock.getAllRanks()).thenReturn(Collections.singletonList(rankMock1));

    SalaryPaymentLine line = new SalaryPaymentLine(territoryMock);
    // rankMock1: 100 * 2 = -200
    assertEquals(-200.0, line.getMoney());
  }

  @Test
  @DisplayName("SalaryPaymentLine should handle high salary values")
  void testHighSalaryValues() {
    when(rankMock1.getSalary()).thenReturn(10000);
    when(rankMock1.getPlayersID()).thenReturn(Arrays.asList("p1", "p2", "p3"));

    when(territoryMock.getAllRanks()).thenReturn(Collections.singletonList(rankMock1));

    SalaryPaymentLine line = new SalaryPaymentLine(territoryMock);
    assertEquals(-30000.0, line.getMoney());
  }

  @Test
  @DisplayName("SalaryPaymentLine with precision salaries")
  void testPrecisionSalaries() {
    when(rankMock1.getSalary()).thenReturn(50);
    when(rankMock1.getPlayersID()).thenReturn(Arrays.asList("p1", "p2"));

    when(rankMock2.getSalary()).thenReturn(25);
    when(rankMock2.getPlayersID()).thenReturn(Collections.singletonList("p3"));

    when(territoryMock.getAllRanks()).thenReturn(Arrays.asList(rankMock1, rankMock2));

    SalaryPaymentLine line = new SalaryPaymentLine(territoryMock);
    // rankMock1: 50 * 2 = 100
    // rankMock2: 25 * 1 = 25
    // Total = -(100 + 25) = -125
    assertEquals(-125.0, line.getMoney(), 0.01);
  }

  @Test
  @DisplayName("SalaryPaymentLine should scale with player count")
  void testScalingWithPlayerCount() {
    when(rankMock1.getSalary()).thenReturn(100);
    when(rankMock1.getPlayersID())
        .thenReturn(Arrays.asList("p1", "p2", "p3", "p4", "p5", "p6", "p7", "p8", "p9", "p10"));
    when(rankMock2.getSalary()).thenReturn(0);
    when(rankMock2.getPlayersID()).thenReturn(Collections.emptyList());

    when(territoryMock.getAllRanks()).thenReturn(Arrays.asList(rankMock1, rankMock2));

    SalaryPaymentLine line = new SalaryPaymentLine(territoryMock);
    assertEquals(-1000.0, line.getMoney());
  }

  @Test
  @DisplayName("SalaryPaymentLine money should always be non-positive")
  void testNonPositiveMoney() {
    assertTrue(salaryLine.getMoney() <= 0, "Salary costs should never be positive");
  }

  @Test
  @DisplayName("SalaryPaymentLine should handle fractional costs")
  void testFractionalCosts() {
    when(rankMock1.getSalary()).thenReturn(33);
    when(rankMock1.getPlayersID()).thenReturn(Arrays.asList("p1", "p2", "p3"));
    when(rankMock2.getSalary()).thenReturn(0);
    when(rankMock2.getPlayersID()).thenReturn(Collections.emptyList());

    when(territoryMock.getAllRanks()).thenReturn(Collections.singletonList(rankMock1));

    SalaryPaymentLine line = new SalaryPaymentLine(territoryMock);
    // 33 * 3 = 99
    assertEquals(-99.0, line.getMoney(), 0.01);
  }

  @Test
  @DisplayName("SalaryPaymentLine with many ranks should accumulate correctly")
  void testManyRanksAccumulation() {
    List<RankData> ranks = new ArrayList<>();

    for (int i = 1; i <= 10; i++) {
      RankData rank = mock(RankData.class);
      when(rank.getSalary()).thenReturn(i * 10);
      when(rank.getPlayersID()).thenReturn(Collections.nCopies(i, "player"));
      ranks.add(rank);
    }

    when(territoryMock.getAllRanks()).thenReturn(ranks);

    SalaryPaymentLine line = new SalaryPaymentLine(territoryMock);

    // Rank 1: -10 * 1 = -10
    // Rank 2: -20 * 2 = -40
    // ...
    // Rank 10: -100 * 10 = -1000
    // Total = -(10 + 40 + 90 + 160 + 250 + 360 + 490 + 640 + 810 + 1000) = -3850
    assertEquals(-3850.0, line.getMoney());
  }

  @Test
  @DisplayName("SalaryPaymentLine should calculate consistently")
  void testConsistentCalculation() {
    double money1 = salaryLine.getMoney();
    double money2 = salaryLine.getMoney();

    assertEquals(money1, money2, "Salary calculation should be consistent");
  }

  @Test
  @DisplayName("SalaryPaymentLine should handle zero salary ranks")
  void testZeroSalaryRanks() {
    when(rankMock1.getSalary()).thenReturn(0);
    when(rankMock1.getPlayersID()).thenReturn(Arrays.asList("p1", "p2", "p3"));

    when(territoryMock.getAllRanks()).thenReturn(Collections.singletonList(rankMock1));

    SalaryPaymentLine line = new SalaryPaymentLine(territoryMock);
    assertEquals(0.0, line.getMoney());
  }

  @Test
  @DisplayName("SalaryPaymentLine should handle empty player lists in ranks")
  void testEmptyPlayerLists() {
    when(rankMock1.getPlayersID()).thenReturn(Collections.emptyList());
    when(rankMock2.getPlayersID()).thenReturn(Collections.emptyList());

    when(territoryMock.getAllRanks()).thenReturn(Arrays.asList(rankMock1, rankMock2));

    SalaryPaymentLine line = new SalaryPaymentLine(territoryMock);
    assertEquals(0.0, line.getMoney());
  }

  @Test
  @DisplayName("SalaryPaymentLine should reference all ranks")
  void testAllRanksUsed() {
    salaryLine.getMoney();

    // Verify that getAllRanks was called
    verify(territoryMock, atLeastOnce()).getAllRanks();
  }
}
