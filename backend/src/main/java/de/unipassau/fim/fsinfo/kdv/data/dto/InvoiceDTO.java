package de.unipassau.fim.fsinfo.kdv.data.dto;

import de.unipassau.fim.fsinfo.kdv.data.dao.InvoiceEntry;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;


@Data
public class InvoiceDTO {

  private Long id;
  private String userId;
  private BigDecimal balance;
  private Long timestamp;
  private Long previousInvoiceTimestamp;
  private boolean isMailed;
  private boolean isPublic;
  private List<AmountMapping> amounts;

  public InvoiceDTO(InvoiceEntry entry, Map<String, Integer> amounts) {
    id = entry.getId();
    userId = entry.getUserId();
    balance = entry.getBalance();
    timestamp = entry.getTimestamp();
    previousInvoiceTimestamp = entry.getPreviousInvoiceTimestamp();
    isMailed = entry.isMailed();
    isPublic = entry.isPublic();

    List<AmountMapping> amountsList = new ArrayList<>();
    for (String k : amounts.keySet()) {
      amountsList.add(new AmountMapping(k, amounts.get(k)));
    }
    this.amounts = amountsList;
  }

  public record AmountMapping(String itemId, int amount) {
    // Needed for JSON-List
  }

}
