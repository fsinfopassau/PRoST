package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.data.dao.ShopHistory;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopHistoryRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shop/history")
public class ShopHistoryController {

  @Autowired
  ShopHistoryRepository historyRepository;

  @GetMapping
  public ResponseEntity<List<ShopHistory>> history() {
    return ResponseEntity.ok(historyRepository.findAll());
  }

  @GetMapping("/{userId}")
  public ResponseEntity<List<ShopHistory>> historyUser(@PathVariable String userId) {
    return ResponseEntity.ok(historyRepository.findByUsernameEquals(userId));
  }

}
