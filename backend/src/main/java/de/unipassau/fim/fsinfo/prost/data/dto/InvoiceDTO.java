package de.unipassau.fim.fsinfo.prost.data.dto;

import de.unipassau.fim.fsinfo.prost.data.dao.InvoiceEntry;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;


@Data
public class InvoiceDTO {

  private Long id;
  private String userId;
  private String userDisplayName;
  private BigDecimal balance;
  private Long timestamp;
  private Long previousInvoiceTimestamp;
  private boolean isMailed;
  private boolean isPublished;
  private List<InvoiceAmountMapping> amounts;

  public InvoiceDTO(InvoiceEntry entry, String userDisplayName,
      List<InvoiceAmountMapping> amounts) {
    id = entry.getId();
    userId = entry.getUserId();
    this.userDisplayName = userDisplayName;
    balance = entry.getBalance();
    timestamp = entry.getTimestamp();
    previousInvoiceTimestamp = entry.getPreviousInvoiceTimestamp();
    isMailed = entry.isMailed();
    isPublished = entry.isPublished();
    this.amounts = amounts;
  }

}
