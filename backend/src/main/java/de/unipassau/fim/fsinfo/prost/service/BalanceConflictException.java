package de.unipassau.fim.fsinfo.prost.service;

import java.math.BigDecimal;
import lombok.Getter;

/**
 * Thrown when a balance change was based on an outdated balance (e.g. a purchase happened while
 * the admin had the "set balance" dialog open). Nothing is written in that case.
 */
@Getter
public class BalanceConflictException extends RuntimeException {

  private final BigDecimal currentBalance;

  public BalanceConflictException(BigDecimal expected, BigDecimal currentBalance) {
    super("expected balance " + expected + " but was " + currentBalance);
    this.currentBalance = currentBalance;
  }
}
