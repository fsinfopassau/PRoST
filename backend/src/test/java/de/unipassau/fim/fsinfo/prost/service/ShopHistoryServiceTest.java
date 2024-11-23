package de.unipassau.fim.fsinfo.prost.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import de.unipassau.fim.fsinfo.prost.data.TransactionType;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.prost.data.dao.TransactionEntry;
import de.unipassau.fim.fsinfo.prost.data.dto.ShopItemHistoryEntryDTO;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class ShopHistoryServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private ShopItemRepository itemRepository;

  @Mock
  private ShopItemHistoryRepository historyRepository;

  private ShopHistoryService shopHistoryService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    shopHistoryService = new ShopHistoryService(userRepository, itemRepository, historyRepository);
  }

  @Test
  public void testHiddenAnonymizedHistory() throws InterruptedException {
    List<ShopItemHistoryEntry> historyEntries = new ArrayList<>();

    TransactionEntry transactionEntry = new TransactionEntry(null, "user1", "user1",
        TransactionType.BUY, null, BigDecimal.valueOf(10.0));
    TransactionEntry transactionEntry2 = new TransactionEntry(null, "user2", "user2",
        TransactionType.BUY, null, BigDecimal.valueOf(20.0));

    historyEntries.add(
        new ShopItemHistoryEntry(transactionEntry, "item1", new BigDecimal("10.23"), 2));
    Thread.sleep(1);
    historyEntries.add(
        new ShopItemHistoryEntry(transactionEntry2, "item2", new BigDecimal("20.99"), 4));
    Page<ShopItemHistoryEntry> page = new PageImpl<>(historyEntries);

    when(historyRepository.findAll(
        PageRequest.of(0, 10, Sort.by("timestamp").descending()))).thenReturn(page);

    when(userRepository.findById("user1")).thenReturn(
        Optional.of(new ProstUser("user1", "User One", "test@test.com", true, false)));
    when(userRepository.findById("user2")).thenReturn(
        Optional.of(new ProstUser("user2", "User Two", "test@test.com", true, true)));

    when(itemRepository.findById("item1")).thenReturn(
        Optional.of(new ShopItem("item1", "Testitem", "Item One", new BigDecimal("10.23"))));
    when(itemRepository.findById("item2")).thenReturn(
        Optional.of(new ShopItem("item2", "Testitem", "Item Two", new BigDecimal("20.99"))));

    Page<ShopItemHistoryEntryDTO> result = shopHistoryService.getHistory(0, 10, Optional.empty(),
        true);

    assertEquals(2, result.getContent().size());

    ShopItemHistoryEntryDTO first = result.getContent().get(0);
    assertEquals("User One", first.getUserDisplayName());
    assertEquals("user1", first.getUserId());
    assertEquals("Item One", first.getItemDisplayName());
    assertEquals("item1", first.getItemId());
    assertEquals(2, first.getAmount());
    assertEquals(new BigDecimal("10.23"), first.getItemPrice());
    assertNotNull(first.getTimestamp());

    ShopItemHistoryEntryDTO second = result.getContent().get(1);
    assertEquals("Anonyme \uD83C\uDF4D", second.getUserDisplayName());
    assertEquals("", second.getUserId());
    assertEquals("", second.getItemDisplayName());
    assertEquals("", second.getItemId());
    assertEquals(0, second.getAmount());
    assertEquals(BigDecimal.ZERO, second.getItemPrice());
    assertNotNull(second.getTimestamp());

    assertTrue(first.getTimestamp() < second.getTimestamp());
  }

  @Test
  public void testGetHistory() throws InterruptedException {
    List<ShopItemHistoryEntry> historyEntries = new ArrayList<>();

    TransactionEntry transactionEntry = new TransactionEntry(null, "user1", "user1",
        TransactionType.BUY, null, BigDecimal.valueOf(10.0));
    TransactionEntry transactionEntry2 = new TransactionEntry(null, "user2", "user2",
        TransactionType.BUY, null, BigDecimal.valueOf(20.0));

    historyEntries.add(
        new ShopItemHistoryEntry(transactionEntry, "item1", new BigDecimal("10.23"), 2));
    Thread.sleep(1);
    historyEntries.add(
        new ShopItemHistoryEntry(transactionEntry2, "item2", new BigDecimal("20.99"), 4));
    Page<ShopItemHistoryEntry> page = new PageImpl<>(historyEntries);

    when(historyRepository.findAll(
        PageRequest.of(0, 10, Sort.by("timestamp").descending()))).thenReturn(page);

    when(userRepository.findById("user1")).thenReturn(
        Optional.of(new ProstUser("user1", "User One", "test@test.com", true)));
    when(userRepository.findById("user2")).thenReturn(
        Optional.of(new ProstUser("user2", "User Two", "test@test.com", true)));

    when(itemRepository.findById("item1")).thenReturn(
        Optional.of(new ShopItem("item1", "Testitem", "Item One", new BigDecimal("10.23"))));
    when(itemRepository.findById("item2")).thenReturn(
        Optional.of(new ShopItem("item2", "Testitem", "Item Two", new BigDecimal("20.99"))));

    Page<ShopItemHistoryEntryDTO> result = shopHistoryService.getHistory(0, 10, Optional.empty(),
        false);

    assertEquals(2, result.getContent().size());

    ShopItemHistoryEntryDTO first = result.getContent().get(0);
    assertEquals("User One", first.getUserDisplayName());
    assertEquals("user1", first.getUserId());
    assertEquals("Item One", first.getItemDisplayName());
    assertEquals("item1", first.getItemId());
    assertEquals(2, first.getAmount());
    assertEquals(new BigDecimal("10.23"), first.getItemPrice());
    assertNotNull(first.getTimestamp());

    ShopItemHistoryEntryDTO second = result.getContent().get(1);
    assertEquals("User Two", second.getUserDisplayName());
    assertEquals("user2", second.getUserId());
    assertEquals("Item Two", second.getItemDisplayName());
    assertEquals("item2", second.getItemId());
    assertEquals(4, second.getAmount());
    assertEquals(new BigDecimal("20.99"), second.getItemPrice());
    assertNotNull(second.getTimestamp());

    assertTrue(first.getTimestamp() < second.getTimestamp());
  }
}

