package de.unipassau.fim.fsinfo.prost.data.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(name = "PRoST_InvoiceHistoryEntry")
public class InvoiceEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String userId;

  @Column(nullable = false)
  private BigDecimal balance;

  @Column(nullable = false)
  private Long timestamp;

  @Column(nullable = false)
  private Long previousInvoiceTimestamp;

  private boolean mailed = false;
  private boolean published = false;

  public InvoiceEntry(String userId, BigDecimal balance, Long currentTimestamp,
      Long previousInvoiceTimestamp) {
    this.userId = userId;
    this.balance = balance;
    this.timestamp = currentTimestamp;
    this.previousInvoiceTimestamp = previousInvoiceTimestamp;
  }

}
