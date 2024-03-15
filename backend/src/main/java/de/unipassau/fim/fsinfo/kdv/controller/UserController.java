package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.data.dao.KdvUser;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import de.unipassau.fim.fsinfo.kdv.service.InvoiceService;
import de.unipassau.fim.fsinfo.kdv.service.UserService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserRepository userRepository;

  private final UserService userService;
  private final InvoiceService invoiceService;

  @Autowired
  public UserController(UserRepository userRepository, UserService userService,
      InvoiceService invoiceService) {
    this.userRepository = userRepository;
    this.userService = userService;
    this.invoiceService = invoiceService;
  }

  /**
   * List all Users
   *
   * @return List of all Users
   */
  @GetMapping
  public ResponseEntity<List<KdvUser>> list(@RequestParam(required = false) String id) {
    if (id == null) {
      return ResponseEntity.ok(userRepository.findAll());
    } else {
      Optional<KdvUser> user = userRepository.findById(id);
      return user.map(kdvUser -> ResponseEntity.ok(List.of(kdvUser)))
          .orElseGet(() -> ResponseEntity.badRequest().build());
    }
  }

  @PostMapping("/create")
  public ResponseEntity<KdvUser> create(@RequestBody KdvUser userTemplate) {

    Optional<KdvUser> user = userService.createUser(userTemplate.getId(),
        userTemplate.getDisplayName(),
        userTemplate.getEmail());

    return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @DeleteMapping("/{id}/delete")
  public ResponseEntity<KdvUser> delete(@PathVariable String id) {
    Optional<KdvUser> user = userRepository.findById(id);

    if (user.isPresent()) {
      userRepository.delete(user.get());
      return ResponseEntity.ok(user.get());
    }

    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/{id}/balance")
  public ResponseEntity<BigDecimal> balance(@PathVariable String id, @RequestParam String value) {
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

  @PostMapping("/{id}/enable")
  public ResponseEntity<String> enable(@PathVariable String id) {
    Optional<KdvUser> user = userRepository.findById(id);

    if (user.isPresent()) {
      user.get().setEnabled(true);
      userRepository.save(user.get());
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/{id}/disable")
  public ResponseEntity<String> disable(@PathVariable String id) {
    Optional<KdvUser> user = userRepository.findById(id);

    if (user.isPresent()) {
      user.get().setEnabled(false);
      userRepository.save(user.get());
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.badRequest().build();
  }

}
