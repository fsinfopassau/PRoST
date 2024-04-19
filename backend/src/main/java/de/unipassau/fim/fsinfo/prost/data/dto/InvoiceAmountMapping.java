package de.unipassau.fim.fsinfo.prost.data.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvoiceAmountMapping {

  private String itemId;
  private BigDecimal singeItemPrice;
  private int amount;
}
