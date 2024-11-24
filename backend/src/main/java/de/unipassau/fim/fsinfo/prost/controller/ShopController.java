package de.unipassau.fim.fsinfo.prost.controller;

import de.unipassau.fim.fsinfo.prost.data.UserAccessRole;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.prost.security.CustomUserDetailsContextMapper.CustomUserDetails;
import de.unipassau.fim.fsinfo.prost.service.AuthenticationService;
import de.unipassau.fim.fsinfo.prost.service.FileStorageService;
import de.unipassau.fim.fsinfo.prost.service.ShopService;
import de.unipassau.fim.fsinfo.prost.service.UserService;
import java.io.File;
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
  private final UserService userService;

  @Autowired
  public ShopController(FileStorageService fileStorageService, ShopService shopService,
      ShopItemRepository itemRepository, AuthenticationService authService,
      UserService userService) {
    this.fileStorageService = fileStorageService;
    this.shopService = shopService;
    this.itemRepository = itemRepository;
    this.authService = authService;
    this.userService = userService;
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
    if (authentication == null) {
      return ResponseEntity.badRequest().build();
    }
    n = (n == null ? 1 : n);

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String bearerId = userDetails.getUsername();

    Collection<UserAccessRole> roles = authService.getRoles(authentication);

    Optional<UserAccessRole> highestPermission = authService.getHighestRole(roles);

    if (shopService.hasBearerCooldown(bearerId)) {
      return ResponseEntity.badRequest().body("on cooldown");
    }

    if (highestPermission.isEmpty()) {
      return ResponseEntity.badRequest().body("No highest permission found!");
    }

    if (!shopService.hasBearerPermissions(id, userId, n, bearerId, highestPermission.get())) {
      if (highestPermission.get() == UserAccessRole.FSINFO) {
        // 🙃🫖
        return ResponseEntity.status(418)
            .body("I'm a teapot, and you're not an admin or kiosk! \uD83D\uDE43");
      } else {
        return ResponseEntity.badRequest().body("No Permissions!");
      }
    }

    if (shopService.consume(id, userId,
        n, bearerId, highestPermission.get())) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().body("Could not consume!");

  }

  @PostMapping("/settings/create")
  public ResponseEntity<ShopItem> create(@RequestBody ShopItem itemTemplate) {
    Optional<ShopItem> shopItem = shopService.createItem(itemTemplate.getId(),
        itemTemplate.getDisplayName(),
        itemTemplate.getCategory(), itemTemplate.getPrice());

    return shopItem.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @DeleteMapping("/settings/item/delete")
  public ResponseEntity<Optional<ShopItem>> delete(@RequestParam String id) {
    Optional<ShopItem> shopItem = shopService.delete(id);

    if (shopItem.isPresent()) {
      return ResponseEntity.ok(shopItem);
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/settings/item/displayname")
  public ResponseEntity<String> displayName(@RequestParam String id,
      @RequestParam String value) {
    Optional<ShopItem> shopItem = shopService.changeDisplayName(id, value);

    if (shopItem.isPresent()) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/settings/item/category")
  public ResponseEntity<String> displayCategory(@RequestParam String id,
      @RequestParam String value) {
    Optional<ShopItem> shopItem = shopService.changeCategory(id, value);

    if (shopItem.isPresent()) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/settings/item/price")
  public ResponseEntity<String> price(@RequestParam String id,
      @RequestParam String value) {
    Optional<ShopItem> shopItem = shopService.changePrice(id, value);

    if (shopItem.isPresent()) {
      return ResponseEntity.ok().build();
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
    Optional<ShopItem> shopItem = shopService.enable(id);
    if (shopItem.isPresent()) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/settings/item/disable")
  public ResponseEntity<String> disable(@RequestParam String id) {
    Optional<ShopItem> shopItem = shopService.disable(id);
    if (shopItem.isPresent()) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

}
