package de.unipassau.fim.fsinfo.kdv.data.dao;

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
@Entity(name = "KDV_ShopHistoryEntry")
public class ShopHistoryEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String userId;

  @Column(nullable = false)
  private String itemId;

  @Column(nullable = false)
  private Double price;

  @Column(nullable = false)
  private Long timestamp;

  public ShopHistoryEntry(String userId, String itemId, Double price) {
    this.userId = userId;
    this.itemId = itemId;
    this.price = price;
    this.timestamp = Instant.now().getEpochSecond();
  }
}
