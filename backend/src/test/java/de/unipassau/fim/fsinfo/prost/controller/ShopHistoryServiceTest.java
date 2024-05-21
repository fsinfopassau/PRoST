package de.unipassau.fim.fsinfo.prost.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import de.unipassau.fim.fsinfo.prost.service.ShopHistoryService;
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
  public void testGetHistory() {
    // Mock data
    List<ShopItemHistoryEntry> historyEntries = new ArrayList<>();

    TransactionEntry transactionEntry = new TransactionEntry(null, "user1", "user1",
        TransactionType.BUY, null, BigDecimal.valueOf(10.0));
    TransactionEntry transactionEntry2 = new TransactionEntry(null, "user2", "user2",
        TransactionType.BUY, null, BigDecimal.valueOf(20.0));

    historyEntries.add(
        new ShopItemHistoryEntry(transactionEntry, "item1", BigDecimal.valueOf(10.0), 2));
    historyEntries.add(
        new ShopItemHistoryEntry(transactionEntry2, "item2", BigDecimal.valueOf(20.0), 4));
    Page<ShopItemHistoryEntry> page = new PageImpl<>(historyEntries);

    // Mock behavior of repositories
    when(historyRepository.findAll(
        PageRequest.of(0, 10, Sort.by("timestamp").descending()))).thenReturn(page);

    // Mock behavior of userRepository
    when(userRepository.findById("user1")).thenReturn(
        Optional.of(new ProstUser("user1", "User One", "test@test.com", true)));
    when(userRepository.findById("user2")).thenReturn(
        Optional.of(new ProstUser("user2", "User Two", "test@test.com", true)));

    // Mock behavior of itemRepository
    when(itemRepository.findById("item1")).thenReturn(
        Optional.of(new ShopItem("item1", "Testitem", "Item One", new BigDecimal("10.0"))));
    when(itemRepository.findById("item2")).thenReturn(
        Optional.of(new ShopItem("item2", "Testitem", "Item Two", new BigDecimal("20.0"))));

    // Call the method to test
    Page<ShopItemHistoryEntryDTO> result = shopHistoryService.getHistory(0, 10, null);

    // Assertions
    assertEquals(2, result.getContent().size());
    assertEquals("User One", result.getContent().get(0).getUserDisplayName());
    assertEquals("Item One", result.getContent().get(0).getItemDisplayName());
    assertEquals("User Two", result.getContent().get(1).getUserDisplayName());
    assertEquals("Item Two", result.getContent().get(1).getItemDisplayName());
  }
}

