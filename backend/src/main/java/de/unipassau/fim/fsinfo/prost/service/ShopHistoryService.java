package de.unipassau.fim.fsinfo.prost.service;

import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.prost.data.dto.ShopItemHistoryEntryDTO;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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

  private List<ShopItemHistoryEntryDTO> getDTO(Iterable<ShopItemHistoryEntry> entryList) {
    List<ShopItemHistoryEntryDTO> entryDTOs = new ArrayList<>();
    if (entryList != null) {
      for (ShopItemHistoryEntry entry : entryList) {

        Optional<ProstUser> userO = userRepository.findById(entry.getUserId());
        Optional<ShopItem> itemO = itemRepository.findById(entry.getItemId());

        String userDisplayName =
            userO.isPresent() ? userO.get().getDisplayName() : entry.getUserId();
        String itemDisplayName =
            itemO.isPresent() ? itemO.get().getDisplayName() : entry.getItemId();

        entryDTOs.add(
            new ShopItemHistoryEntryDTO(entry.getId(), entry.getUserId(), userDisplayName,
                entry.getItemId(), itemDisplayName,
                entry.getItemPrice(),
                entry.getAmount(),
                entry.getTimestamp(),
                entry.getTransaction()));
      }
    }
    return entryDTOs;
  }

  public Optional<List<ShopItemHistoryEntryDTO>> getLastUserHistory(Integer n, String userId) {

    Optional<ProstUser> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      return Optional.empty();
    }

    if (n == null) {
      return Optional.of(getDTO(historyRepository.findAll(desc)));
    }

    Pageable pageable = PageRequest.of(0, n, desc);
    return Optional.of(getDTO(historyRepository.findByUserIdEquals(userId, pageable)));
  }

  public List<ShopItemHistoryEntryDTO> getLastHistory(Integer n) {
    if (n == null) {
      return getDTO(historyRepository.findAll(desc));
    }
    Pageable pageable = PageRequest.of(0, n, desc);
    return getDTO(historyRepository.findAll(pageable));
  }
}
