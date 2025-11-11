package org.leralix.tan.utils.text;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Unit tests for DateUtil. */
class DateUtilTest {

  // ==================== Basic Functionality Tests ====================

  @Test
  void getDateStringFromTicks_zeroTicks_returnsZeroTime() {
    String result = DateUtil.getDateStringFromTicks(0);
    assertEquals("0h00m", result);
  }

  @Test
  void getDateStringFromTicks_oneMinute_returnsOneMinute() {
    String result = DateUtil.getDateStringFromTicks(1);
    assertEquals("0h01m", result);
  }

  @Test
  void getDateStringFromTicks_fiftyNineMinutes_returnsCorrectFormat() {
    String result = DateUtil.getDateStringFromTicks(59);
    assertEquals("0h59m", result);
  }

  @Test
  void getDateStringFromTicks_sixtyMinutes_returnsOneHour() {
    String result = DateUtil.getDateStringFromTicks(60);
    assertEquals("1h00m", result);
  }

  @Test
  void getDateStringFromTicks_oneHourThirtyMinutes_returnsCorrectFormat() {
    String result = DateUtil.getDateStringFromTicks(90);
    assertEquals("1h30m", result);
  }

  @Test
  void getDateStringFromTicks_twoHours_returnsCorrectFormat() {
    String result = DateUtil.getDateStringFromTicks(120);
    assertEquals("2h00m", result);
  }

  // ==================== Edge Cases ====================

  @Test
  void getDateStringFromTicks_largeNumber_returnsCorrectFormat() {
    String result = DateUtil.getDateStringFromTicks(1000);
    assertEquals("16h40m", result);
  }

  @Test
  void getDateStringFromTicks_veryLargeNumber_returnsCorrectFormat() {
    String result = DateUtil.getDateStringFromTicks(10000);
    assertEquals("166h40m", result);
  }

  @Test
  void getDateStringFromTicks_twentyFourHours_returnsCorrectFormat() {
    String result = DateUtil.getDateStringFromTicks(1440); // 24 hours
    assertEquals("24h00m", result);
  }

  @Test
  void getDateStringFromTicks_oneWeek_returnsCorrectFormat() {
    String result = DateUtil.getDateStringFromTicks(10080); // 7 days * 24 hours * 60 minutes
    assertEquals("168h00m", result);
  }

  // ==================== Formatting Tests ====================

  @Test
  void getDateStringFromTicks_singleDigitMinutes_usesTwoDigitFormat() {
    String result = DateUtil.getDateStringFromTicks(5);
    assertEquals("0h05m", result);
  }

  @Test
  void getDateStringFromTicks_tenMinutes_usesTwoDigitFormat() {
    String result = DateUtil.getDateStringFromTicks(10);
    assertEquals("0h10m", result);
  }

  @Test
  void getDateStringFromTicks_elevenMinutes_usesCorrectFormat() {
    String result = DateUtil.getDateStringFromTicks(11);
    assertEquals("0h11m", result);
  }

  @Test
  void getDateStringFromTicks_thirtyMinutes_usesCorrectFormat() {
    String result = DateUtil.getDateStringFromTicks(30);
    assertEquals("0h30m", result);
  }

  @Test
  void getDateStringFromTicks_fortyFiveMinutes_usesCorrectFormat() {
    String result = DateUtil.getDateStringFromTicks(45);
    assertEquals("0h45m", result);
  }

  // ==================== Boundary Tests ====================

  @Test
  void getDateStringFromTicks_maxInteger_handlesCorrectly() {
    // Should not throw arithmetic exception
    assertDoesNotThrow(() -> DateUtil.getDateStringFromTicks(Integer.MAX_VALUE));
  }

  @Test
  void getDateStringFromTicks_negativeValue_handlesCorrectly() {
    String result = DateUtil.getDateStringFromTicks(-60);
    // Negative values should be handled (implementation dependent)
    assertNotNull(result);
  }
}
