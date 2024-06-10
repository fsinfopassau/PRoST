package de.unipassau.fim.fsinfo.prost.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class DataFilterTest {

  @Test
  public void testIsValidString_ValidString() {
    assertTrue(DataFilter.isValidString("validString", "testName"));
  }

  @Test
  public void testIsValidString_NullString() {
    assertFalse(DataFilter.isValidString(null, "testName"));
  }

  @Test
  public void testIsValidString_EmptyString() {
    assertFalse(DataFilter.isValidString("", "testName"));
  }

  @Test
  public void testIsValidString_BlankString() {
    assertFalse(DataFilter.isValidString("   ", "testName"));
  }

  @Test
  public void testIsValidString_TooLongString() {
    String longString = "thisStringIsWayTooLongToBeValidAndExceedsTheMaximumLength";
    assertFalse(DataFilter.isValidString(longString, "testName"));
  }

  @Test
  public void testFilterNameId_ValidId() {
    assertEquals("validid123", DataFilter.filterNameId("ValidId123"));
  }

  @Test
  public void testFilterNameId_IdWithSpecialChars() {
    assertEquals("validid123", DataFilter.filterNameId("ValidId!@#123"));
  }

  @Test
  public void testFilterNameId_UppercaseId() {
    assertEquals("validid", DataFilter.filterNameId("VALIDID"));
  }

  @Test
  public void testFilterNameId_EmptyId() {
    assertEquals("", DataFilter.filterNameId(""));
  }

  @Test
  public void testIsValidEmail_ValidEmail() {
    assertTrue(DataFilter.isValidEmail("test.email@example.com"));
  }

  @Test
  public void testIsValidEmail_InvalidEmail_NoAt() {
    assertFalse(DataFilter.isValidEmail("test.email.example.com"));
  }

  @Test
  public void testIsValidEmail_InvalidEmail_NoDomain() {
    assertFalse(DataFilter.isValidEmail("test.email@"));
  }

  @Test
  public void testIsValidEmail_InvalidEmail_Empty() {
    assertFalse(DataFilter.isValidEmail(""));
  }

  @Test
  public void testIsValidEmail_NullEmail() {
    assertFalse(DataFilter.isValidEmail(null));
  }

  @Test
  public void testFormatMoney_NullAmount() {
    assertEquals("0,00 €", DataFilter.formatMoney(null));
  }

  @Test
  public void testFormatMoney_ValidAmount() {
    assertEquals("1.234,56 €", DataFilter.formatMoney(new BigDecimal("1234.56")));
  }

  @Test
  public void testFormatMoney_ZeroAmount() {
    assertEquals("0,00 €", DataFilter.formatMoney(new BigDecimal("0")));
  }

  @Test
  public void testFormatMoney_NegativeAmount() {
    assertEquals("-1.234,56 €", DataFilter.formatMoney(new BigDecimal("-1234.56")));
  }

  @Test
  public void testValidMoney_RightDecimalAmount() {
    assertTrue(DataFilter.isValidMoney(new BigDecimal("-1234.56")));
  }

  @Test
  public void testValidMoney_RightDecimalAmount2() {
    assertTrue(DataFilter.isValidMoney(new BigDecimal("-1234")));
  }

  @Test
  public void testValidMoney_WrongDecimalAmount() {
    assertFalse(DataFilter.isValidMoney(new BigDecimal("-1234.563")));
  }

  @Test
  public void testValidMoney_NullAmount() {
    assertFalse(DataFilter.isValidMoney(null));
  }
}

