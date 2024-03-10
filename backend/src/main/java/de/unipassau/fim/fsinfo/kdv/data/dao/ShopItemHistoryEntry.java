package de.unipassau.fim.fsinfo.kdv.data.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(name = "KDV_ShopItemHistoryEntry")
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
  private BigDecimal price;

  @Column(nullable = false)
  private Long timestamp;

  public ShopItemHistoryEntry(String userId, String itemId, BigDecimal price, int amount) {
    this.userId = userId;
    this.itemId = itemId;
    this.price = price;
    this.amount = amount;
    this.timestamp = Instant.now().toEpochMilli();
  }
}
