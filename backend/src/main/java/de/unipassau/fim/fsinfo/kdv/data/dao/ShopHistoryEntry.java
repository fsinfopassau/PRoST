package de.unipassau.fim.fsinfo.kdv.data.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;

@Entity(name = "KDV_ShopHistoryEntry")
public class ShopHistoryEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private String itemId;

  @Column(nullable = false)
  private Double price;

  @Column(nullable = false)
  private Long timestamp;

  public ShopHistoryEntry(String username, String itemId, Double price) {
    this.username = username;
    this.itemId = itemId;
    this.price = price;
    this.timestamp = Instant.now().getEpochSecond();
  }

  /**
   * Do not use to create new entries. Unless Timestamp is added manually
   */
  @Deprecated
  public ShopHistoryEntry() {
  }

  public String getUserName() {
    return username;
  }

  public void setUserName(String username) {
    this.username = username;
  }

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public Long getId() {
    return id;
  }
}
