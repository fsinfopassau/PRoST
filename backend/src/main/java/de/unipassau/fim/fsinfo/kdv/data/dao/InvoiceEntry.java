package de.unipassau.fim.fsinfo.kdv.data.dao;

import de.unipassau.fim.fsinfo.kdv.data.dto.InvoiceDTO;
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
@Entity(name = "KDV_InvoiceHistoryEntry")
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

  public InvoiceEntry(InvoiceDTO invoice, Long previousInvoiceTimestamp, Long currentTimestamp) {
    this.previousInvoiceTimestamp = previousInvoiceTimestamp;
    this.userId = invoice.getUserId();
    this.balance = invoice.getBalance();
    this.timestamp = currentTimestamp;
  }

}
