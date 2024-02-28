package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopHistoryRepository;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import de.unipassau.fim.fsinfo.kdv.service.FileStorageService;
import de.unipassau.fim.fsinfo.kdv.service.ShopService;
import java.io.File;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

  @Autowired
  FileStorageService fileStorageService;
  @Autowired
  ShopService shopService;

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

  @PostMapping("/create")
  public ResponseEntity<ShopItem> create(@RequestBody ShopItem item) {
    if (item.getId() == null || itemRepository.existsById(item.getId())
        || item.getDisplayName() == null
        || item.getDisplayName().isBlank()) {
      return ResponseEntity.badRequest().build();
    }

    if (item.getEnabled() == null) {
      item.setEnabled(true);
    }
    item.setPrice(0.0);

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
      return ResponseEntity.badRequest().body("NumberFormatException");
    }
    return ResponseEntity.badRequest().build();
  }

  @GetMapping("/{id}/picture")
  public ResponseEntity<FileSystemResource> getDisplayImage(@PathVariable String id) {
    Optional<ShopItem> item = itemRepository.findById(id);
    if (item.isPresent()) {

      Optional<File> file = fileStorageService.getItemPicture(item.get());
      if (file.isPresent()) {
        FileSystemResource resource = new FileSystemResource(file.get());

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.get().getName() + "\"")
            .body(resource);
      } else {
        return ResponseEntity.internalServerError().build();
      }
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/{id}/display-picture")
  public ResponseEntity<String> setDisplayImage(@PathVariable String id,
      @RequestParam("file") MultipartFile file) {
    Optional<ShopItem> item = itemRepository.findById(id);

    try {
      if (item.isPresent() && fileStorageService.saveItemPicture(item.get(), file)) {
        return ResponseEntity.ok().build();
      }
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
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
    if (shopService.consume(id, userId, (n == null ? 1 : n))) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

}
