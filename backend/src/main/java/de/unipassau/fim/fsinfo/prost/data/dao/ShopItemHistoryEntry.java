package de.unipassau.fim.fsinfo.prost.data.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(name = "PRoST_ShopItemHistoryEntry")
public class ShopItemHistoryEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String userId;

  @Column(nullable = false)
  private String itemId;

  @Column(nullable = false)
  private int amount = 1;

  @Column(nullable = false)
  private BigDecimal itemPrice;

  @Column(nullable = false)
  private Long timestamp;

  @OneToOne
  private TransactionEntry transaction;

  public ShopItemHistoryEntry(TransactionEntry transaction, String itemId,
      BigDecimal itemPrice, int amount) {
    this.userId = transaction.getReceiverId();
    this.itemId = itemId;
    this.itemPrice = itemPrice;
    this.amount = amount;
    this.timestamp = Instant.now().toEpochMilli();
  }
}
