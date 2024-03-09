package de.unipassau.fim.fsinfo.kdv.data.dto;

import java.math.BigDecimal;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class InvoiceDTO {

  private String userId;
  private BigDecimal balance;
  private Map<String, Integer> amounts;

}
