package de.unipassau.fim.fsinfo.prost.service;

import de.unipassau.fim.fsinfo.prost.data.dao.InvoiceEntry;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.prost.data.dto.InvoiceAmountMapping;
import de.unipassau.fim.fsinfo.prost.data.dto.InvoiceDTO;
import de.unipassau.fim.fsinfo.prost.data.repositories.InvoiceRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceService {

  private final ShopItemHistoryRepository shopHistory;
  private final InvoiceRepository invoiceRepository;
  private final UserRepository userRepository;

  private final MailService mail;

  @Autowired
  public InvoiceService(ShopItemHistoryRepository shopHistory, InvoiceRepository invoiceRepository,
      MailService mail, UserRepository userRepository) {
    this.shopHistory = shopHistory;
    this.invoiceRepository = invoiceRepository;
    this.mail = mail;
    this.userRepository = userRepository;
  }

  public Page<InvoiceDTO> getInvoices(int pageNumber, int pageSize, Boolean mailed, String userId) {

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("timestamp").descending());
    Page<InvoiceEntry> entriesPage;

    if (userId != null && mailed != null) {
      if (mailed) {
        entriesPage = invoiceRepository.findByUserIdAndMailedTrue(userId, pageable);
      } else {
        entriesPage = invoiceRepository.findByUserIdAndMailedFalse(userId, pageable);
      }
    } else if (mailed != null) {
      if (mailed) {
        entriesPage = invoiceRepository.findByMailedTrue(pageable);
      } else {
        entriesPage = invoiceRepository.findByMailedFalse(pageable);
      }
    } else if (userId != null) {
      entriesPage = invoiceRepository.findByUserId(userId, pageable);
    } else {
      entriesPage = invoiceRepository.findAll(pageable);
    }

    List<InvoiceDTO> invoiceDTOs = entriesPage.getContent().stream()
        .map(this::getInvoiceDTO)
        .collect(Collectors.toList());

    return new PageImpl<>(invoiceDTOs, pageable, entriesPage.getTotalElements());
  }

  private InvoiceDTO getInvoiceDTO(InvoiceEntry invoice) {
    List<ShopItemHistoryEntry> shopEntries = shopHistory.findByUserIdAndTimestampBetween(
        invoice.getUserId(),
        invoice.getPreviousInvoiceTimestamp(), invoice.getTimestamp());

    Optional<ProstUser> user = userRepository.findById(invoice.getUserId());
    String userName = user.isPresent() ? user.get().getDisplayName() : invoice.getUserId();

    if (shopEntries.isEmpty()) {
      return new InvoiceDTO(invoice, userName,
          new ArrayList<>());
    }
    return new InvoiceDTO(invoice, userName, getItemAmounts(shopEntries));
  }

  @Transactional
  public Optional<List<Long>> mailInvoices(List<Long> invoiceIds) {
    if (invoiceIds == null || invoiceIds.isEmpty()) {
      return Optional.empty();
    }

    List<Long> successfulSends = new ArrayList<>();

    for (Long id : invoiceIds) {

      if (id == null) {
        continue;
      }

      Optional<InvoiceEntry> invoiceO = invoiceRepository.findById(id);

      if (invoiceO.isEmpty()) {
        continue;
      }

      InvoiceEntry invoice = invoiceO.get();
      invoice.setPublished(true);
      invoiceRepository.save(invoice);

      if (invoice.isMailed()) {
        continue;
      }

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

  @Transactional
  public Optional<List<String>> createInvoices(List<String> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return Optional.empty();
    }

    List<String> successful = new ArrayList<>();
    for (String id : userIds) {
      Optional<ProstUser> user0 = userRepository.findById(id);
      if (user0.isEmpty()) {
        System.err.println("[IS] :: No user found with id " + id);
        continue;
      }

      ProstUser user = user0.get();
      if (createInvoice(user).isPresent()) {
        successful.add(id);
      }
    }

    return Optional.of(successful);
  }

  /**
   * Creates and saves a new Invoice.
   * <br>
   * If the user did not buy anything since the last invoice, no invoice is created.
   *
   * @param user the invoiced user.
   * @return optional
   */
  private Optional<InvoiceDTO> createInvoice(ProstUser user) {

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

  @Transactional
  public Optional<List<Long>> delete(List<Long> invoiceIds) {

    if (invoiceIds == null || invoiceIds.isEmpty()) {
      return Optional.empty();
    }

    List<Long> successfulRemovals = new ArrayList<>();

    for (Long id : invoiceIds) {
      if (id == null) {
        continue;
      }

      Optional<InvoiceEntry> invoice = invoiceRepository.findById(id);

      if (invoice.isEmpty()) {
        continue;
      }

      // Cant delete sent Invoices
      if (invoice.get().isMailed()) {
        continue;
      }

      Optional<InvoiceEntry> newer = getNewerEntry(invoice.get());

      if (newer.isPresent()) {
        InvoiceEntry newerEntry = newer.get();
        newerEntry.setPreviousInvoiceTimestamp(invoice.get().getPreviousInvoiceTimestamp());
        invoiceRepository.save(newerEntry);
      }

      invoiceRepository.delete(invoice.get());
      successfulRemovals.add(id);
    }

    return Optional.of(successfulRemovals);
  }

  private Optional<InvoiceEntry> getNewerEntry(InvoiceEntry entry) {
    if (entry == null) {
      return Optional.empty();
    }
    List<InvoiceEntry> entries = invoiceRepository.findByUserIdAndTimestampGreaterThanOrderByTimestampAsc(
        entry.getUserId(),
        entry.getTimestamp());
    if (entries.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(entries.get(0));
  }

  /**
   * @param entries to search through.
   * @return a list of amounts.
   */
  private List<InvoiceAmountMapping> getItemAmounts(List<ShopItemHistoryEntry> entries) {

    if (entries == null || entries.isEmpty()) {
      return List.of();
    }

    Map<CompositeKey, InvoiceAmountMapping> amounts = new HashMap<>();

    entries.forEach((entry) -> {
      if (entry == null) {
        return;
      }

      CompositeKey key = new CompositeKey(entry.getItemId(), entry.getItemPrice());
      if (amounts.containsKey(key)) {
        InvoiceAmountMapping lastAmount = amounts.get(key);
        lastAmount.setAmount(lastAmount.getAmount() + entry.getAmount());
      } else {
        amounts.put(key,
            new InvoiceAmountMapping(entry.getItemId(), entry.getItemPrice(), entry.getAmount()));
      }
    });

    return amounts.values().stream().toList();
  }

  public static class CompositeKey {

    private String itemId;
    private BigDecimal itemPrice;

    public CompositeKey(String itemId, BigDecimal itemPrice) {
      this.itemId = itemId;
      this.itemPrice = itemPrice;
    }

    // Implement equals and hashCode methods
    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      CompositeKey that = (CompositeKey) o;
      return Objects.equals(itemId, that.itemId) &&
          Objects.equals(itemPrice, that.itemPrice);
    }

    @Override
    public int hashCode() {
      return Objects.hash(itemId, itemPrice);
    }
  }


}
