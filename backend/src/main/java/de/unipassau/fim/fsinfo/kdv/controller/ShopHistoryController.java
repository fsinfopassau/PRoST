package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItemHistoryEntry;
import de.unipassau.fim.fsinfo.kdv.data.dto.ShopItemHistoryEntryDTO;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopItemHistoryRepository;
import de.unipassau.fim.fsinfo.kdv.service.ShopHistoryService;
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

  private final ShopItemHistoryRepository historyRepository;
  private final ShopHistoryService shopHistoryService;

  @Autowired
  public ShopHistoryController(ShopItemHistoryRepository historyRepository,
      ShopHistoryService shopHistoryService) {
    this.historyRepository = historyRepository;
    this.shopHistoryService = shopHistoryService;
  }

  @GetMapping
  public ResponseEntity<List<ShopItemHistoryEntryDTO>> history(
      @RequestParam(required = false) Integer n) {
    return ResponseEntity.ok(
        shopHistoryService.getDTO(getSizedHistory(n, historyRepository.findAll())));
  }

  @GetMapping("/{userId}")
  public ResponseEntity<List<ShopItemHistoryEntryDTO>> historyUser(@PathVariable String userId,
      @RequestParam(required = false) Integer n) {
    return ResponseEntity.ok(
        shopHistoryService.getDTO(
            getSizedHistory(n, historyRepository.findByUserIdEquals(userId))));
  }

  private List<ShopItemHistoryEntry> getSizedHistory(
      @RequestParam(required = false) Integer amount,
      List<ShopItemHistoryEntry> history) {
    if (amount == null || amount < 0) {
      return history;
    }

    if (history.size() <= amount) {
      return history;
    } else {
      return history.subList(history.size() - amount, history.size());
    }
  }
}
