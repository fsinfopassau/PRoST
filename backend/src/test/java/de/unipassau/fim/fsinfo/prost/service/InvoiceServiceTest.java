package de.unipassau.fim.fsinfo.prost.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.unipassau.fim.fsinfo.prost.data.TransactionType;
import de.unipassau.fim.fsinfo.prost.data.dao.InvoiceEntry;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.prost.data.dao.TransactionEntry;
import de.unipassau.fim.fsinfo.prost.data.dto.InvoiceAmountMapping;
import de.unipassau.fim.fsinfo.prost.data.dto.InvoiceDTO;
import de.unipassau.fim.fsinfo.prost.data.repositories.InvoiceRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class InvoiceServiceTest {

  @InjectMocks
  private InvoiceService invoiceService;

  @Mock
  private ShopItemHistoryRepository shopHistory;

  @Mock
  private InvoiceRepository invoiceRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private MailService mailService;

  private Pageable pageable;
  private InvoiceEntry invoiceEntry;
  private InvoiceDTO invoiceDTO;
  private ProstUser prostUser;
  private List<ShopItemHistoryEntry> shopEntries;
  private List<InvoiceAmountMapping> mappings;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    pageable = PageRequest.of(0, 10, Sort.by("timestamp").descending());

    prostUser = new ProstUser("testUser", "Test User", "testmail@test.com", true);
    prostUser.setBalance(new BigDecimal("100.00"));
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.of(prostUser));

    invoiceEntry = new InvoiceEntry(prostUser.getId(), new BigDecimal("100.00"), 1625151600L, 0L);
    invoiceEntry.setId(40L);
    invoiceDTO = new InvoiceDTO(invoiceEntry, prostUser.getDisplayName(), Collections.emptyList());

    shopEntries = List.of(
        new ShopItemHistoryEntry(new TransactionEntry(null, prostUser.getId(), prostUser.getId(),
            TransactionType.BUY, null, BigDecimal.valueOf(23.13d)), "item",
            BigDecimal.valueOf(23.13d), 1),
        new ShopItemHistoryEntry(new TransactionEntry(null, prostUser.getId(), prostUser.getId(),
            TransactionType.BUY, null, BigDecimal.valueOf(1.23d)), "item2",
            BigDecimal.valueOf(1.23d), 2)
    );

    mappings = List.of(
        new InvoiceAmountMapping("item2", BigDecimal.valueOf(1.23d), 2),
        new InvoiceAmountMapping("item", BigDecimal.valueOf(23.13d), 1)
    );
  }

  @Test
  void testGetInvoices() {
    when(invoiceRepository.findAll(pageable)).thenReturn(
        new PageImpl<>(List.of(invoiceEntry), pageable, 1));

    Page<InvoiceDTO> result = invoiceService.getInvoices(0, 10, null, null);

    assertEquals(1, result.getTotalElements());
    assertEquals(invoiceDTO, result.getContent().get(0));
  }

  @Test
  public void testGetInvoices_FilterByUserId() {
    when(invoiceRepository.findByUserId("testUser", pageable)).thenReturn(
        new PageImpl<>(List.of(invoiceEntry), pageable, 1));

    Page<InvoiceDTO> result = invoiceService.getInvoices(0, 10, null, "testUser");

    assertEquals(1, result.getTotalElements());
    assertEquals(invoiceDTO, result.getContent().get(0));
  }

  @Test
  public void testGetInvoices_FilterByMailed() {
    when(invoiceRepository.findByMailedTrue(pageable)).thenReturn(
        new PageImpl<>(List.of(invoiceEntry), pageable, 1));

    Page<InvoiceDTO> result = invoiceService.getInvoices(0, 10, true, null);

    assertEquals(1, result.getTotalElements());
    assertEquals(invoiceDTO, result.getContent().get(0));
  }

  @Test
  public void testGetInvoices_FilterByUserIdAndMailed() {
    when(invoiceRepository.findByUserIdAndMailedTrue("testUser", pageable)).thenReturn(
        new PageImpl<>(List.of(invoiceEntry), pageable, 1));

    Page<InvoiceDTO> result = invoiceService.getInvoices(0, 10, true, "testUser");

    assertEquals(1, result.getTotalElements());
    assertEquals(invoiceDTO, result.getContent().get(0));
  }

  @Test
  public void testGetInvoices_EmptyResults() {
    when(invoiceRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

    Page<InvoiceDTO> result = invoiceService.getInvoices(0, 10, null, null);

    assertEquals(0, result.getTotalElements());
  }

  @Test
  public void testMailInvoices_NullList_ReturnsEmpty() {
    Optional<List<Long>> result = invoiceService.mailInvoices(null);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testMailInvoices_EmptyList_ReturnsEmpty() {
    Optional<List<Long>> result = invoiceService.mailInvoices(Collections.emptyList());
    assertTrue(result.isEmpty());
  }

  @Test
  public void testMailInvoices_ValidList_SuccessfulSend() {
    when(invoiceRepository.findById(invoiceEntry.getId())).thenReturn(Optional.of(invoiceEntry));
    when(mailService.sendInvoice(invoiceEntry, mappings)).thenReturn(true);
    when(
        shopHistory.findByUserIdAndTimestampBetween(prostUser.getId(), 0L, 1625151600L)).thenReturn(
        shopEntries);

    Optional<List<Long>> result = invoiceService.mailInvoices(
        Collections.singletonList(invoiceEntry.getId()));

    assertTrue(result.isPresent());
    assertEquals(1, result.get().size());
    assertEquals(invoiceEntry.getId(), result.get().get(0));
  }

  @Test
  public void testMailInvoices_ValidList_UnsuccessfulSend() {
    when(invoiceRepository.findById(invoiceEntry.getId())).thenReturn(Optional.of(invoiceEntry));
    when(
        shopHistory.findByUserIdAndTimestampBetween(prostUser.getId(), 0L, 1625151600L)).thenReturn(
        shopEntries);
    when(mailService.sendInvoice(invoiceEntry, mappings)).thenReturn(false);
    Optional<List<Long>> result = invoiceService.mailInvoices(
        Collections.singletonList(invoiceEntry.getId()));

    assertTrue(result.isPresent());
    assertEquals(0, result.get().size());
  }

  @Test
  public void testCreateInvoices_NullList_ReturnsEmpty() {
    Optional<List<String>> result = invoiceService.createInvoices(null);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testCreateInvoices_EmptyList_ReturnsEmpty() {
    Optional<List<String>> result = invoiceService.createInvoices(Collections.emptyList());
    assertTrue(result.isEmpty());
  }

  @Test
  public void testCreateInvoices_ValidList_SuccessfulCreation() {
    when(userRepository.findById(prostUser.getId())).thenReturn(Optional.of(prostUser));
    when(invoiceRepository.save(any(InvoiceEntry.class))).thenReturn(invoiceEntry);

    Optional<List<String>> result = invoiceService.createInvoices(
        Collections.singletonList(prostUser.getId()));

    assertTrue(result.isPresent());
    assertEquals(1, result.get().size());
    assertEquals(prostUser.getId(), result.get().get(0));
  }

  @Test
  public void testDelete_NullList_ReturnsEmpty() {
    Optional<List<Long>> result = invoiceService.delete(null);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testDelete_EmptyList_ReturnsEmpty() {
    Optional<List<Long>> result = invoiceService.delete(Collections.emptyList());
    assertTrue(result.isEmpty());
  }

  @Test
  public void testDelete_ValidList_SuccessfulDeletion() {
    invoiceEntry.setMailed(false);
    when(invoiceRepository.findById(invoiceEntry.getId())).thenReturn(Optional.of(invoiceEntry));

    Optional<List<Long>> result = invoiceService.delete(
        Collections.singletonList(invoiceEntry.getId()));

    assertTrue(result.isPresent());
    assertEquals(1, result.get().size());
    assertEquals(invoiceEntry.getId(), result.get().get(0));
  }

}
