package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.data.dao.ShopHistoryEntry;
import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.kdv.data.dao.User;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopHistoryRepository;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import de.unipassau.fim.fsinfo.kdv.service.FileStorageService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

  @Autowired
  FileStorageService fileStorageService;

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

  @GetMapping("/{id}")
  public ResponseEntity<Optional<ShopItem>> get(@PathVariable String id) {
    Optional<ShopItem> item = itemRepository.findById(id);
    if (item.isPresent()) {
      return ResponseEntity.ok(item);
    }
    return ResponseEntity.badRequest().build();
  }

  @DeleteMapping("/{id}/delete")
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
      @RequestParam String value) {
    Optional<ShopItem> item = itemRepository.findById(id);
    if (item.isPresent()) {
      item.get().setDisplayName(value);
      itemRepository.save(item.get());
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/{id}/category")
  public ResponseEntity<String> displayCategory(@PathVariable String id,
                                            @RequestParam String value) {
    Optional<ShopItem> item = itemRepository.findById(id);
    if (item.isPresent()) {
      item.get().setCategory(value);
      itemRepository.save(item.get());
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/{id}/price")
  public ResponseEntity<String> price(@PathVariable String id,
      @RequestParam String value) {
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

  @GetMapping("/{id}/display-picture")
  public ResponseEntity<MultipartFile> getDisplayImage(@PathVariable String id) {
    Optional<ShopItem> item = itemRepository.findById(id);
    if (item.isPresent()) {
      return ResponseEntity.ok(fileStorageService.getItemPicture(item.get().getId()));
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/{id}/display-picture")
  public ResponseEntity<String> setDisplayImage(@PathVariable String id,
      @RequestParam("file") MultipartFile file) {
    Optional<ShopItem> item = itemRepository.findById(id);
    if (item.isPresent()) {
      fileStorageService.saveItemPicture(item.get().getId(), file);
      return ResponseEntity.ok().build();
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
  public ResponseEntity<String> consume(@PathVariable String id, @RequestParam String userId,
      @RequestParam(required = false) Integer n) {
    Optional<ShopItem> itemO = itemRepository.findById(id);
    Optional<User> userO = userRepository.findById(userId);

    if (userO.isPresent() && itemO.isPresent()) {
      User user = userO.get();
      ShopItem item = itemO.get();

      if (!item.getEnabled()) {
        return ResponseEntity.badRequest().build();
      }

      for (int i = 0; i < (n == null ? 1 : n); i++) {
        user.setBalance(user.getBalance() - item.getPrice());
        userRepository.save(user);

        ShopHistoryEntry history = new ShopHistoryEntry(user.getId(), item.getId(),
            item.getPrice());
        historyRepository.save(history);
      }

      return ResponseEntity.ok(user.getBalance() + "");
    }
    return ResponseEntity.badRequest().build();
  }

}
