package de.unipassau.fim.fsinfo.prost.data.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import de.unipassau.fim.fsinfo.prost.data.dao.TransactionEntry;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ShopItemHistoryEntryDTO {

  private Long id;
  private String userId;
  private String userDisplayName;
  private String itemId;
  private String itemDisplayName;
  private BigDecimal itemPrice;
  private int amount;
  private Long timestamp;
  private TransactionEntry transaction;
  private Boolean hidden;

}
