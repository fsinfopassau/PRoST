package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.data.UserAccessRole;
import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.kdv.security.CustomUserDetailsContextMapper.CustomUserDetails;
import de.unipassau.fim.fsinfo.kdv.service.AuthenticationService;
import de.unipassau.fim.fsinfo.kdv.service.FileStorageService;
import de.unipassau.fim.fsinfo.kdv.service.ShopService;
import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

  private final FileStorageService fileStorageService;
  private final ShopService shopService;
  private final AuthenticationService authService;

  private final ShopItemRepository itemRepository;

  @Autowired
  public ShopController(FileStorageService fileStorageService, ShopService shopService,
      ShopItemRepository itemRepository, AuthenticationService authService) {
    this.fileStorageService = fileStorageService;
    this.shopService = shopService;
    this.itemRepository = itemRepository;
    this.authService = authService;
  }

  @GetMapping("/item/list")
  public ResponseEntity<List<ShopItem>> list() {
    return ResponseEntity.ok(itemRepository.findAll());
  }

  @GetMapping("/item/info")
  public ResponseEntity<Optional<ShopItem>> get(@RequestParam String id) {
    Optional<ShopItem> item = itemRepository.findById(id);
    if (item.isPresent()) {
      return ResponseEntity.ok(item);
    }
    return ResponseEntity.badRequest().build();
  }

  @GetMapping("/item/picture")
  public ResponseEntity<FileSystemResource> getDisplayImage(@RequestParam String id) {
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
        return ResponseEntity.noContent().build();
      }
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/item/consume")
  public ResponseEntity<String> consume(@RequestParam String id, @RequestParam String userId,
      @RequestParam(required = false) Integer n, Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    Collection<UserAccessRole> roles = authService.getRoles(authentication);

    boolean permited =
        roles.contains(UserAccessRole.KIOSK) || roles.contains(UserAccessRole.KAFFEEKASSE);

    if (!permited && roles.contains(UserAccessRole.FSINFO)) { // Check if user is buying for himself
      permited = userId.equals(userDetails.getUsername());
    }

    if (permited && shopService.consume(id, userId,
        (n == null ? 1 : n))) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.status(403).body("No buying for others...");
  }

  @PostMapping("/settings/create")
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
      item.setPrice(new BigDecimal(0));
    }

    itemRepository.save(item);
    return ResponseEntity.ok(item);
  }

  @DeleteMapping("/settings/item/delete")
  public ResponseEntity<Optional<ShopItem>> delete(@RequestParam String id) {
    Optional<ShopItem> item = itemRepository.findById(id);
    if (item.isPresent()) {
      itemRepository.delete(item.get());
      return ResponseEntity.ok(item);
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/settings/item/displayname")
  public ResponseEntity<String> displayName(@RequestParam String id,
      @RequestParam String value) {
    Optional<ShopItem> item = itemRepository.findById(id);
    if (item.isPresent()) {
      item.get().setDisplayName(value);
      itemRepository.save(item.get());
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/settings/item/category")
  public ResponseEntity<String> displayCategory(@RequestParam String id,
      @RequestParam String value) {
    Optional<ShopItem> item = itemRepository.findById(id);
    if (item.isPresent()) {
      item.get().setCategory(value);
      itemRepository.save(item.get());
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/settings/item/price")
  public ResponseEntity<String> price(@RequestParam String id,
      @RequestParam String value) {
    Optional<ShopItem> item = itemRepository.findById(id);
    try {
      BigDecimal price = new BigDecimal(value);
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

  @PostMapping("/settings/item/picture")
  public ResponseEntity<String> setDisplayImage(@RequestParam String id,
      @RequestParam("file") MultipartFile file) {
    Optional<ShopItem> item = itemRepository.findById(id);

    try {
      if (item.isPresent() && fileStorageService.saveItemPicture(item.get(), file)) {
        return ResponseEntity.ok().build();
      }
    } catch (Exception e) {
      System.err.println(e.getMessage() + " " + Arrays.toString(e.getStackTrace()));
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/settings/item/enable")
  public ResponseEntity<String> enable(@RequestParam String id) {
    Optional<ShopItem> item = itemRepository.findById(id);
    if (item.isPresent()) {
      item.get().setEnabled(true);
      itemRepository.save(item.get());
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/settings/item/disable")
  public ResponseEntity<String> disable(@RequestParam String id) {
    Optional<ShopItem> item = itemRepository.findById(id);
    if (item.isPresent()) {
      item.get().setEnabled(false);
      itemRepository.save(item.get());
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

}
