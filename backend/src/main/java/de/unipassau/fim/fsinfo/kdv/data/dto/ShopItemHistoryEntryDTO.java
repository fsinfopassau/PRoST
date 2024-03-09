package de.unipassau.fim.fsinfo.kdv.data.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ShopItemHistoryEntryDTO {

  private Long id;
  private String userId;
  private String userDisplayName;
  private String itemId;
  private String itemDisplayName;
  private BigDecimal price;
  private Long timestamp;

}
