package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.UserRole;
import de.unipassau.fim.fsinfo.kdv.data.dao.User;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import de.unipassau.fim.fsinfo.kdv.service.UserAuthService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

  @Autowired
  UserAuthService userAuthService;

  @Autowired
  UserRepository userRepository;

  /**
   * List all Users
   *
   * @return List of all Users
   */
  @GetMapping
  public ResponseEntity<List<User>> list() {
    return ResponseEntity.ok(userRepository.findAll());
  }

  /**
   * Create a new User
   *
   * @param user with a unique username
   * @return the ID of the new user - nothing if User can not be created
   */
  @PostMapping("/create")
  public ResponseEntity<String> create(@RequestBody User user) {

    if (userRepository.findById(user.getUsername()).isPresent()) {
      return ResponseEntity.badRequest().build();
    }

    try {
      userRepository.save(user);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }

    return ResponseEntity.ok(user.getUsername());
  }

  @PostMapping("/{id}/delete")
  public ResponseEntity<String> delete(@PathVariable String id) {
    // TODO
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/{id}/balance")
  public ResponseEntity<String> balance(@PathVariable String id, @RequestBody String value) {
    // TODO
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/{id}/enable")
  public ResponseEntity<String> enable(@PathVariable String id) {
    // TODO
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/{id}/disable")
  public ResponseEntity<String> disable(@PathVariable String id) {
    // TODO
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/{id}/role")
  public ResponseEntity<String> role(@PathVariable String id, @RequestBody UserRole role) {
    // TODO
    return ResponseEntity.badRequest().build();
  }

}
