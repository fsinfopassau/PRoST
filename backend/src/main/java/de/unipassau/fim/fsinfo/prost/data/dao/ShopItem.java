package de.unipassau.fim.fsinfo.prost.data.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(name = "PRoST_ShopItem")
public class ShopItem {

  @Id
  private String id;
  private String category;
  @Column(nullable = false)
  private String displayName;
  @Column(nullable = false)
  private BigDecimal price;
  @Column(nullable = false)
  private Boolean enabled;

  public ShopItem(String id, String category, String displayName, BigDecimal price) {
    this.id = id;
    this.category = category;
    this.displayName = displayName;
    this.price = price;
    this.enabled = true;
  }
}
