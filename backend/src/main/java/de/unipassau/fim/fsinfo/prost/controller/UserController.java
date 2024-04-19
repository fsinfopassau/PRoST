package de.unipassau.fim.fsinfo.prost.controller;

import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.security.CustomUserDetailsContextMapper.CustomUserDetails;
import de.unipassau.fim.fsinfo.prost.service.TransactionService;
import de.unipassau.fim.fsinfo.prost.service.UserService;
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

  private final UserService userService;
  private final TransactionService transactionService;

  @Autowired
  public UserController(UserService userService, TransactionService transactionService) {
    this.userService = userService;
    this.transactionService = transactionService;
  }

  /**
   * List all Users
   *
   * @return List of all Users
   */
  @GetMapping("/info")
  public ResponseEntity<List<ProstUser>> list(@RequestParam(required = false) String id) {
    return userService.info(id).map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @GetMapping("/me")
  public ResponseEntity<ProstUser> me(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    Optional<List<ProstUser>> users = userService.info(userDetails.getUsername());
    return users.map(prostUsers -> ResponseEntity.ok(prostUsers.get(0)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/create")
  public ResponseEntity<ProstUser> create(@RequestBody ProstUser userTemplate) {

    Optional<ProstUser> user = userService.createUser(userTemplate.getId(),
        userTemplate.getDisplayName(),
        userTemplate.getEmail());

    return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @DeleteMapping("/delete")
  public ResponseEntity<String> delete(@RequestParam String id) {
    if (userService.delete(id)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/name")
  public ResponseEntity<String> name(@RequestParam String id, @RequestParam String value) {
    if (userService.rename(id, value)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/email")
  public ResponseEntity<String> email(@RequestParam String id, @RequestParam String value) {
    if (userService.setEmail(id, value)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/enable")
  public ResponseEntity<String> enable(@RequestParam String id) {
    if (userService.setEnabled(id, true)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/disable")
  public ResponseEntity<String> disable(@RequestParam String id) {
    if (userService.setEnabled(id, false)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

}
