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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {

  private final ShopItemHistoryRepository shopHistory;

  private final InvoiceRepository invoiceRepository;

  private final MailService mail;

  @Autowired
  public InvoiceService(ShopItemHistoryRepository shopHistory, InvoiceRepository invoiceRepository,
      MailService mail) {
    this.shopHistory = shopHistory;
    this.invoiceRepository = invoiceRepository;
    this.mail = mail;
  }

  public Page<InvoiceDTO> getInvoices(int pageNumber, int pageSize, Boolean mailed,
      String userId) {

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("timestamp").descending());
    Page<InvoiceEntry> entriesPage;

    if (userId != null && mailed != null) {
      System.out.println("1");
      if (mailed) {
        entriesPage = invoiceRepository.findByUserIdAndMailedTrue(userId, pageable);
      } else {
        entriesPage = invoiceRepository.findByUserIdAndMailedFalse(userId, pageable);
      }
    } else if (mailed != null) {
      System.out.println("2");
      if (mailed) {
        entriesPage = invoiceRepository.findByMailedTrue(pageable);
      } else {
        entriesPage = invoiceRepository.findByMailedFalse(pageable);
      }
    } else if (userId != null) {
      System.out.println("3");
      entriesPage = invoiceRepository.findByUserId(userId, pageable);
    } else {
      System.out.println("4");
      entriesPage = invoiceRepository.findAll(pageable);
    }

    List<InvoiceDTO> invoiceDTOs = entriesPage.getContent().stream()
        .map(this::getInvoiceDTO)
        .collect(Collectors.toList());

    return new PageImpl<>(invoiceDTOs, pageable, invoiceDTOs.size());
  }

  private InvoiceDTO getInvoiceDTO(InvoiceEntry invoice) {
    List<ShopItemHistoryEntry> shopEntries = shopHistory.findByUserIdAndTimestampBetween(
        invoice.getUserId(),
        invoice.getPreviousInvoiceTimestamp(), invoice.getTimestamp());

    if (shopEntries.isEmpty()) {
      return new InvoiceDTO(invoice, new HashMap<>());
    }
    return new InvoiceDTO(invoice, getItemAmounts(shopEntries));
  }

  public Optional<List<Long>> mailInvoices(List<Long> invoiceIds) {

    if (invoiceIds == null || invoiceIds.isEmpty()) {
      return Optional.empty();
    }

    List<Long> successfulSends = new ArrayList<>();

    for (Long id : invoiceIds) {

      Optional<InvoiceEntry> invoiceO = invoiceRepository.findById(id);

      if (invoiceO.isEmpty()) {
        continue;
      }

      InvoiceEntry invoice = invoiceO.get();

      List<ShopItemHistoryEntry> shopEntries = shopHistory.findByUserIdAndTimestampBetween(
          invoice.getUserId(), invoice.getPreviousInvoiceTimestamp(), invoice.getTimestamp());

      if (mail.sendInvoice(invoice, getItemAmounts(shopEntries))) {
        successfulSends.add(id);
        invoice.setMailed(true);
        invoiceRepository.save(invoice);
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

    List<InvoiceEntry> previousInvoices = invoiceRepository.findByUserIdEqualsOrderByTimestampDesc(
        user.getId());

    Long lastInvoiceTimestamp = 0L; // No previous invoice
    Long currentTimestamp = Instant.now().toEpochMilli();

    if (!previousInvoices.isEmpty()) {
      lastInvoiceTimestamp = previousInvoices.get(0).getTimestamp();
    }

    InvoiceEntry invoice = new InvoiceEntry(user.getId(), user.getBalance(), currentTimestamp,
        lastInvoiceTimestamp);

    invoiceRepository.save(invoice); // save in DB to get ID

    return Optional.of(getInvoiceDTO(invoice));
  }

  public boolean delete(Long invoiceId) {
    Optional<InvoiceEntry> invoice = invoiceRepository.findById(invoiceId);

    if (invoice.isEmpty()) {
      return false;
    }

    invoiceRepository.delete(invoice.get());
    return true;
  }

  /**
   * @param entries to search through.
   * @return a map of itemIds and their amounts.
   */
  private Map<String, Integer> getItemAmounts(List<ShopItemHistoryEntry> entries) {

    Map<String, Integer> amounts = new HashMap<>();

    entries.forEach((entry) -> {
      if (amounts.containsKey(entry.getItemId())) {
        Integer lastAmount = amounts.get(entry.getItemId());
        amounts.put(entry.getItemId(), lastAmount + 1);
      } else {
        amounts.put(entry.getItemId(), 1);
      }
    });

    return amounts;
  }

}
