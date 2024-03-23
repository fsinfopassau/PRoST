package de.unipassau.fim.fsinfo.kdv.service;

import de.unipassau.fim.fsinfo.kdv.data.dao.KdvUser;
import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.kdv.data.dto.ShopItemHistoryEntryDTO;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
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
  private final Sort desc;

  @Autowired
  public ShopHistoryService(UserRepository userRepository, ShopItemRepository itemRepository,
      ShopItemHistoryRepository historyRepository) {
    this.userRepository = userRepository;
    this.itemRepository = itemRepository;
    this.historyRepository = historyRepository;
    desc = Sort.by("timestamp").descending();
  }

  private List<ShopItemHistoryEntryDTO> getDTO(Iterable<ShopItemHistoryEntry> entryList) {
    List<ShopItemHistoryEntryDTO> entryDTOs = new ArrayList<>();
    if (entryList != null) {
      for (ShopItemHistoryEntry entry : entryList) {

        Optional<KdvUser> userO = userRepository.findById(entry.getUserId());
        Optional<ShopItem> itemO = itemRepository.findById(entry.getItemId());

        if (userO.isPresent() && itemO.isPresent()) {
          String userDisplayName = userO.get().getDisplayName();
          String itemDisplayName = itemO.get().getDisplayName();

          entryDTOs.add(
              new ShopItemHistoryEntryDTO(entry.getId(), entry.getUserId(), userDisplayName,
                  entry.getItemId(), itemDisplayName,
                  entry.getPrice(),
                  entry.getAmount(),
                  entry.getTimestamp()));
        }
      }
    }
    return entryDTOs;
  }

  public List<ShopItemHistoryEntryDTO> getLastUserHistory(Integer n, String userId) {
    if (n == null) {
      return getDTO(historyRepository.findAll(desc));
    }
    Pageable pageable = PageRequest.of(0, n, desc);
    return getDTO(historyRepository.findByUserIdEquals(userId, pageable));
  }

  public List<ShopItemHistoryEntryDTO> getLastHistory(Integer n) {
    if (n == null) {
      return getDTO(historyRepository.findAll(desc));
    }
    Pageable pageable = PageRequest.of(0, n, desc);
    return getDTO(historyRepository.findAll(pageable));
  }
}
