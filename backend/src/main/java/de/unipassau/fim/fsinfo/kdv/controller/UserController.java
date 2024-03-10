package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.data.UserRole;
import de.unipassau.fim.fsinfo.kdv.data.dao.KdvUser;
import de.unipassau.fim.fsinfo.kdv.data.dto.InvoiceDTO;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import de.unipassau.fim.fsinfo.kdv.service.InvoiceService;
import de.unipassau.fim.fsinfo.kdv.service.UserService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserService userService;
  @Autowired
  InvoiceService invoiceService;

  /**
   * List all Users
   *
   * @return List of all Users
   */
  @GetMapping
  public ResponseEntity<List<KdvUser>> list() {
    return ResponseEntity.ok(userRepository.findAll());
  }

  /**
   * Create a new User
   *
   * @param user with a unique username
   * @return the ID of the new user - nothing if User can not be created
   */
  @PostMapping("/create")
  public ResponseEntity<String> create(@RequestBody KdvUser user) {
    if (userService.createUser(user)) {
      return ResponseEntity.ok(user.getId());
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<KdvUser> get(@PathVariable String id) {
    Optional<KdvUser> user = userRepository.findById(id);
    return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @GetMapping("/{id}/invoices")
  public Page<InvoiceDTO> getInvoices(
      @PathVariable String id,
      @RequestParam(defaultValue = "0") int p,
      @RequestParam(defaultValue = "10") int s,
      @RequestParam(required = false) Boolean mailed) {
    return invoiceService.getInvoices(Math.max(0, p), Math.max(1, s), mailed, id);
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

  @PostMapping("/{id}/role")
  public ResponseEntity<String> role(@PathVariable String id, @RequestParam UserRole role) {
    Optional<KdvUser> user = userRepository.findById(id);

    if (user.isPresent()) {
      user.get().setRole(role);
      userRepository.save(user.get());
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.badRequest().build();
  }

}
