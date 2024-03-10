package de.unipassau.fim.fsinfo.kdv.service;

import de.unipassau.fim.fsinfo.kdv.data.dao.KdvUser;
import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.kdv.data.dto.ShopItemHistoryEntryDTO;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShopHistoryService {

  private final UserRepository userRepository;
  private final ShopItemRepository itemRepository;

  @Autowired
  public ShopHistoryService(UserRepository userRepository, ShopItemRepository itemRepository) {
    this.userRepository = userRepository;
    this.itemRepository = itemRepository;
  }

  public List<ShopItemHistoryEntryDTO> getDTO(List<ShopItemHistoryEntry> entryList) {
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

}
