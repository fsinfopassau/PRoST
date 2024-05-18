package de.unipassau.fim.fsinfo.prost.service;

import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.prost.data.dto.ShopItemHistoryEntryDTO;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import java.util.List;
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
public class ShopHistoryService {

  private final UserRepository userRepository;
  private final ShopItemRepository itemRepository;
  private final ShopItemHistoryRepository historyRepository;
  private final Sort desc = Sort.by("timestamp").descending();

  @Autowired
  public ShopHistoryService(UserRepository userRepository, ShopItemRepository itemRepository,
      ShopItemHistoryRepository historyRepository) {
    this.userRepository = userRepository;
    this.itemRepository = itemRepository;
    this.historyRepository = historyRepository;
  }

  public Page<ShopItemHistoryEntryDTO> getHistory(
      int pageNumber, int pageSize, String userId) {

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("timestamp").descending());
    Page<ShopItemHistoryEntry> entriesPage;

    if (userId != null) {
      entriesPage = historyRepository.findByUserIdEquals(userId, pageable);
    } else {
      entriesPage = historyRepository.findAll(pageable);
    }

    List<ShopItemHistoryEntryDTO> entryDTOs = entriesPage.getContent().stream()
        .map(this::getDTO)
        .collect(Collectors.toList());

    return new PageImpl<>(entryDTOs, pageable, entriesPage.getTotalElements());
  }

  private ShopItemHistoryEntryDTO getDTO(ShopItemHistoryEntry entry) {
    Optional<ProstUser> userO = userRepository.findById(entry.getUserId());
    Optional<ShopItem> itemO = itemRepository.findById(entry.getItemId());

    String userDisplayName =
        userO.isPresent() ? userO.get().getDisplayName() : entry.getUserId();
    String itemDisplayName =
        itemO.isPresent() ? itemO.get().getDisplayName() : entry.getItemId();

    return new ShopItemHistoryEntryDTO(entry.getId(), entry.getUserId(), userDisplayName,
        entry.getItemId(), itemDisplayName,
        entry.getItemPrice(),
        entry.getAmount(),
        entry.getTimestamp(),
        entry.getTransaction());
  }

}
