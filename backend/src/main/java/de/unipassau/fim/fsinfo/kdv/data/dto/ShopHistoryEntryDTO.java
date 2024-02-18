package de.unipassau.fim.fsinfo.kdv.data.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ShopHistoryEntryDTO {
  private Long id;
  private String userId;
  private String userDisplayName;
  private String itemId;
  private String itemDisplayName;
  private Double price;
  private Long timestamp;

  public ShopHistoryEntryDTO(Long id, String userId, String userDisplayName, String itemId,
      String itemDisplayName, double price, long timestamp) {
    this.id = id;
    this.userId = userId;
    this.itemId = itemId;
    this.price = price;
    this.timestamp = timestamp;
    this.userDisplayName = userDisplayName;
    this.itemDisplayName = itemDisplayName;
  }
}
