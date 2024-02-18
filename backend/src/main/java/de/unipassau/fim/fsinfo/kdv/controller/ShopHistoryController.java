package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.data.dao.ShopHistoryEntry;
import de.unipassau.fim.fsinfo.kdv.data.dto.ShopHistoryEntryDTO;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopHistoryRepository;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/history")
public class ShopHistoryController {

  @Autowired
  ShopHistoryRepository historyRepository;
  @Autowired
  ShopItemRepository itemRepository;
  @Autowired
  UserRepository userRepository;

  @GetMapping
  public ResponseEntity<List<ShopHistoryEntryDTO>> history(@RequestParam(required = false) Integer n) {
    return ResponseEntity.ok(getDTO(getSizedHistory(n, historyRepository.findAll())));
  }

  @GetMapping("/{userId}")
  public ResponseEntity<List<ShopHistoryEntryDTO>> historyUser(@PathVariable String userId,
      @RequestParam(required = false) Integer n) {
    return ResponseEntity.ok(getDTO(getSizedHistory(n, historyRepository.findByUserIdEquals(userId))));
  }

  private List<ShopHistoryEntry> getSizedHistory(
      @RequestParam(required = false) Integer amount,
      List<ShopHistoryEntry> history) {
    if (amount == null || amount < 0) {
      return history;
    }

    if (history.size() <= amount) {
      return history;
    } else {
      return history.subList(history.size() - amount, history.size());
    }
  }

  private List<ShopHistoryEntryDTO> getDTO(List<ShopHistoryEntry> entryList){
    List<ShopHistoryEntryDTO> entryDTOs = new ArrayList<>();
    for(ShopHistoryEntry entry : entryList) {

      String userDisplayName = userRepository.findById(entry.getUserId()).get().getDisplayName();
      String itemDisplayName = itemRepository.findById(entry.getItemId()).get().getDisplayName();

      entryDTOs.add(new ShopHistoryEntryDTO(entry.getId(), entry.getUserId(), userDisplayName, entry.getItemId(), itemDisplayName,
          entry.getPrice(),
          entry.getTimestamp()));
    }
    return entryDTOs;
  }

}
