package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.data.dao.ShopHistoryEntry;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopHistoryRepository;
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

  @GetMapping
  public ResponseEntity<List<ShopHistoryEntry>> history(@RequestParam(required = false) Integer n) {
    return getSizedHistory(n, historyRepository.findAll());
  }

  @GetMapping("/{userId}")
  public ResponseEntity<List<ShopHistoryEntry>> historyUser(@PathVariable String userId,
      @RequestParam(required = false) Integer n) {
    return getSizedHistory(n, historyRepository.findByUsernameEquals(userId));
  }

  private ResponseEntity<List<ShopHistoryEntry>> getSizedHistory(
      @RequestParam(required = false) Integer amount,
      List<ShopHistoryEntry> history) {
    if (amount == null || amount < 0) {
      return ResponseEntity.ok(history);
    }

    if (history.size() <= amount) {
      return ResponseEntity.ok(history);
    } else {
      return ResponseEntity.ok(history.subList(history.size() - amount, history.size()));
    }
  }

}
