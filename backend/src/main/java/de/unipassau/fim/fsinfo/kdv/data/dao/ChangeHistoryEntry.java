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
@Entity(name = "KDV_ChangeHistoryEntry")
public class ChangeHistoryEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private ChangeType type;

  @Column(nullable = false)
  private String value;

  @Column(nullable = false)
  private Long timestamp;

  @Column(nullable = false)
  private boolean completed = false;

  public ChangeHistoryEntry(ChangeType type, String value) {
    this.type = type;
    this.value = value;
    this.timestamp = Instant.now().toEpochMilli();
  }

  public enum ChangeType {
    TRANSACTION_ADD,
    INVOICE_CREATE, INVOICE_DELETE, INVOICE_MAIL,
    USER_CREATE, USER_DELETE, USER_ENABLE, USER_EMAIL, USER_NAME,
    ITEM_REFUND, SHOP_CONSUME,
    SHOP_CATEGORY, SHOP_DELETE, SHOP_DISABLE, SHOP_NAME, SHOP_ENABLE, SHOP_PICTURE, SHOP_PRICE
  }

}
