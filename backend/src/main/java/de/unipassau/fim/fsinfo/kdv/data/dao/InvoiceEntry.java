package de.unipassau.fim.fsinfo.kdv.data.dao;

import de.unipassau.fim.fsinfo.kdv.data.Invoice;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;
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
  private Double price;

  @Column(nullable = false)
  private Long timestamp;

  public InvoiceEntry(Invoice invoice) {
    this.userId = invoice.getUserId();
    this.price = invoice.getPrice();
    this.timestamp = Instant.now().getEpochSecond();
  }

}
