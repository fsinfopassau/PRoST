package de.unipassau.fim.fsinfo.prost.service;

import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.prost.data.dto.ShopItemHistoryEntryDTO;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import java.math.BigDecimal;
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

  /**
   * Returns the last history (as a Page) of the last bought items, beginning with the most recent.
   * Can either return a Page of the complete History or the History of a given user.
   *
   * @param pageNumber The page-index (0-n) of the searched dataset.
   * @param pageSize   The size of a page (1-n).
   * @param userId     The userId of the searched user-buy-history.
   * @return A page with the search-result according to the given parameters.
   */
  public Page<ShopItemHistoryEntryDTO> getHistory(
      int pageNumber, int pageSize, Optional<String> userId, boolean anonymizeHidden) {

    Pageable pageable = PageRequest.of(pageNumber, pageSize, desc);
    Page<ShopItemHistoryEntry> entriesPage;

    if (userId.isPresent()) {
      entriesPage = historyRepository.findByUserIdEquals(userId.get(), pageable);
    } else {
      entriesPage = historyRepository.findAll(pageable);
    }

    List<ShopItemHistoryEntryDTO> entryDTOs = entriesPage.getContent().stream()
        .map(entry -> this.getDTO(entry, anonymizeHidden))
        .collect(Collectors.toList());

    return new PageImpl<>(entryDTOs, pageable, entriesPage.getTotalElements());
  }

  private ShopItemHistoryEntryDTO getDTO(ShopItemHistoryEntry entry, boolean anonymizeHidden) {

    Optional<ProstUser> userO = userRepository.findById(entry.getUserId());
    Optional<ShopItem> itemO = itemRepository.findById(entry.getItemId());

    String userDisplayName =
        userO.isPresent() ? userO.get().getDisplayName() : entry.getUserId();
    String itemDisplayName =
        itemO.isPresent() ? itemO.get().getDisplayName() : entry.getItemId();
    boolean isHidden =
        userO.isPresent() && (userO.get().getHidden() == null || userO.get().getHidden());

    if (anonymizeHidden && isHidden) {
      return new ShopItemHistoryEntryDTO(entry.getId(), "", "Anonyme \uD83C\uDF4D",
          "", "",
          BigDecimal.ZERO,
          0,
          entry.getTimestamp(),
          null,
          isHidden);
    }

    return new ShopItemHistoryEntryDTO(entry.getId(), entry.getUserId(), userDisplayName,
        entry.getItemId(), itemDisplayName,
        entry.getItemPrice(),
        entry.getAmount(),
        entry.getTimestamp(),
        entry.getTransaction(),
        isHidden);
  }

}
