package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.data.dao.KdvUser;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import de.unipassau.fim.fsinfo.kdv.security.CustomUserDetailsContextMapper.CustomUserDetails;
import de.unipassau.fim.fsinfo.kdv.service.InvoiceService;
import de.unipassau.fim.fsinfo.kdv.service.ShopHistoryService;
import de.unipassau.fim.fsinfo.kdv.service.UserService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

  private final UserRepository userRepository;

  private final UserService userService;
  private final InvoiceService invoiceService;
  private final ShopHistoryService shopHistoryService;

  @Autowired
  public UserController(UserRepository userRepository, UserService userService,
      InvoiceService invoiceService, ShopHistoryService shopHistoryService) {
    this.userRepository = userRepository;
    this.userService = userService;
    this.invoiceService = invoiceService;
    this.shopHistoryService = shopHistoryService;
  }

  /**
   * List all Users
   *
   * @return List of all Users
   */
  @GetMapping("/info")
  public ResponseEntity<List<KdvUser>> list(@RequestParam(required = false) String id) {
    if (id == null) {
      return ResponseEntity.ok(userRepository.findAll());
    } else {
      Optional<KdvUser> user = userRepository.findById(id);
      return user.map(kdvUser -> ResponseEntity.ok(List.of(kdvUser)))
          .orElseGet(() -> ResponseEntity.badRequest().build());
    }
  }

  @GetMapping("/me")
  public ResponseEntity<KdvUser> me(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    Optional<KdvUser> user = userRepository.findById(userDetails.getUsername());
    return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @PostMapping("/create")
  public ResponseEntity<KdvUser> create(@RequestBody KdvUser userTemplate) {

    Optional<KdvUser> user = userService.createUser(userTemplate.getId(),
        userTemplate.getDisplayName(),
        userTemplate.getEmail());

    return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @DeleteMapping("/delete")
  public ResponseEntity<KdvUser> delete(@RequestParam String id) {
    Optional<KdvUser> user = userRepository.findById(id);

    if (user.isPresent()) {
      userRepository.delete(user.get());
      return ResponseEntity.ok(user.get());
    }

    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/name")
  public ResponseEntity<String> name(@RequestParam String id, @RequestParam String value) {
    Optional<KdvUser> user = userRepository.findById(id);

    try {
      if (user.isPresent()) {
        user.get().setDisplayName(value);
        userRepository.save(user.get());
        return ResponseEntity.ok().build();
      }
    } catch (NumberFormatException e) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/email")
  public ResponseEntity<String> email(@RequestParam String id, @RequestParam String value) {
    Optional<KdvUser> user = userRepository.findById(id);

    try {
      if (user.isPresent() && UserService.isValidEmail(value)) {
        user.get().setEmail(value);
        userRepository.save(user.get());
        return ResponseEntity.ok().build();
      }
    } catch (NumberFormatException e) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/balance")
  public ResponseEntity<BigDecimal> balance(@RequestParam String id, @RequestParam String value) {
    Optional<KdvUser> user = userRepository.findById(id);

    try {
      BigDecimal d = new BigDecimal(value);
      if (user.isPresent()) {
        user.get().setBalance(d);
        userRepository.save(user.get());
        return ResponseEntity.ok(user.get().getBalance());
      }
    } catch (NumberFormatException e) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/enable")
  public ResponseEntity<String> enable(@RequestParam String id) {
    Optional<KdvUser> user = userRepository.findById(id);

    if (user.isPresent()) {
      user.get().setEnabled(true);
      userRepository.save(user.get());
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/disable")
  public ResponseEntity<String> disable(@RequestParam String id) {
    Optional<KdvUser> user = userRepository.findById(id);

    if (user.isPresent()) {
      user.get().setEnabled(false);
      userRepository.save(user.get());
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.badRequest().build();
  }

}
