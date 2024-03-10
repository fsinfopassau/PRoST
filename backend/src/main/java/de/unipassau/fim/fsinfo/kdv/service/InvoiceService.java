package de.unipassau.fim.fsinfo.kdv.service;

import de.unipassau.fim.fsinfo.kdv.data.dao.InvoiceEntry;
import de.unipassau.fim.fsinfo.kdv.data.dao.KdvUser;
import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.kdv.data.dto.InvoiceDTO;
import de.unipassau.fim.fsinfo.kdv.data.repositories.InvoiceRepository;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopItemHistoryRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {

  @Autowired
  private ShopItemHistoryRepository shopHistory;

  @Autowired
  private InvoiceRepository invoiceHistory;

  @Autowired
  private MailService mail;

  public Optional<List<Long>> mailInvoices(List<Long> invoiceIds) {

    if (invoiceIds == null || invoiceIds.isEmpty()) {
      return Optional.empty();
    }

    List<Long> successfulSends = new ArrayList<>();

    for (Long id : invoiceIds) {

      Optional<InvoiceEntry> invoiceO = invoiceHistory.findById(id);

      if (invoiceO.isEmpty()) {
        continue;
      }

      InvoiceEntry invoice = invoiceO.get();

      List<ShopItemHistoryEntry> shopEntries = shopHistory.findByUserIdAndTimestampBetween(
          invoice.getUserId(), invoice.getPreviousInvoiceTimestamp(), invoice.getTimestamp());

      if (mail.sendInvoice(invoice, getItemAmounts(shopEntries))) {
        successfulSends.add(id);
        invoice.setMailed(true);
        invoiceHistory.save(invoice);
      }
    }

    return Optional.of(successfulSends);
  }

  /**
   * Creates and saves a new Invoice.
   * <br>
   * If the user did not buy anything since the last invoice, no invoice is created.
   *
   * @param user the invoiced user.
   * @return optional
   */
  public Optional<InvoiceDTO> createInvoice(KdvUser user) {

    // Nur fortfahren, wenn Nutzer Schulden hat.
    if (user.getBalance().compareTo(new BigDecimal(0)) != -1) {
      return Optional.empty();
    }

    List<InvoiceEntry> previousInvoices = invoiceHistory.findByUserIdEquals(user.getId());

    Long lastInvoiceTimestamp = 0L; // No previous invoice
    Long currentTimestamp = Instant.now().getEpochSecond();

    if (!previousInvoices.isEmpty()) {
      Optional<InvoiceEntry> entry = getLastEntry(previousInvoices);

      if (entry.isPresent()) {
        lastInvoiceTimestamp = entry.get().getTimestamp();
      }
    }

    List<ShopItemHistoryEntry> shopEntries = shopHistory.findByUserIdAndTimestampBetween(
        user.getId(),
        lastInvoiceTimestamp, currentTimestamp);

    InvoiceEntry invoice = new InvoiceEntry(user.getId(), user.getBalance(), currentTimestamp,
        lastInvoiceTimestamp);

    InvoiceDTO invoiceDTO;
    if (shopEntries.isEmpty()) {
      invoiceDTO = new InvoiceDTO(invoice, new HashMap<>());
    } else {
      invoiceDTO = new InvoiceDTO(invoice, getItemAmounts(shopEntries));
    }

    invoiceHistory.save(invoice); // save in DB to get ID
    invoiceDTO.setId(invoice.getId());

    return Optional.of(invoiceDTO);
  }

  /**
   * @param entrys to search through.
   * @return a map of itemIds and their amounts.
   */
  private Map<String, Integer> getItemAmounts(List<ShopItemHistoryEntry> entrys) {

    Map<String, Integer> amounts = new HashMap<>();

    entrys.forEach((entry) -> {
      if (amounts.containsKey(entry.getItemId())) {
        Integer lastAmount = amounts.get(entry.getItemId());
        amounts.put(entry.getItemId(), lastAmount + 1);
      } else {
        amounts.put(entry.getItemId(), 1);
      }
    });

    return amounts;
  }

  /**
   * @return Entry with the most recent timestamp.
   */
  private Optional<InvoiceEntry> getLastEntry(List<InvoiceEntry> invoices) {
    if (invoices.isEmpty()) {
      return Optional.empty();
    } else if (invoices.size() == 1) {
      return Optional.of(invoices.get(0));
    }

    List<InvoiceEntry> sorted = new ArrayList<>(invoices);
    sorted.sort(Comparator.comparing(InvoiceEntry::getTimestamp));

    try {
      return Optional.of(sorted.get(sorted.size() - 1));
    } catch (IndexOutOfBoundsException maybe) {
      return Optional.empty();
    }
  }

}
