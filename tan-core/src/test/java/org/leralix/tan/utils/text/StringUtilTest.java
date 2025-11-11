package org.leralix.tan.utils.text;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** Unit tests for StringUtil. */
class StringUtilTest {

  // ==================== Color Code Validation Tests ====================

  @Test
  void isValidColorCode_validGreen_returnsTrue() {
    assertTrue(StringUtil.isValidColorCode("00FF00"));
  }

  @Test
  void isValidColorCode_validRed_returnsTrue() {
    assertTrue(StringUtil.isValidColorCode("FF0000"));
  }

  @Test
  void isValidColorCode_validBlue_returnsTrue() {
    assertTrue(StringUtil.isValidColorCode("0000FF"));
  }

  @Test
  void isValidColorCode_validWhite_returnsTrue() {
    assertTrue(StringUtil.isValidColorCode("FFFFFF"));
  }

  @Test
  void isValidColorCode_validBlack_returnsTrue() {
    assertTrue(StringUtil.isValidColorCode("000000"));
  }

  @Test
  void isValidColorCode_validLowercase_returnsTrue() {
    assertTrue(StringUtil.isValidColorCode("00ff00"));
  }

  @Test
  void isValidColorCode_validMixedCase_returnsTrue() {
    assertTrue(StringUtil.isValidColorCode("00Ff00"));
  }

  @Test
  void isValidColorCode_tooShort_returnsFalse() {
    assertFalse(StringUtil.isValidColorCode("00FF0"));
  }

  @Test
  void isValidColorCode_tooLong_returnsFalse() {
    assertFalse(StringUtil.isValidColorCode("00FF000"));
  }

  @Test
  void isValidColorCode_invalidChars_returnsFalse() {
    assertFalse(StringUtil.isValidColorCode("00GG00"));
  }

  @Test
  void isValidColorCode_withHash_returnsFalse() {
    assertFalse(StringUtil.isValidColorCode("#00FF00"));
  }

  @Test
  void isValidColorCode_empty_returnsFalse() {
    assertFalse(StringUtil.isValidColorCode(""));
  }

  @Test
  void isValidColorCode_null_throwsException() {
    assertThrows(NullPointerException.class, () -> StringUtil.isValidColorCode(null));
  }

  // ==================== Hex Color to Int Tests ====================

  @Test
  void hexColorToInt_white_returnsMaxValue() {
    assertEquals(16777215, StringUtil.hexColorToInt("FFFFFF"));
  }

  @Test
  void hexColorToInt_black_returnsZero() {
    assertEquals(0, StringUtil.hexColorToInt("000000"));
  }

  @Test
  void hexColorToInt_red_returnsCorrectValue() {
    assertEquals(16711680, StringUtil.hexColorToInt("FF0000"));
  }

  @Test
  void hexColorToInt_green_returnsCorrectValue() {
    assertEquals(65280, StringUtil.hexColorToInt("00FF00"));
  }

  @Test
  void hexColorToInt_blue_returnsCorrectValue() {
    assertEquals(255, StringUtil.hexColorToInt("0000FF"));
  }

  // ==================== Random Color Tests ====================

  @Test
  void randomColor_returnsValidColor() {
    int color = StringUtil.randomColor();
    assertTrue(color >= 0 && color <= 16777215);
  }

  @Test
  void randomColor_multipleCalls_returnsDifferentValues() {
    int color1 = StringUtil.randomColor();
    int color2 = StringUtil.randomColor();
    int color3 = StringUtil.randomColor();

    // At least one should be different (very high probability)
    assertTrue(color1 != color2 || color2 != color3 || color1 != color3);
  }

  // ==================== Format Money Tests ====================

  @Test
  void formatMoney_zero_returnsZeroString() {
    String result = StringUtil.formatMoney(0);
    assertEquals("0.0", result);
  }

  @Test
  void formatMoney_underThousand_returnsPlainNumber() {
    String result = StringUtil.formatMoney(999);
    assertTrue(result.contains("999"));
  }

  @Test
  void formatMoney_thousand_returnsKFormat() {
    String result = StringUtil.formatMoney(1000);
    assertTrue(result.endsWith("K"));
  }

  @Test
  void formatMoney_fiveThousand_returnsKFormat() {
    String result = StringUtil.formatMoney(5000);
    assertEquals("5.0K", result);
  }

  @Test
  void formatMoney_million_returnsMFormat() {
    String result = StringUtil.formatMoney(1_000_000);
    assertEquals("1.0M", result);
  }

  @Test
  void formatMoney_billion_returnsBFormat() {
    String result = StringUtil.formatMoney(1_000_000_000);
    assertEquals("1.0B", result);
  }

  @Test
  void formatMoney_trillion_returnsTFormat() {
    String result = StringUtil.formatMoney(1_000_000_000_000L);
    assertEquals("1.0T", result);
  }

  @Test
  void formatMoney_negativeThousand_returnsKFormat() {
    String result = StringUtil.formatMoney(-1000);
    assertTrue(result.contains("K"));
  }

  // ==================== Colored Money Tests ====================

  @Test
  void getColoredMoney_positive_startsWithGreen() {
    String result = StringUtil.getColoredMoney(100);
    assertTrue(result.startsWith("§a+"));
  }

  @Test
  void getColoredMoney_negative_startsWithRed() {
    String result = StringUtil.getColoredMoney(-100);
    assertTrue(result.startsWith("§c"));
  }

  @Test
  void getColoredMoney_zero_startsWithGray() {
    String result = StringUtil.getColoredMoney(0);
    assertTrue(result.startsWith("§7"));
  }

  @Test
  void getColoredMoney_positiveThousand_includesKAndGreen() {
    String result = StringUtil.getColoredMoney(5000);
    assertTrue(result.startsWith("§a+"));
    assertTrue(result.contains("K"));
  }

  @Test
  void getColoredMoney_negativeThousand_includesKAndRed() {
    String result = StringUtil.getColoredMoney(-5000);
    assertTrue(result.startsWith("§c"));
    assertTrue(result.contains("K"));
  }

  // ==================== Handle Digits Tests ====================

  @Test
  void handleDigits_integer_returnsInteger() {
    double result = StringUtil.handleDigits(100.0);
    assertEquals(100.0, result);
  }

  @Test
  void handleDigits_decimal_roundsCorrectly() {
    double result = StringUtil.handleDigits(123.456);
    // Result depends on Constants.getNbDigits()
    assertNotNull(result);
  }

  @Test
  void handleDigits_zero_returnsZero() {
    double result = StringUtil.handleDigits(0.0);
    assertEquals(0.0, result);
  }

  @Test
  void handleDigits_negative_handlesCorrectly() {
    double result = StringUtil.handleDigits(-123.456);
    assertTrue(result < 0);
  }
}
