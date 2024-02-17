package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.data.dao.ShopHistory;
import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.kdv.data.dao.User;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopHistoryRepository;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

  @Autowired
  ShopItemRepository itemRepository;
  @Autowired
  ShopHistoryRepository historyRepository;
  @Autowired
  UserRepository userRepository;

  @GetMapping
  public ResponseEntity<List<ShopItem>> list() {
    return ResponseEntity.ok(itemRepository.findAll());
  }

  @PostMapping
  public ResponseEntity<ShopItem> create(@RequestBody ShopItem item) {
    if (item.getId() == null || itemRepository.existsById(item.getId())
        || item.getDisplayName() == null
        || item.getDisplayName().isBlank()) {
      return ResponseEntity.badRequest().build();
    }

    if (item.getEnabled() == null) {
      item.setEnabled(true);
    }

    if (item.getPrice() == null) {
      item.setPrice(0.0);
    }

    itemRepository.save(item);
    return ResponseEntity.ok(item);
  }

  @PostMapping("/{id}/delete")
  public ResponseEntity<Optional<ShopItem>> delete(@PathVariable String id) {
    Optional<ShopItem> item = itemRepository.findById(id);
    if (item.isPresent()) {
      itemRepository.delete(item.get());
      return ResponseEntity.ok(item);
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/{id}/displayname")
  public ResponseEntity<String> displayName(@PathVariable String id,
      @RequestBody String displayName) {
    Optional<ShopItem> item = itemRepository.findById(id);
    if (item.isPresent()) {
      item.get().setDisplayName(displayName);
      itemRepository.save(item.get());
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/{id}/price")
  public ResponseEntity<String> price(@PathVariable String id,
      @RequestBody String value) {
    Optional<ShopItem> item = itemRepository.findById(id);
    try {
      Double price = Double.parseDouble(value);
      if (item.isPresent()) {
        item.get().setPrice(price);
        itemRepository.save(item.get());
        return ResponseEntity.ok().build();
      }
    } catch (NumberFormatException e) {
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/{id}/enable")
  public ResponseEntity<String> enable(@PathVariable String id) {
    Optional<ShopItem> item = itemRepository.findById(id);
    if (item.isPresent()) {
      item.get().setEnabled(true);
      itemRepository.save(item.get());
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/{id}/disable")
  public ResponseEntity<String> disable(@PathVariable String id) {
    Optional<ShopItem> item = itemRepository.findById(id);
    if (item.isPresent()) {
      item.get().setEnabled(false);
      itemRepository.save(item.get());
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/{id}/consume")
  public ResponseEntity<String> consume(@PathVariable String id, @RequestBody String userId,
      @RequestParam(required = false) Integer n) {
    Optional<ShopItem> itemOptional = itemRepository.findById(id);
    Optional<User> userOption = userRepository.findById(userId);

    if (userOption.isPresent() && itemOptional.isPresent()) {
      User user = userOption.get();
      ShopItem item = itemOptional.get();

      for (int i = 0; i < (n == null ? 1 : n); i++) {
        user.setBalance(user.getBalance() - item.getPrice());
        userRepository.save(user);

        ShopHistory history = new ShopHistory(user.getUsername(), item.getId(), item.getPrice());
        historyRepository.save(history);
      }

      return ResponseEntity.ok(user.getBalance() + "");
    }
    return ResponseEntity.badRequest().build();
  }

}
